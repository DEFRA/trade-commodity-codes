package uk.gov.defra.cdp.trade.demo.domain.enumerations;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.dto.enumerations.CommodityClass;

class CommodityClassTest {

  @Test
  void whenCommodityClassEnumFromValueMethodIsCalledExpectCorrectValue() {
    // Given
    final String value = "Class I";

    // When
    Optional<CommodityClass> result = CommodityClass.fromValue(value);

    // Then
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo(CommodityClass.CLASSI);
  }

  @Test
  void whenCommodityClassEnumToStringMethodIsCalledExpectCorrectValue() {
    // Given
    CommodityClass enumValue = CommodityClass.EXTRA;

    // When
    String result = enumValue.toString();

    // Then
    assertThat(result).isEqualTo("Extra Class");
  }
}
