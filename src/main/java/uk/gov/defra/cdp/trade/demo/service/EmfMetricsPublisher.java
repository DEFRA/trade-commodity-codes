package uk.gov.defra.cdp.trade.demo.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;

@Service
@Slf4j
public class EmfMetricsPublisher {

  private final String namespace;
  private final MeterRegistry meterRegistry;

  EmfMetricsPublisher(@Value("${aws.emf.namespace}") String namespace,
      MeterRegistry meterRegistry) {
    this.namespace = namespace;
    this.meterRegistry = meterRegistry;
  }

  @Scheduled(fixedRate = 60000)
  public void publishMetrics() {
    MetricsLogger metricsLogger = new MetricsLogger();
    metricsLogger.setNamespace(namespace);
    meterRegistry
        .getMeters()
        .forEach(
            meter -> {
              log.debug("Publishing metrics for {}", meter.getId().getName());
              meter
                  .measure()
                  .forEach(
                      measurement -> {
                        var name = meter.getId().getName();
                        var value = measurement.getValue();
                        metricsLogger.putMetric(name, value);
                      });
            });
    metricsLogger.flush();
  }
  
}
