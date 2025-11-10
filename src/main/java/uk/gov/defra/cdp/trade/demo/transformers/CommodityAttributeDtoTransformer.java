package uk.gov.defra.cdp.trade.demo.transformers;


import uk.gov.defra.cdp.trade.demo.domain.CommodityAttribute;
import uk.gov.defra.cdp.trade.demo.dto.CommodityAttributeDto;

public class CommodityAttributeDtoTransformer {

  private CommodityAttributeDtoTransformer() {
    throw new IllegalStateException("Utility class");
  }

  public static CommodityAttributeDto from(CommodityAttribute attribute) {
    return CommodityAttributeDto.builder()
        .commodityCode(attribute.getCommodityCode())
        .propagation(attribute.getPropagation())
        .build();
  }
}
