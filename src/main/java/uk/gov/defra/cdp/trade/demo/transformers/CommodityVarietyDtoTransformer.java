package uk.gov.defra.cdp.trade.demo.transformers;


import uk.gov.defra.cdp.trade.demo.domain.CommodityVariety;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDto;

public class CommodityVarietyDtoTransformer {

  private CommodityVarietyDtoTransformer() {
    throw new IllegalStateException("Utility class");
  }

  public static CommodityVarietyDto from(CommodityVariety commodityVariety) {
    return CommodityVarietyDto.builder()
        .commodityCode(commodityVariety.getCommodityCode())
        .eppoCode(commodityVariety.getEppoCode())
        .regulatoryAuthority(commodityVariety.getRegulatoryAuthority())
        .marketingStandard(commodityVariety.getMarketingStandard())
        .variety(commodityVariety.getVariety())
        .validityPeriod(commodityVariety.getCertificateValidityPeriod())
        .build();
  }
}
