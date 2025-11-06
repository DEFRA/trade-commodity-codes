package uk.gov.defra.cdp.trade.demo.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.sql.DataSource;

/**
 * PostgreSQL DataSource configuration with custom SSL support.
 * 
 * This configuration creates a HikariCP connection pool that uses the custom SSLContext
 * from TrustStoreConfiguration for secure connections to PostgreSQL.
 * 
 * The SSL configuration is applied by setting the global default SSLContext
 * which PostgreSQL JDBC driver will use for SSL connections.
 */
@Configuration
@Slf4j
public class PostgreSQLDataSourceConfig {

    @Autowired
    private SSLContext customSslContext;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username:postgres}")
    private String username;

    @Value("${spring.datasource.password:postgres}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.max-lifetime:600000}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    /**
     * Creates the primary PostgreSQL DataSource with custom SSL support.
     * 
     * This DataSource is only created when SSL is required (e.g., in production).
     * For local development without SSL, Spring Boot's auto-configuration is used.
     */
    @Bean
    @Primary
    @ConditionalOnProperty(
        name = "spring.datasource.ssl.enabled", 
        havingValue = "true", 
        matchIfMissing = false
    )
    public DataSource postgresDataSource() {
        log.info("Configuring PostgreSQL DataSource with custom SSL context");

        // Set the global default SSLContext for JDBC connections
        // PostgreSQL JDBC driver will use this for SSL connections
        SSLContext.setDefault(customSslContext);
        HttpsURLConnection.setDefaultSSLSocketFactory(customSslContext.getSocketFactory());

        HikariConfig config = new HikariConfig();
        
        // Basic connection settings with SSL enabled
        config.setJdbcUrl(buildSslJdbcUrl(jdbcUrl));
        if (username != null) {
          config.setUsername(username);
        }
        if (password != null) {
          config.setPassword(password);
        }
        config.setDriverClassName("org.postgresql.Driver");

        // Connection pool settings
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setMaxLifetime(maxLifetime);
        config.setConnectionTimeout(connectionTimeout);

        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);

        // Pool name for monitoring
        config.setPoolName("PostgreSQL-HikariCP-SSL");

        // Leak detection for development
        config.setLeakDetectionThreshold(60000); // 60 seconds
        
        // Configure initialization behavior
        configureInitialization(config);

        log.info("Created PostgreSQL DataSource with SSL enabled using custom SSLContext, pool size: {}", maximumPoolSize);

        return new HikariDataSource(config);
    }

    /**
     * Builds JDBC URL with SSL parameters for PostgreSQL.
     * 
     * Uses sslmode=require to force SSL connections and 
     * sslhostnameverifier=org.postgresql.ssl.NonValidatingFactory
     * to disable hostname verification since we're using custom certificates.
     */
    private String buildSslJdbcUrl(String baseUrl) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        
        if (!baseUrl.contains("?")) {
            urlBuilder.append("?");
        } else {
            urlBuilder.append("&");
        }
        
        // Require SSL connection
        urlBuilder.append("sslmode=require");
        
        // Disable hostname verification for custom certificates
        // This is safe because we're explicitly trusting specific certificates
        urlBuilder.append("&sslhostnameverifier=org.postgresql.ssl.NonValidatingFactory");
        
        // Use the default Java SSL implementation (will pick up our custom SSLContext)
        urlBuilder.append("&sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory");
        
        log.debug("Built SSL JDBC URL: {}", urlBuilder.toString().replaceAll("password=[^&]*", "password=***"));
        
        return urlBuilder.toString();
    }
    
    /**
     * Configures HikariCP initialization behavior.
     * Can be overridden in tests to disable connection initialization.
     */
    protected void configureInitialization(HikariConfig config) {
        // Default behavior - initialize connections eagerly
        // Tests can override this to disable initialization
    }
}