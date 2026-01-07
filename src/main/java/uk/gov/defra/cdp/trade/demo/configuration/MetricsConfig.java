package uk.gov.defra.cdp.trade.demo.configuration;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
@Configuration
public class MetricsConfig {

  /**
   * Creates TimedAspect bean to enable @Timed annotation support.
   *
   * <p>This aspect intercepts methods annotated with @Timed and records execution duration metrics.
   * Commonly used on controller endpoints to track request processing time.
   *
   * @param registry the Micrometer registry for recording metrics
   * @return configured TimedAspect instance
   */
  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    log.debug("Creating TimedAspect for {}", registry.getClass().getSimpleName());
    return new TimedAspect(registry);
  }

  /**
   * Creates CountedAspect bean to enable @Counted annotation support.
   *
   * <p>This aspect intercepts methods annotated with @Counted and increments counter metrics.
   * Useful for tracking method invocation counts without timing overhead.
   *
   * @param registry the Micrometer registry for recording metrics
   * @return configured CountedAspect instance
   */
  @Bean
  CountedAspect countedAspect(MeterRegistry registry) {
    return new CountedAspect(registry);
  }
}
