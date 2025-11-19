package uk.gov.defra.cdp.trade.demo.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;
import software.amazon.cloudwatchlogs.emf.model.Unit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmfMetricsPublisher {

  private final MetricsLogger metricsLogger = new MetricsLogger();
  private final MeterRegistry meterRegistry;

  @Scheduled(fixedRate = 60000)
  public void publishMetrics() {
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
                        metricsLogger.putMetric(name, value, Unit.COUNT);
                      });
            });
    metricsLogger.flush();
  }

}
