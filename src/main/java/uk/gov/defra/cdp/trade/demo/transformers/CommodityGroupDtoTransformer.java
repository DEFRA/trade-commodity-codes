package uk.gov.defra.cdp.trade.demo.transformers;


import uk.gov.defra.cdp.trade.demo.domain.CommodityGroup;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupDto;

public class CommodityGroupDtoTransformer {

  private CommodityGroupDtoTransformer() {
    throw new IllegalStateException("Utility class");
  }

  public static CommodityGroupDto from(CommodityGroup commodityGroup) {
    return CommodityGroupDto.builder()
        .commodityCode(commodityGroup.getCommodityCode())
        .commodityGroup(commodityGroup.getCommodityGroups())
        .build();
  }
}
