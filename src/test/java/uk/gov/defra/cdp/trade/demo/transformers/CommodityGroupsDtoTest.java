package uk.gov.defra.cdp.trade.demo.transformers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.domain.CommodityGroup;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupsDto;
import uk.gov.defra.cdp.trade.demo.fixtures.CommodityGroupFixtures;

class CommodityGroupsDtoTest {
  @Test
  void from_ReturnsTheCorrectDtoInformation_WhenCalled() {
    // Given
    CommodityGroup commodityGroup = CommodityGroupFixtures.commodityGroups();
    CommodityGroupsDto commodityGroupsDto = CommodityGroupFixtures.commodityGroupsDto();

    // When
    CommodityGroupsDto result = CommodityGroupsDtoTransformer.from(commodityGroup);

    // Then
    assertThat(result).isEqualTo(commodityGroupsDto);
  }
}
