package uk.gov.defra.cdp.trade.demo.fixtures;

import java.util.List;
import uk.gov.defra.cdp.trade.demo.domain.CommodityClass;
import uk.gov.defra.cdp.trade.demo.domain.CommodityVariety;
import uk.gov.defra.cdp.trade.demo.domain.IntendedUseRegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.domain.SearchVariety;
import uk.gov.defra.cdp.trade.demo.domain.SupplementaryData;
import uk.gov.defra.cdp.trade.demo.enumerations.MarketingStandard;
import uk.gov.defra.cdp.trade.demo.enumerations.RegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDtoV2;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDto;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDtoV2;

public class SupplementaryDataFixtures {

  public static final String COMMODITY_CODE = "0805102890";
  public static final String EPPO_CODE = "CIDSI";
  public static final String SPECIES_ID = "1380239";
  public static final String SPECIES_NAME = "Citrus sinensis";
  public static final String VARIETY = "Navel";
  public static final String VALIDITY_PERIOD_1 = "1";
  public static final String VALIDITY_PERIOD_2 = "2";
  private static final String CLASS = "Abate";
  public static final String COMMODITY_DESCRIPTION = "OTHER";

  public static SupplementaryData supplementaryData() {
    return SupplementaryData.builder()
        .code("00001")
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .regulatoryAuthority(RegulatoryAuthority.PHSI)
        .marketingStandard(MarketingStandard.SMS)
        .certificateValidityPeriod(VALIDITY_PERIOD_1)
        .build();
  }

  public static SupplementaryDataDto supplementaryDataDto() {
    return SupplementaryDataDto.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .regulatoryAuthority(List.of(RegulatoryAuthority.PHSI))
        .marketingStandard(MarketingStandard.SMS)
        .validityPeriod(VALIDITY_PERIOD_1)
        .classes(List.of(CLASS))
        .varieties(List.of(VARIETY))
        .intendedUseRegulatoryAuthorities(List.of(
            IntendedUseRegulatoryAuthority.builder()
                .intendedUse("")
                .regulatoryAuthority(RegulatoryAuthority.PHSI)
                .build()))
        .build();
  }

  public static SupplementaryDataDtoV2 supplementaryDataDtoV2() {
    return SupplementaryDataDtoV2.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .regulatoryAuthority(RegulatoryAuthority.PHSI)
        .marketingStandard(MarketingStandard.SMS)
        .validityPeriod(VALIDITY_PERIOD_1)
        .classes(List.of(CLASS))
        .varieties(List.of(VARIETY))
        .build();
  }

  public static SupplementaryDataDtoV2 supplementaryDataDtoV2NoClassesAndVarieties() {
    return SupplementaryDataDtoV2.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .regulatoryAuthority(RegulatoryAuthority.PHSI)
        .marketingStandard(MarketingStandard.SMS)
        .validityPeriod(VALIDITY_PERIOD_1)
        .build();
  }

  public static SupplementaryDataDto supplementaryDataDtoWithSpeciesId() {
    SupplementaryDataDto dto = supplementaryDataDto();
    dto.setSpeciesId(SPECIES_ID);
    return dto;
  }

  public static SupplementaryDataDtoV2 supplementaryDataDtoWithSpeciesIdV2() {
    SupplementaryDataDtoV2 dto = supplementaryDataDtoV2();
    dto.setSpeciesId(SPECIES_ID);
    return dto;
  }

  public static CommodityVariety commodityVariety() {
    return CommodityVariety.builder()
        .id(1L)
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .regulatoryAuthority(RegulatoryAuthority.JOINT)
        .marketingStandard(MarketingStandard.SMS)
        .variety(VARIETY)
        .certificateValidityPeriod(VALIDITY_PERIOD_2)
        .build();
  }

  public static CommodityVarietyDto commodityVarietyDto() {
    return CommodityVarietyDto.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .regulatoryAuthority(RegulatoryAuthority.JOINT)
        .marketingStandard(MarketingStandard.SMS)
        .variety(VARIETY)
        .validityPeriod(VALIDITY_PERIOD_2)
        .build();
  }

  public static CommodityVarietyDtoV2 commodityVarietyDtoV2() {
    return CommodityVarietyDtoV2.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .regulatoryAuthority(RegulatoryAuthority.JOINT)
        .marketingStandard(MarketingStandard.SMS)
        .variety(VARIETY)
        .validityPeriod(VALIDITY_PERIOD_2)
        .build();
  }

  public static CommodityClass commodityClass() {
    return CommodityClass.builder()
        .code("00001")
        .clazz(CLASS)
        .build();
  }

  public static SearchVariety searchVariety() {
    return SearchVariety.builder()
        .eppoCode(EPPO_CODE)
        .variety(VARIETY)
        .build();
  }
}
