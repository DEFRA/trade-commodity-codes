package uk.gov.defra.cdp.trade.demo.transformers;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryData;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryDataDtoV2;

import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDtoV2;

class SupplementaryDataDtoV2TransformerTest {

  @Test
  void from_ReturnsCorrectDto() {
    SupplementaryDataDtoV2 expected = supplementaryDataDtoV2();
    expected.setVarieties(null);
    expected.setClasses(null);

    SupplementaryDataDtoV2 result = SupplementaryDataDtoV2Transformer.from(supplementaryData());

    assertThat(result).isEqualTo(expected);
  }
}
