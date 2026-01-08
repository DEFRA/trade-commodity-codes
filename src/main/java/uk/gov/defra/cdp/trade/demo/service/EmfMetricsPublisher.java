package uk.gov.defra.cdp.trade.demo.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;

/**
 * Publishes Micrometer metrics to CloudWatch via AWS Embedded Metrics Format (EMF).
 *
 * <p>This service runs on a fixed schedule (every 60 seconds) and publishes all registered metrics
 * to CloudWatch. Controller-specific metrics (prefixed with "controller") are automatically removed
 * after publishing to prevent unbounded growth in the registry.
 *
 * <p>Configuration:
 * <ul>
 *   <li>{@code management.metrics.enabled=true} - Required for this bean to be created</li>
 *   <li>{@code aws.emf.namespace} - CloudWatch namespace for published metrics</li>
 * </ul>
 *
 * <p>Architecture Note: Controller metrics are ephemeral (removed after each publish cycle) while
 * standard JVM/DB metrics persist for continuous monitoring.
 *
 * @see software.amazon.cloudwatchlogs.emf.logger.MetricsLogger
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "management.metrics.enabled", havingValue = "true")
public class EmfMetricsPublisher {

  private final String namespace;
  private final MeterRegistry meterRegistry;

  EmfMetricsPublisher(
      @Value("${aws.emf.namespace}") String namespace,
      MeterRegistry meterRegistry) {
    this.namespace = namespace;
    this.meterRegistry = meterRegistry;
  }

  /**
   * Publishes all registered metrics to CloudWatch using EMF format.
   *
   * <p>Execution flow:
   * <ol>
   *   <li>Creates EMF MetricsLogger with configured namespace</li>
   *   <li>Iterates all meters in registry and publishes their measurements</li>
   *   <li>Removes controller-specific metrics to prevent registry bloat</li>
   *   <li>Flushes metrics to CloudWatch Logs</li>
   * </ol>
   *
   * <p>Scheduled to run every 60 seconds (60000 milliseconds).
   */
  @Scheduled(fixedRate = 60000)
  public void publishMetrics() {
    MetricsLogger metricsLogger = new MetricsLogger();
    metricsLogger.setNamespace(namespace);
    meterRegistry
        .getMeters()
        .forEach(
            meter -> {
              meter
                  .measure()
                  .forEach(
                      measurement -> {
                        var name = meter.getId().getName();
                        var value = measurement.getValue();
                        log.debug("Publishing metrics for {} with a value of {}", name, value);
                        metricsLogger.putMetric(name, value);
                      });
            });
    // Cleanup: Remove ephemeral controller metrics after publishing to prevent unbounded growth.
    // Controller metrics are recreated on each request via @Timed annotations.
    meterRegistry.getMeters()
        .stream()
        .filter(meter -> meter.getId().getName().startsWith("controller"))
        .forEach(meterRegistry::remove);
    metricsLogger.flush();
  }
}
