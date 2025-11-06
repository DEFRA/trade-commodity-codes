package uk.gov.defra.cdp.trade.demo.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.gov.defra.cdp.trade.demo.domain.enumerations.ChedType;

@Converter
public class ChedTypeConverter implements AttributeConverter<ChedType, String> {

  @Override
  public String convertToDatabaseColumn(ChedType chedType) {
    return chedType.getValue();
  }

  @Override
  public ChedType convertToEntityAttribute(String type) {
    return ChedType.from(type).orElse(null);
  }
}
