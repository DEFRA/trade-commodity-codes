package uk.gov.defra.cdp.trade.demo.domain.enumerations;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.enumerations.ChedType;

class ChedTypeTest {

  @Test
  void from_returnsNull_whenNoMatchingEnumValue() {
    Optional<ChedType> value = ChedType.from("some random value");

    assertThat(value).isEmpty();
  }

  @Test
  void from_returnsEnum_whenMatchingEnumValueProvided() {
    Optional<ChedType> value = ChedType.from("CHED-PP");

    assertThat(value).contains(ChedType.CHEDPP);
  }
}
