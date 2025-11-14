package uk.gov.defra.cdp.trade.demo;

import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uk.gov.defra.cdp.trade.demo.domain.ChedpSpecies;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;

public class CommodityCategoryFixtures {

  public CommodityCategoryFixtures() {
    throw new IllegalArgumentException("This class cannot be instantiated");
  }

  public static ChedppSpecies chedppSpeciesFixture() {
    return ChedppSpecies.builder()
        .commodityCode("12345")
        .speciesId(12345)
        .eppoCode("MABSD")
        .speciesName("Malis")
        .commodityDescription("Other")
        .build();
  }

  public static ChedpSpecies chedpSpeciesFixture() {
    return ChedpSpecies.builder()
        .commodityCode("01")
        .speciesId(12345)
        .speciesName("Malis")
        .commodityTypeName("Wild stock")
        .className("Actinopterygii")
        .familyName("Scombridae")
        .build();
  }

  public static Page<ChedppSpecies> chedppSpeciesPaginatedResponseFixture() {
    return new PageImpl<>(Collections.singletonList(chedppSpeciesFixture()));
  }
}
