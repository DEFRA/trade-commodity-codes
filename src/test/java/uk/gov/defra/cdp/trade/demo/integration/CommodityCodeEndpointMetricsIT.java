package uk.gov.defra.cdp.trade.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for controller metrics using @Timed annotations.
 *
 * <p>Uses WebTestClient with RANDOM_PORT instead of MockMvc to test metrics recording
 * in a real server environment. MockMvc operates at the servlet layer and may not
 * trigger all AOP interceptors and filters that process @Timed annotations, potentially
 * missing metrics recording in the full request lifecycle.
 */
class CommodityCodeEndpointMetricsIT extends IntegrationBase {

  @Autowired
  private MeterRegistry meterRegistry;

  @BeforeEach
  void setUp() {
    // Clear metrics before each test to prevent test order dependency
    meterRegistry.getMeters().forEach(meterRegistry::remove);
  }

  @Test
  void getTopLevel_shouldRecordTimerMetric() {
    // Given: Metric name for top-level endpoint
    String metricName = "controller.getTopLevel.time";
    long countBefore = Optional.ofNullable(meterRegistry.find(metricName).timer())
        .map(Timer::count).orElse(0L);

    // When: Call the endpoint
    webClient()
        .get()
        .uri("/commodity-codes/chedpp/top-level")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.SC_OK);

    // Then: Timer metric should be recorded with exact increment
    Timer timer = meterRegistry.find(metricName).timer();
    assertThat(timer).isNotNull();
    assertThat(timer.count()).isEqualTo(countBefore + 1);
  }

  @Test
  void getByCommodityCode_shouldRecordTimerMetric() {
    // Given: Metric name for commodity code endpoint
    String metricName = "controller.getByCode.time";
    long countBefore = Optional.ofNullable(meterRegistry.find(metricName).timer())
        .map(Timer::count).orElse(0L);

    // When: Call the endpoint
    webClient()
        .get()
        .uri("/commodity-codes/chedpp/commodity-code/0101000000")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.SC_OK);

    // Then: Timer metric should be recorded with exact increment
    Timer timer = meterRegistry.find(metricName).timer();
    assertThat(timer).isNotNull();
    assertThat(timer.count()).isEqualTo(countBefore + 1);
  }
}
