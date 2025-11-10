package uk.gov.defra.cdp.trade.demo.transformers;


import uk.gov.defra.cdp.trade.demo.domain.CommodityConfiguration;
import uk.gov.defra.cdp.trade.demo.dto.CommodityConfigurationDto;

public class CommodityConfigurationDtoTransformer {

  private CommodityConfigurationDtoTransformer() {
    throw new IllegalStateException("Utility class");
  }

  public static CommodityConfigurationDto from(CommodityConfiguration config) {
    return CommodityConfigurationDto.builder()
        .commodityCode(config.getCommodityCode())
        .requiresTestAndTrial(config.isRequiresTestAndTrial())
        .requiresFinishedOrPropagated(config.isRequiresFinishedOrPropagated())
        .build();
  }
}
