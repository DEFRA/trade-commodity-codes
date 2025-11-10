package uk.gov.defra.cdp.trade.demo.transformers;

import java.util.Arrays;
import uk.gov.defra.cdp.trade.demo.domain.CommodityGroup;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupsDto;

public class CommodityGroupsDtoTransformer {

  private CommodityGroupsDtoTransformer() {
    throw new IllegalStateException("Utility class");
  }

  public static CommodityGroupsDto from(CommodityGroup commodityGroup) {
    return CommodityGroupsDto.builder()
        .commodityCode(commodityGroup.getCommodityCode())
        .commodityGroups(Arrays.asList(commodityGroup.getCommodityGroups().split(";")))
        .build();
  }
}
