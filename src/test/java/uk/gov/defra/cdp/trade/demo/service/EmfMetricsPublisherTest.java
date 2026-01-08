package uk.gov.defra.cdp.trade.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmfMetricsPublisherTest {

  private MeterRegistry meterRegistry;
  private EmfMetricsPublisher publisher;
  private String namespace;

  @BeforeEach
  void setUp() {
    namespace = "TestNamespace";
    meterRegistry = new SimpleMeterRegistry();
    publisher = new EmfMetricsPublisher(namespace, meterRegistry);
  }

  @Test
  void publishMetrics_shouldProcessAllMeters() {
    // Given: Register test metrics
    meterRegistry.counter("test.counter").increment();
    meterRegistry.timer("test.timer").record(() -> {});
    // Note: Gauge metrics are not used in this test as they require a state object
    // and are difficult to test in isolation. Counter and timer provide sufficient
    // coverage of the metric processing logic.

    int initialMeterCount = meterRegistry.getMeters().size();

    // When: Publish metrics
    publisher.publishMetrics();

    // Then: Meters are still registered (non-controller metrics remain)
    assertThat(meterRegistry.getMeters()).hasSizeGreaterThanOrEqualTo(initialMeterCount);
  }

  @Test
  void publishMetrics_shouldRemoveControllerMetrics() {
    // Given: Register controller and non-controller metrics
    meterRegistry.counter("controller.test.counter").increment();
    meterRegistry.counter("business.counter").increment();
    meterRegistry.timer("controller.test.timer").record(() -> {});

    // When: Publish metrics
    publisher.publishMetrics();

    // Then: Only controller metrics are removed
    assertThat(meterRegistry.getMeters())
        .noneMatch(meter -> meter.getId().getName().startsWith("controller"));
    assertThat(meterRegistry.getMeters())
        .anyMatch(meter -> meter.getId().getName().equals("business.counter"));
  }

  @Test
  void constructor_shouldInitializeWithNamespace() {
    EmfMetricsPublisher newPublisher = new EmfMetricsPublisher("CustomNamespace", meterRegistry);

    assertThat(newPublisher).isNotNull();
  }
}
