package uk.gov.defra.cdp.trade.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.defra.cdp.trade.demo.integration.API.getCommodityCategoryUrl;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.FluxExchangeResult;

class CommodityCategoryEndpointsIT extends IntegrationBase {

  private static final String CERT_TYPE = "cveda";
  private static final String COMMODITY_CODE = "0101";
  private static final String NOT_FOUND = "notFound";

  @Test
  void getShouldReturnTheCorrectCategory() {

    String url = getCommodityCategoryUrl(CERT_TYPE, COMMODITY_CODE);

    var response = getCommodityCategory(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  void getByNonExistentCommodityCodeShouldReturnEmptyResponse() {
    String url = getCommodityCategoryUrl(CERT_TYPE, NOT_FOUND);

    var response = getCommodityCategory(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  void getByNonExistentCertificateTypeShouldReturnEmptyResponse() {
    String url = getCommodityCategoryUrl(NOT_FOUND, COMMODITY_CODE);

    var response = getCommodityCategory(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
  }


  private FluxExchangeResult<String> getCommodityCategory(String url) {
    return whenApiCallByADInspector(API.get(url)).body();
  }
}
