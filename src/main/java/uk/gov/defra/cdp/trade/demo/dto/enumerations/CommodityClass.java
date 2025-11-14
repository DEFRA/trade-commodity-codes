package uk.gov.defra.cdp.trade.demo.dto.enumerations;

import java.util.Arrays;
import java.util.Optional;

public enum CommodityClass {
  CLASSI("Class I"),
  CLASSII("Class II"),
  EXTRA("Extra Class");

  private String value;

  CommodityClass(String value) {
    this.value = value;
  }

  public static Optional<CommodityClass> fromValue(String text) {
    return Arrays.stream(CommodityClass.values())
        .filter(label -> label.value.equals(text))
        .findFirst();
  }

  @Override
  public String toString() {
    return this.value;
  }
}
