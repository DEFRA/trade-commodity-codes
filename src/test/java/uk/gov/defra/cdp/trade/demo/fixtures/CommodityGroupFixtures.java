package uk.gov.defra.cdp.trade.demo.fixtures;

import java.util.Arrays;
import uk.gov.defra.cdp.trade.demo.domain.CommodityGroup;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupsDto;

public class CommodityGroupFixtures {

  private static final String COMMODITY_CODE_ONE = "10294023";
  private static final String PLANTS_FOR_PLANTING = "Plants for Planting";
  private static final String SEED_AND_TISSUE_CULTURE = "Seed & Tissue Culture";

  public static CommodityGroup commodityGroups() {
    return CommodityGroup.builder()
        .commodityCode(COMMODITY_CODE_ONE)
        .commodityGroups(String.join(";", SEED_AND_TISSUE_CULTURE))
        .build();
  }

  public static CommodityGroupsDto commodityGroupsDto() {
    return CommodityGroupsDto.builder()
        .commodityCode(COMMODITY_CODE_ONE)
        .commodityGroups(Arrays.asList(SEED_AND_TISSUE_CULTURE))
        .build();
  }

  public static CommodityGroup commodityGroup() {
    return CommodityGroup.builder()
        .commodityCode(COMMODITY_CODE_ONE)
        .commodityGroups(PLANTS_FOR_PLANTING)
        .build();
  }

  public static CommodityGroupDto commodityGroupDto() {
    return CommodityGroupDto.builder()
        .commodityCode(COMMODITY_CODE_ONE)
        .commodityGroup(PLANTS_FOR_PLANTING)
        .build();
  }
}
