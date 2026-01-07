package uk.gov.defra.cdp.trade.demo.resource;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Optional;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration tests for controller metrics using @Timed annotations.
 *
 * <p>Uses WebTestClient with RANDOM_PORT instead of MockMvc to test metrics recording
 * in a real server environment. MockMvc operates at the servlet layer and may not
 * trigger all AOP interceptors and filters that process @Timed annotations, potentially
 * missing metrics recording in the full request lifecycle.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class CommodityCodeResourceMetricsIntegrationTest {

  @LocalServerPort
  int port;

  @Autowired
  private MeterRegistry meterRegistry;

  static PostgreSQLContainer POSTGRES_CONTAINER =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
          .withUsername("postgres")
          .withPassword("postgres")
          .withDatabaseName("trade_commodity_codes");

  static {
    Startables.deepStart(POSTGRES_CONTAINER).join();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.hostname", POSTGRES_CONTAINER::getHost);
    registry.add("management.metrics.enabled", () -> "true");
    registry.add("management.metrics.enable.controller", () -> "true");
    registry.add("aws.emf.namespace", () -> "TestNamespace");
  }

  @BeforeEach
  void setUp() {
    // Clear metrics before each test to prevent test order dependency
    meterRegistry.getMeters().forEach(meterRegistry::remove);
  }

  WebTestClient webClient() {
    return WebTestClient.bindToServer()
        .baseUrl("http://localhost:%d".formatted(port))
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("x-cdp-request-id", UUID.randomUUID().toString())
        .build();
  }

  @Test
  void getTopLevel_shouldRecordTimerMetric() {
    // Given: Metric name for top-level endpoint
    String metricName = "controller.getTopLevelDuration.time";
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
    String metricName = "controller.getByCodeDuration.time";
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
