package uk.gov.defra.cdp.trade.demo.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;

/**
 * Configures a DataSource with AWS RDS IAM authentication.
 *
 * This configuration:
 * - Generates short-lived authentication tokens from AWS RDS (valid for 15 minutes)
 * - Proactively refreshes tokens every 12 minutes via scheduled task
 * - Configures HikariCP to refresh tokens before they expire
 * - Enables SSL/TLS connections to RDS using custom SSLContext
 * - Only activates when aws.rds.iam.enabled=true
 *
 * Token refresh is handled both by scheduled refresh and HikariCP's connection
 * validation to ensure tokens never expire during active connections.
 */
@Configuration
@ConditionalOnProperty(name = "aws.rds.iam.enabled", havingValue = "true")
@EnableScheduling
@Slf4j
public class RdsIamDataSourceConfiguration {

  @Value("${spring.datasource.hostname}")
  private String hostname;

  @Value("${spring.datasource.port}")
  private int port;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.database}")
  private String database;

  @Value("${aws.region}")
  private String awsRegion;

  private final SSLContext sslContext;
  private HikariDataSource dataSource;
  private volatile String currentToken;
  private volatile LocalDateTime tokenExpiryTime;
  private final ReentrantReadWriteLock tokenLock = new ReentrantReadWriteLock();

  public RdsIamDataSourceConfiguration(SSLContext sslContext) {
    this.sslContext = sslContext;
  }

  /**
   * Creates a HikariCP DataSource configured for AWS RDS IAM authentication.
   *
   * Key configuration:
   * - max-lifetime: 10 minutes (tokens valid for 15, refresh before expiry)
   * - connection-timeout: 20 seconds (allows time for token generation)
   * - SSL enabled with custom SSLContext (includes RDS certificates)
   */
  @Bean
  @Primary
  public DataSource dataSource() {
    log.info("Configuring RDS IAM authentication for database: {}@{}:{}/{}",
        username, hostname, port, database);

    HikariConfig config = new HikariConfig();

    // JDBC URL with SSL enabled
    String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?ssl=true&sslmode=require",
        hostname, port, database);
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);

    // Initialize token tracking
    tokenLock.writeLock().lock();
    try {
      currentToken = generateAuthToken();
      tokenExpiryTime = LocalDateTime.now().plusMinutes(15);
      config.setPassword(currentToken);
    } finally {
      tokenLock.writeLock().unlock();
    }

    // Force connection refresh every 10 minutes (tokens expire after 15)
    config.setMaxLifetime(600000); // 10 minutes in milliseconds

    // Connection pool settings
    config.setMaximumPoolSize(10);
    config.setConnectionTimeout(20000); // 20 seconds
    config.setIdleTimeout(300000); // 5 minutes

    // Validate connections to ensure token freshness
    config.setConnectionTestQuery("SELECT 1");
    config.setValidationTimeout(3000);

    // PostgreSQL driver properties for SSL with custom certificate trust
    Properties props = new Properties();
    props.setProperty("sslfactory", "uk.gov.defra.cdp.trade.demo.configuration.CustomSSLSocketFactory");
    config.setDataSourceProperties(props);

    // Configure custom SSLContext for CustomSSLSocketFactory
    CustomSSLSocketFactory.setCustomSslContext(sslContext);

    this.dataSource = new HikariDataSource(config);
    log.info("RDS IAM DataSource configured successfully");

    return this.dataSource;
  }

  /**
   * Scheduled task to refresh RDS IAM authentication token before expiry.
   * Runs every 12 minutes (720 seconds) to refresh tokens before the 15-minute expiry.
   * Forces HikariCP pool to evict connections and reconnect with the new token.
   */
  @Scheduled(fixedRate = 720000) // 12 minutes in milliseconds
  public void refreshAuthToken() {
    if (dataSource == null) {
      log.debug("DataSource not yet initialized, skipping token refresh");
      return;
    }

    tokenLock.readLock().lock();
    try {
      LocalDateTime now = LocalDateTime.now();
      if (tokenExpiryTime != null && now.isBefore(tokenExpiryTime.minusMinutes(3))) {
        log.debug("Token still valid for more than 3 minutes, skipping refresh");
        return;
      }
    } finally {
      tokenLock.readLock().unlock();
    }

    log.info("Refreshing RDS IAM authentication token proactively");

    tokenLock.writeLock().lock();
    try {
      String newToken = generateAuthToken();
      currentToken = newToken;
      tokenExpiryTime = LocalDateTime.now().plusMinutes(15);
      
      // Force HikariCP to refresh connections with new token
      dataSource.getHikariConfigMXBean().setPassword(newToken);
      dataSource.getHikariPoolMXBean().softEvictConnections();
      dataSource.getHikariPoolMXBean().resumePool();
      
      log.info("Successfully refreshed RDS IAM authentication token");
      
    } catch (Exception e) {
      log.error("Failed to refresh RDS IAM authentication token: {}", e.getMessage(), e);
    } finally {
      tokenLock.writeLock().unlock();
    }
  }

  /**
   * Generates an AWS RDS IAM authentication token.
   *
   * Tokens are:
   * - Valid for 15 minutes
   * - Generated using IAM credentials (no password needed)
   * - Encrypted in transit
   * - Automatically rotated by scheduled refresh and HikariCP's connection refresh
   */
  private String generateAuthToken() {
    log.debug("Generating RDS IAM authentication token for {}@{}:{}",
        username, hostname, port);

    try {
      RdsUtilities rdsUtilities = RdsUtilities.builder()
          .region(Region.of(awsRegion))
          .credentialsProvider(DefaultCredentialsProvider.create())
          .build();

      GenerateAuthenticationTokenRequest request = GenerateAuthenticationTokenRequest.builder()
          .hostname(hostname)
          .port(port)
          .username(username)
          .build();

      String authToken = rdsUtilities.generateAuthenticationToken(request);
      log.debug("Successfully generated RDS IAM authentication token");
      return authToken;

    } catch (Exception e) {
      log.error("Failed to generate RDS IAM authentication token: {}", e.getMessage(), e);
      throw new IllegalStateException("Cannot generate RDS authentication token", e);
    }
  }
}
