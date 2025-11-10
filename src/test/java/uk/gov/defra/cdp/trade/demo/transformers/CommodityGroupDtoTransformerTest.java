package uk.gov.defra.cdp.trade.demo.transformers;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.defra.cdp.trade.demo.domain.CommodityGroup;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupDto;
import uk.gov.defra.cdp.trade.demo.fixtures.CommodityGroupFixtures;

@RunWith(MockitoJUnitRunner.class)
class CommodityGroupDtoTransformerTest {

  @Test
  void from_ReturnsCorrectDto() {
    // Given
    CommodityGroup commodityGroup = CommodityGroupFixtures.commodityGroup();
    CommodityGroupDto commodityGroupDto = CommodityGroupFixtures.commodityGroupDto();

    // When
    CommodityGroupDto result = CommodityGroupDtoTransformer.from(commodityGroup);

    // Then
    assertThat(result).isEqualTo(commodityGroupDto);
  }
}
