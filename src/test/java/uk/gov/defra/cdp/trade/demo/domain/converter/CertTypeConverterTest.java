package uk.gov.defra.cdp.trade.demo.domain.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CertTypeConverterTest {

    @ParameterizedTest
    @MethodSource("provideCertTypeConversions")
    void shouldConvertToDbFormat(String input, String expected) {
        String result = CertTypeConverter.convertToDbFormat(input);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldConvertCvedaToDbFormat() {
        String result = CertTypeConverter.convertToDbFormat("CVEDA");
        assertThat(result).isEqualTo("CVED-A");
    }

    @Test
    void shouldConvertCvedpToDbFormat() {
        String result = CertTypeConverter.convertToDbFormat("CVEDP");
        assertThat(result).isEqualTo("CVED-P");
    }

    @Test
    void shouldConvertChedppToDbFormat() {
        String result = CertTypeConverter.convertToDbFormat("CHEDPP");
        assertThat(result).isEqualTo("CHED-PP");
    }

    @Test
    void shouldHandleLowerCaseInput() {
        String result = CertTypeConverter.convertToDbFormat("cveda");
        assertThat(result).isEqualTo("CVED-A");
    }

    @Test
    void shouldHandleMixedCaseInput() {
        String result = CertTypeConverter.convertToDbFormat("CvEdA");
        assertThat(result).isEqualTo("CVED-A");
    }

    @Test
    void shouldReturnUnchangedForUnknownCertType() {
        String input = "CED";
        String result = CertTypeConverter.convertToDbFormat(input);
        assertThat(result).isEqualTo("CED");
    }

    @Test
    void shouldReturnUnchangedForArbitraryCertType() {
        String input = "UNKNOWN_TYPE";
        String result = CertTypeConverter.convertToDbFormat(input);
        assertThat(result).isEqualTo("UNKNOWN_TYPE");
    }

    @ParameterizedTest
    @ValueSource(strings = {"PHYTO", "CITES", "OTHER"})
    void shouldReturnUnchangedForOtherCertTypes(String input) {
        String result = CertTypeConverter.convertToDbFormat(input);
        assertThat(result).isEqualTo(input);
    }

    @Test
    void shouldThrowExceptionForNullInput() {
        assertThrows(NullPointerException.class, () -> {
            CertTypeConverter.convertToDbFormat(null);
        });
    }

    @Test
    void shouldHandleEmptyStringInput() {
        String result = CertTypeConverter.convertToDbFormat("");
        assertThat(result).isEqualTo("");
    }

    @Test
    void shouldHandleWhitespaceInput() {
        String result = CertTypeConverter.convertToDbFormat("   ");
        assertThat(result).isEqualTo("   ");
    }

    private static Stream<Arguments> provideCertTypeConversions() {
        return Stream.of(
            Arguments.of("CVEDA", "CVED-A"),
            Arguments.of("cveda", "CVED-A"),
            Arguments.of("CvEdA", "CVED-A"),
            Arguments.of("CVEDP", "CVED-P"),
            Arguments.of("cvedp", "CVED-P"),
            Arguments.of("CvEdP", "CVED-P"),
            Arguments.of("CHEDPP", "CHED-PP"),
            Arguments.of("chedpp", "CHED-PP"),
            Arguments.of("ChEdPp", "CHED-PP"),
            Arguments.of("CED", "CED"),
            Arguments.of("ced", "CED"),
            Arguments.of("PHYTO", "PHYTO"),
            Arguments.of("phyto", "PHYTO"),
            Arguments.of("CITES", "CITES"),
            Arguments.of("cites", "CITES"),
            Arguments.of("UNKNOWN", "UNKNOWN")
        );
    }
}