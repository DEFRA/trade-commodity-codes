package uk.gov.defra.cdp.trade.demo.integration;

import static java.util.Arrays.asList;
import static uk.gov.defra.cdp.trade.demo.integration.API.getChedpSpeciesUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getChedppSpeciesByIdUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getChedppSpeciesUrl;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.defra.cdp.trade.demo.dto.CommoditySearchParametersDto;

class CommoditySpeciesEndpointsIT extends IntegrationBase {

  private static final String APPLES_COMMODITY_CODE = "0808108090";
  private static final String MABSD_EPPO_CODE = "MABSD";
  private static final int MABSD_SPECIES_ID = 1391442;
  private static final String MABSD_SPECIES_NAME = "Malus domestica";
  private static final String MABZU_EPPO_CODE = "MABZU";
  private static final int MABZU_SPECIES_ID = 1327015;
  private static final String MABZU_SPECIES_NAME = "Malus x zumi";
  private static final String MABZC_EPPO_CODE = "MABZC";
  private static final int MABZC_SPECIES_ID = 1365800;
  private static final String MABZC_SPECIES_NAME = "Malus x zumi var. calocarpa";

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void getWithChedppCertTypeShouldReturnCorrectResponseWithEppoCodeAndSpeciesNameQueryParams() {
    // Given
    final String queryUrl =
        String.format("?eppoCode=%s&speciesName=%s", MABSD_EPPO_CODE, MABSD_SPECIES_NAME);

    // When / Then
    String url = getChedppSpeciesUrl(APPLES_COMMODITY_CODE) + queryUrl;

    webClient()
        .get()
        .uri(url)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[0].commodityCode")
        .isEqualTo(APPLES_COMMODITY_CODE)
        .jsonPath("$.content[0].speciesId")
        .isEqualTo(MABSD_SPECIES_ID)
        .jsonPath("$.content[0].speciesName")
        .isEqualTo(MABSD_SPECIES_NAME)
        .jsonPath("$.content[0].eppoCode")
        .isEqualTo(MABSD_EPPO_CODE);
  }

  @Test
  void
      getWithChedppCertTypeShouldReturnCorrectResponseWhenExcludeSpeciesIdsQueryParamIsNotPresent() {
    // Given
    final String queryUrl = String.format("?speciesName=%s", MABZU_SPECIES_NAME);

    // When / Then
    String url = getChedppSpeciesUrl(APPLES_COMMODITY_CODE) + queryUrl;

    webClient()
        .get()
        .uri(url)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[0].commodityCode")
        .isEqualTo(APPLES_COMMODITY_CODE)
        .jsonPath("$.content[0].speciesId")
        .isEqualTo(MABZU_SPECIES_ID)
        .jsonPath("$.content[0].speciesName")
        .isEqualTo(MABZU_SPECIES_NAME)
        .jsonPath("$.content[0].eppoCode")
        .isEqualTo(MABZU_EPPO_CODE)
        // .jsonPath("$.content[1].commodityCode").isEqualTo(APPLES_COMMODITY_CODE)
        .jsonPath("$.content[1].speciesId")
        .isEqualTo(MABZC_SPECIES_ID)
        .jsonPath("$.content[1].speciesName")
        .isEqualTo(MABZC_SPECIES_NAME)
        .jsonPath("$.content[1].eppoCode")
        .isEqualTo(MABZC_EPPO_CODE);
  }

  @Test
  void post_ShouldReturnPagedChedppResponse_WhenEppoCodeIsProvided() {
    CommoditySearchParametersDto searchParameters =
        CommoditySearchParametersDto.builder().eppoCode("MABSD").build();

    webClient()
        .post()
        .uri(getChedppSpeciesUrl(APPLES_COMMODITY_CODE))
        .bodyValue(searchParameters)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[0].commodityCode")
        .isEqualTo(APPLES_COMMODITY_CODE)
        .jsonPath("$.content[0].speciesId")
        .isEqualTo(MABSD_SPECIES_ID)
        .jsonPath("$.content[0].speciesName")
        .isEqualTo(MABSD_SPECIES_NAME)
        .jsonPath("$.content[0].eppoCode")
        .isEqualTo(MABSD_EPPO_CODE);
  }

