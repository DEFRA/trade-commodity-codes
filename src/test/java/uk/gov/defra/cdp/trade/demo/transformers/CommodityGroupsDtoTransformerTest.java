package uk.gov.defra.cdp.trade.demo.transformers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.domain.CommodityGroup;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupsDto;
import uk.gov.defra.cdp.trade.demo.fixtures.CommodityGroupFixtures;

class CommodityGroupsDtoTransformerTest {
  @Test
  void from_ReturnsCorrectDto() {
    // Given
    CommodityGroup commodityGroups = CommodityGroupFixtures.commodityGroups();
    CommodityGroupsDto commodityGroupsDto = CommodityGroupFixtures.commodityGroupsDto();

    // When
    CommodityGroupsDto result = CommodityGroupsDtoTransformer.from(commodityGroups);

    // Then
    assertThat(result).isEqualTo(commodityGroupsDto);
  }
}
