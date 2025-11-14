package uk.gov.defra.cdp.trade.demo.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
abstract class IntegrationBase {

  @LocalServerPort
  int port;

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
  }

  WebTestClient webClient() {

    return WebTestClient.bindToServer()
        .baseUrl("http://localhost:%d".formatted(port))
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("x-cdp-request-id", UUID.randomUUID().toString())
        .build();
  }

  <T> T getResponseAsObject(byte[] bytes, Class<T> clazz) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
      return mapper.reader().forType(clazz).readValue(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  <T> API.Response<T> whenApiCallByADInspector(API.MakeRequest<T> apiRequester) {
    return apiRequester.makeRequest(webClient());
  }

  <T> List<T> getResponseAsList(byte[] bytes, Class<T> clazz) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(
          bytes,
          mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
    } catch (IOException e) {
      // Debug: Print the actual response when JSON parsing fails
      String responseBody = new String(bytes, StandardCharsets.UTF_8);
      System.err.println("Failed to parse response as list. Actual response: " + responseBody);
      throw new RuntimeException(e);
    }
  }
}
