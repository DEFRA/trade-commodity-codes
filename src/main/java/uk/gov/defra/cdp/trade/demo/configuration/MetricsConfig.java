package uk.gov.defra.cdp.trade.demo.configuration;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

/**
 * Configuration for Micrometer metrics.
 *
 * <p>Micrometer provides standard JVM, HTTP, and database metrics via Spring Boot Actuator. Custom
 * business metrics use AWS EMF (see EmfMetricsConfig and MetricsService).
 *
 * <p>ARCHITECTURE: - Standard metrics (JVM, HTTP, DB): Micrometer via Spring Boot Actuator - Custom
 * business metrics: AWS Embedded Metrics Format (EMF)
 *
 * <p>This configuration only provides a fallback SimpleMeterRegistry when metrics are disabled.
 * When enabled, Spring Boot Actuator auto-configures appropriate registries.
 */
@Slf4j
public class MetricsConfig {

  /**
   * Fallback MeterRegistry when metrics are disabled.
   *
   * <p>When management.metrics.enabled=false, Spring Boot Actuator still needs a MeterRegistry bean
   * to avoid errors. SimpleMeterRegistry is an in-memory registry that discards all metrics.
   *
   * <p>This bean is only created when metrics are explicitly disabled.
   */
  @Bean
  @ConditionalOnProperty(value = "management.metrics.enabled", havingValue = "false")
  public MeterRegistry simpleMeterRegistry() {
    log.info("Metrics disabled - using SimpleMeterRegistry");
    return new SimpleMeterRegistry();
  }

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  @Bean
  public CloudWatchAsyncClient cloudWatchAsyncClient(
      @Value("${spring.cloud.aws.cloudwatch.endpoint}") String cloudwatchUri,
      @Value("${spring.cloud.aws.cloudwatch.region}") String region) {
    return CloudWatchAsyncClient.builder()
        .endpointOverride(URI.create(cloudwatchUri))
        .region(Region.of(region)) // your region
        .build();
  }

  @Bean
  public CloudWatchMeterRegistry cloudWatchMeterRegistry(
      CloudWatchConfig cloudWatchConfig,
      CloudWatchAsyncClient cloudWatchAsyncClient,
      Clock clock) {
    return new CloudWatchMeterRegistry(cloudWatchConfig, clock, cloudWatchAsyncClient);
  }
}
