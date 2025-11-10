package uk.gov.defra.cdp.trade.demo.transformers;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.defra.cdp.trade.demo.domain.CommodityVariety;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDto;
import uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures;

@RunWith(MockitoJUnitRunner.class)
class CommodityVarietyDtoTransformerTest {

  @Test
  void from_ReturnsCorrectDto() {
    // Given
    CommodityVariety commodityVariety = SupplementaryDataFixtures.commodityVariety();
    CommodityVarietyDto commodityVarietyDto = SupplementaryDataFixtures.commodityVarietyDto();

    // When
    CommodityVarietyDto result = CommodityVarietyDtoTransformer.from(commodityVariety);

    // Then
    assertThat(result).isEqualTo(commodityVarietyDto);
  }
}
