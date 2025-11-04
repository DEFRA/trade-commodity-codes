package uk.gov.defra.cdp.trade.demo.domain.enumerations;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChedType {
  CHEDPP("CHED-PP");

  private final String value;

  public static Optional<ChedType> from(String type) {
    return Arrays.stream(ChedType.values())
        .filter(chedType -> chedType.getValue().equals(type))
        .findFirst();
  }
}
