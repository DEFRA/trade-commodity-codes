package uk.gov.defra.cdp.trade.demo.transformers;


import uk.gov.defra.cdp.trade.demo.domain.SupplementaryData;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDtoV2;

public class SupplementaryDataDtoV2Transformer {

  private SupplementaryDataDtoV2Transformer() {
    throw new IllegalStateException("Utility class");
  }

  public static SupplementaryDataDtoV2 from(SupplementaryData supplementaryData) {
    return SupplementaryDataDtoV2.builder()
        .commodityCode(supplementaryData.getCommodityCode())
        .eppoCode(supplementaryData.getEppoCode())
        .regulatoryAuthority(supplementaryData.getRegulatoryAuthority())
        .marketingStandard(supplementaryData.getMarketingStandard())
        .validityPeriod(supplementaryData.getCertificateValidityPeriod())
        .build();
  }
}
