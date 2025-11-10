package uk.gov.defra.cdp.trade.demo.integration;


import static uk.gov.defra.cdp.trade.demo.integration.API.searchArticle72Url;

import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.domain.Article72SearchPayload;

class Article72EndpointsIT extends IntegrationBase {

  private static final String CIDER_APPLES_COMMODITY_CODE = "0808108010";
  private static final String CIDER_APPLES_VALID_EPPO_CODE = "MABSZ";
  private static final String CIDER_APPLES_INVALID_EPPO_CODE = "INVALID";
  private static final String FRUIT_AND_NUTS_COMMODITY_GROUP = "Fruit and nuts";

  @Test
  void post_ReturnsCorrectResult_WhenPayloadCombinationIsAnArticle72Commodity() {

    Article72SearchPayload searchPayload = new Article72SearchPayload(CIDER_APPLES_COMMODITY_CODE,
        CIDER_APPLES_VALID_EPPO_CODE, FRUIT_AND_NUTS_COMMODITY_GROUP);

    webClient()
        .post()
        .uri(searchArticle72Url())
        .bodyValue(searchPayload)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.commodityCode").isEqualTo(CIDER_APPLES_COMMODITY_CODE)
        .jsonPath("$.eppoCode").isEqualTo(CIDER_APPLES_VALID_EPPO_CODE)
        .jsonPath("$.commodityGroup").isEqualTo(FRUIT_AND_NUTS_COMMODITY_GROUP)
        .jsonPath("$.lowRiskArticle72").isEqualTo(true);
  }

  @Test
  void post_ReturnsCorrectResult_WhenPayloadCombinationIsNotAnArticle72Commodity() {

    Article72SearchPayload searchPayload = new Article72SearchPayload(CIDER_APPLES_COMMODITY_CODE,
        CIDER_APPLES_INVALID_EPPO_CODE, FRUIT_AND_NUTS_COMMODITY_GROUP);

    webClient()
        .post()
        .uri(searchArticle72Url())
        .bodyValue(searchPayload)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.commodityCode").isEqualTo(CIDER_APPLES_COMMODITY_CODE)
        .jsonPath("$.eppoCode").isEqualTo(CIDER_APPLES_INVALID_EPPO_CODE)
        .jsonPath("$.commodityGroup").isEqualTo(FRUIT_AND_NUTS_COMMODITY_GROUP)
        .jsonPath("$.lowRiskArticle72").isEqualTo(false);
  }

  @Test
  void post_ReturnsBadRequest_WhenPayloadCommodityCodeIsNull() {

    Article72SearchPayload searchPayload = new Article72SearchPayload(null, CIDER_APPLES_VALID_EPPO_CODE,
        FRUIT_AND_NUTS_COMMODITY_GROUP);

    webClient()
        .post()
        .uri(searchArticle72Url())
        .bodyValue(searchPayload)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  public void post_ReturnsBadRequest_WhenPayloadCommodityCodeIsEmpty() {

    Article72SearchPayload searchPayload = new Article72SearchPayload("",
        CIDER_APPLES_VALID_EPPO_CODE, FRUIT_AND_NUTS_COMMODITY_GROUP);

    webClient()
        .post()
        .uri(searchArticle72Url())
        .bodyValue(searchPayload)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  public void post_ReturnsBadRequest_WhenPayloadEppoCodeIsNull() {

    Article72SearchPayload searchPayload = new Article72SearchPayload(CIDER_APPLES_COMMODITY_CODE,
        null, FRUIT_AND_NUTS_COMMODITY_GROUP);

    webClient()
        .post()
        .uri(searchArticle72Url())
        .bodyValue(searchPayload)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  public void post_ReturnsBadRequest_WhenPayloadEppoCodeIsEmpty() {

    Article72SearchPayload searchPayload = new Article72SearchPayload(CIDER_APPLES_COMMODITY_CODE,
        "", FRUIT_AND_NUTS_COMMODITY_GROUP);

    webClient()
        .post()
        .uri(searchArticle72Url())
        .bodyValue(searchPayload)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  public void post_ReturnsBadRequest_WhenPayloadCommodityGroupIsNull() {

    Article72SearchPayload searchPayload = new Article72SearchPayload(CIDER_APPLES_COMMODITY_CODE,
        CIDER_APPLES_VALID_EPPO_CODE, null);

    webClient()
        .post()
        .uri(searchArticle72Url())
        .bodyValue(searchPayload)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  public void post_ReturnsBadRequest_WhenPayloadCommodityGroupIsEmpty() {

    Article72SearchPayload searchPayload = new Article72SearchPayload(CIDER_APPLES_COMMODITY_CODE,
        CIDER_APPLES_VALID_EPPO_CODE, "");

    webClient()
        .post()
        .uri(searchArticle72Url())
        .bodyValue(searchPayload)
        .exchange()
        .expectStatus().isBadRequest();
  }
}
