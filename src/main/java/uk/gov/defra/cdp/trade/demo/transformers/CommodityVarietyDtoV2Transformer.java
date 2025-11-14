package uk.gov.defra.cdp.trade.demo.transformers;


import uk.gov.defra.cdp.trade.demo.domain.CommodityVariety;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDtoV2;

public class CommodityVarietyDtoV2Transformer {

  private CommodityVarietyDtoV2Transformer() {
    throw new IllegalStateException("Utility class");
  }

  public static CommodityVarietyDtoV2 from(CommodityVariety commodityVariety) {
    return CommodityVarietyDtoV2.builder()
        .commodityCode(commodityVariety.getCommodityCode())
        .eppoCode(commodityVariety.getEppoCode())
        .regulatoryAuthority(commodityVariety.getRegulatoryAuthority())
        .marketingStandard(commodityVariety.getMarketingStandard())
        .variety(commodityVariety.getVariety())
        .validityPeriod(commodityVariety.getCertificateValidityPeriod())
        .build();
  }
}
