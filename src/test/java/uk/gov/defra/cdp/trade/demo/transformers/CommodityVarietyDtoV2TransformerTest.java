package uk.gov.defra.cdp.trade.demo.transformers;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.commodityVariety;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.commodityVarietyDtoV2;

import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDtoV2;

class CommodityVarietyDtoV2TransformerTest {

  @Test
  void from_ReturnsCorrectDto() {
    CommodityVarietyDtoV2 result = CommodityVarietyDtoV2Transformer.from(commodityVariety());

    assertThat(result).isEqualTo(commodityVarietyDtoV2());
  }
}