  @Test
  void post_ShouldReturnPagedChedppResponse_WhenSpeciesNameIsProvided() {
    CommoditySearchParametersDto searchParameters =
        CommoditySearchParametersDto.builder().speciesName("Malus domestica").build();

    webClient()
        .post()
        .uri(getChedppSpeciesUrl(APPLES_COMMODITY_CODE))
        .bodyValue(searchParameters)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[0].commodityCode")
        .isEqualTo(APPLES_COMMODITY_CODE)
        .jsonPath("$.content[0].speciesId")
        .isEqualTo(MABSD_SPECIES_ID)
        .jsonPath("$.content[0].speciesName")
        .isEqualTo(MABSD_SPECIES_NAME)
        .jsonPath("$.content[0].eppoCode")
        .isEqualTo(MABSD_EPPO_CODE);
  }

  @Test
  void post_ShouldReturnCompletePagedChedppResponse_WhenNoParametersAreProvided() {
    CommoditySearchParametersDto searchParameters = CommoditySearchParametersDto.builder().build();

    webClient()
        .post()
        .uri(getChedppSpeciesUrl(APPLES_COMMODITY_CODE))
        .bodyValue(searchParameters)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(11)
        .jsonPath("$.totalPages")
        .isEqualTo(1);
  }

  @Test
  void
      post_ShouldReturnPagedChedppResponseWithReducedElements_WhenExcludedSpeciesIdsAreSpecified() {
    CommoditySearchParametersDto searchParameters =
        CommoditySearchParametersDto.builder().excludeSpeciesIds(asList(1323077, 1423448)).build();

    webClient()
        .post()
        .uri(getChedppSpeciesUrl(APPLES_COMMODITY_CODE))
        .bodyValue(searchParameters)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(11)
        .jsonPath("$.totalPages")
        .isEqualTo(1);
  }

  @Test
  void getWithChedppCertTypeShouldReturnCorrectResponseWhenExcludeSpeciesIdsQueryParamIsPresent() {
    // Given
    final String queryUrl =
        String.format("?speciesName=%s&excludeSpeciesIds=%s", MABZU_SPECIES_NAME, MABZU_SPECIES_ID);

    // When / Then
    String url = getChedppSpeciesUrl(APPLES_COMMODITY_CODE) + queryUrl;

    webClient()
        .get()
        .uri(url)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[0].commodityCode")
        .isEqualTo(APPLES_COMMODITY_CODE)
        .jsonPath("$.content[0].speciesId")
        .isEqualTo(MABZC_SPECIES_ID)
        .jsonPath("$.content[0].speciesName")
        .isEqualTo(MABZC_SPECIES_NAME)
        .jsonPath("$.content[0].eppoCode")
        .isEqualTo(MABZC_EPPO_CODE);
  }

  @Test
  void getChedppSpeciesShouldReturnCorrectResult() {
    // Given / When / Then
    webClient()
        .get()
        .uri(getChedppSpeciesByIdUrl(APPLES_COMMODITY_CODE, MABSD_SPECIES_ID))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.commodityCode")
        .isEqualTo(APPLES_COMMODITY_CODE)
        .jsonPath("$.speciesId")
        .isEqualTo(MABSD_SPECIES_ID)
        .jsonPath("$.speciesName")
        .isEqualTo(MABSD_SPECIES_NAME)
        .jsonPath("$.eppoCode")
        .isEqualTo(MABSD_EPPO_CODE);
  }

  @Test
  void getChedppSpeciesShouldReturnNotFoundStatusWhenSpeciesIdIsNotFound() {
    // Given
    final int unknownSpeciesId = 90938429;

    // When / Then
    webClient()
        .get()
        .uri(getChedppSpeciesByIdUrl(APPLES_COMMODITY_CODE, unknownSpeciesId))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void getChedpSpecies_ShouldReturnNotFoundStatus_WhenCommodityCodeAndSpeciesNameNotMatched() {
    // Given
    final String queryUrl = String.format("?speciesName=%s", "Invalid");

    // When / Then
    String url = getChedpSpeciesUrl(APPLES_COMMODITY_CODE) + queryUrl;

    webClient().method(HttpMethod.GET).uri(url).exchange().expectStatus().isNotFound();
  }

  @Test
  void getChedpSpecies_ShouldReturnNotFoundStatus_WhenSpeciesNameNotPassed() {
    // Given// When / Then
    String url = getChedpSpeciesUrl(APPLES_COMMODITY_CODE);

    webClient().get().uri(url).exchange().expectStatus().isBadRequest();
  }
}
