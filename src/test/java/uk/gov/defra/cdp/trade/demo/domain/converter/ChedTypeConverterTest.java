package uk.gov.defra.cdp.trade.demo.domain.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.defra.cdp.trade.demo.enumerations.ChedType.CHEDPP;

import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.enumerations.ChedType;

class ChedTypeConverterTest {

  private final ChedTypeConverter underTest = new ChedTypeConverter();

  @Test
  void convertToDatabaseColumn_returnsValueOfEnum() {
    String converted = underTest.convertToDatabaseColumn(CHEDPP);

    assertThat(converted).isEqualTo(CHEDPP.getValue());
  }

  @Test
  void convertToEntityAttribute_ReturnsNull_WhenTypeDoesNotMatchEnumValue() {
    ChedType type = underTest.convertToEntityAttribute("some invalid type");

    assertThat(type).isNull();
  }

  @Test
  void convertToEntityAttribute_ReturnsEnum_WhenTypeDoesMatchesEnumValue() {
    ChedType type = underTest.convertToEntityAttribute("CHED-PP");

    assertThat(type).isEqualTo(CHEDPP);
  }
}
