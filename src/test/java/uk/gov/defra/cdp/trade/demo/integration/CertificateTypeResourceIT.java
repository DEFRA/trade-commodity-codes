package uk.gov.defra.cdp.trade.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static uk.gov.defra.cdp.trade.demo.integration.API.getCertTypeUrl;

import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.FluxExchangeResult;

class CertificateTypeResourceIT extends IntegrationBase {

  private static final String COMMODITY_CODE_ONLY_FOR_CVEDA = "01";
  private static final String COMMODITY_CODE_FOR_CVEDP_AND_CHEDPP = "06";
  private static final String COMMODITY_CODE_ONLY_FOR_CHEDPP = "44079";
  private static final String COMMODITY_CODE_ONLY_FOR_CED = "2201";
  private static final String COMMODITY_CODE_NOT_EXIST = "CODE_NOT_EXIST";

  @Test
  void getAllCertTypesForMultipleCommodityCodes_forAllCombinations() {

    String certTypeUrl = getCertTypeUrl(List.of(
        COMMODITY_CODE_ONLY_FOR_CVEDA,
        COMMODITY_CODE_FOR_CVEDP_AND_CHEDPP,
        COMMODITY_CODE_ONLY_FOR_CHEDPP,
        COMMODITY_CODE_ONLY_FOR_CED,
        COMMODITY_CODE_NOT_EXIST,""));

    var response = getCertType(certTypeUrl);
    Map<String, List<String>> certTypes = getResponseAsObject(response.getResponseBodyContent(), Map.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(certTypes).contains(entry(COMMODITY_CODE_ONLY_FOR_CVEDA, List.of("A")),
        entry(COMMODITY_CODE_FOR_CVEDP_AND_CHEDPP, List.of("P")),
        entry(COMMODITY_CODE_ONLY_FOR_CED, List.of("D")));

  }

  @Test
  void getAllCertTypesForSingleCommodityCode() {

    var response = getCertType(getCertTypeUrl(List.of(COMMODITY_CODE_ONLY_FOR_CVEDA)));

    Map<String, List<String>> certTypes =
        getResponseAsObject(response.getResponseBodyContent(), Map.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(certTypes).contains(entry(COMMODITY_CODE_ONLY_FOR_CVEDA, List.of("A")));

  }

  @Test
  void getAllCertTypes_whenNoCommodityCodesPassedAsParams_returns400() {
    var response = getCertType(getCertTypeUrl(null));

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void getAllCertTypes_whenInvalidCommodityCodesPassedAsParams_returnsNoCertTypes() {
    var response = getCertType(getCertTypeUrl(List.of(COMMODITY_CODE_NOT_EXIST)));

    Map<String, List<String>> certTypes =
        getResponseAsObject(response.getResponseBodyContent(), Map.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(certTypes).isEmpty();
  }

  private FluxExchangeResult<String> getCertType(String url) {
    return whenApiCallByADInspector(API.get(url)).body();
  }
}
