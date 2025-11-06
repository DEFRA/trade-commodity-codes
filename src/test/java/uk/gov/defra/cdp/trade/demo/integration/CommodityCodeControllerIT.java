package uk.gov.defra.cdp.trade.demo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/test-data/commodity-codes-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CommodityCodeControllerIT extends IntegrationBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/commodity-codes";
    }

    @Test
    void shouldGetAllCommodityCodes() {
        ResponseEntity<CommodityCode[]> response = restTemplate.getForEntity(baseUrl, CommodityCode[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldGetCommodityCodeByCode() {
        String testCode = "3001";
        
        ResponseEntity<CommodityCode> response = restTemplate.getForEntity(
            baseUrl + "/code/" + testCode, 
            CommodityCode.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(testCode);
    }

    @Test
    void shouldReturnNotFoundForNonExistentCode() {
        String nonExistentCode = "999999";
        
        ResponseEntity<CommodityCode> response = restTemplate.getForEntity(
            baseUrl + "/code/" + nonExistentCode, 
            CommodityCode.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldSearchCommodityCodesByDescription() {
        String searchTerm = "wheat";

        ResponseEntity<CommodityCode[]> response = restTemplate.getForEntity(
            baseUrl + "/search?q=" + searchTerm,
            CommodityCode[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldFindCommodityCodesByPrefix() {
        String prefix = "10";

        ResponseEntity<CommodityCode[]> response = restTemplate.getForEntity(
            baseUrl + "/prefix/" + prefix,
            CommodityCode[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldIncludeTraceIdInResponse() {
        String traceId = "test-trace-id-123";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cdp-request-id", traceId);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<CommodityCode[]> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            request,
            CommodityCode[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
