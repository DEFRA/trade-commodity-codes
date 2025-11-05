package uk.gov.defra.cdp.trade.demo.domain.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.domain.enumerations.ChedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChedTypeConverterTest {

    private ChedTypeConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ChedTypeConverter();
    }

    @Test
    void shouldConvertChedTypeToDataBaseColumn() {
        String result = converter.convertToDatabaseColumn(ChedType.CHEDPP);
        
        assertThat(result).isEqualTo("CHED-PP");
    }

    @Test
    void shouldThrowNullPointerExceptionWhenConvertingNullChedTypeToDataBaseColumn() {
        assertThrows(NullPointerException.class, () -> {
            converter.convertToDatabaseColumn(null);
        });
    }

    @Test
    void shouldConvertValidStringToEntityAttribute() {
        ChedType result = converter.convertToEntityAttribute("CHED-PP");
        
        assertThat(result).isEqualTo(ChedType.CHEDPP);
    }

    @Test
    void shouldReturnNullForInvalidStringToEntityAttribute() {
        ChedType result = converter.convertToEntityAttribute("INVALID_TYPE");
        
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullForNullStringToEntityAttribute() {
        ChedType result = converter.convertToEntityAttribute(null);
        
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullForEmptyStringToEntityAttribute() {
        ChedType result = converter.convertToEntityAttribute("");
        
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullForWhitespaceStringToEntityAttribute() {
        ChedType result = converter.convertToEntityAttribute("   ");
        
        assertThat(result).isNull();
    }

    @Test
    void shouldHandleCaseSensitiveConversion() {
        ChedType result = converter.convertToEntityAttribute("ched-pp");
        
        assertThat(result).isNull();
    }

    @Test
    void shouldHandleRoundTripConversion() {
        ChedType originalEnum = ChedType.CHEDPP;
        
        String dbValue = converter.convertToDatabaseColumn(originalEnum);
        ChedType convertedBack = converter.convertToEntityAttribute(dbValue);
        
        assertThat(convertedBack).isEqualTo(originalEnum);
    }

    @Test
    void shouldConvertAllAvailableChedTypes() {
        for (ChedType chedType : ChedType.values()) {
            String dbValue = converter.convertToDatabaseColumn(chedType);
            ChedType convertedBack = converter.convertToEntityAttribute(dbValue);
            
            assertThat(convertedBack).isEqualTo(chedType);
            assertThat(dbValue).isNotNull();
        }
    }
}