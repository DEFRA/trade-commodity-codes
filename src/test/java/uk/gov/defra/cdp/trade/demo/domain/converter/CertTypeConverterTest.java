package uk.gov.defra.cdp.trade.demo.domain.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CertTypeConverterTest {

  @Test
  void convert_ShouldReturnConvertedCertType_WhenGivenListOfValues() {
    Map.of(
            "CVEDA", "CVED-A",
            "CVEDP", "CVED-P",
            "CHEDPP", "CHED-PP",
            "CED", "CED",
            "ced", "CED")
        .forEach(
            (inputType, expectedType) -> {
              String result = CertTypeConverter.convertToDbFormat(inputType);

              assertThat(result).isEqualTo(expectedType);
            });
  }
}
