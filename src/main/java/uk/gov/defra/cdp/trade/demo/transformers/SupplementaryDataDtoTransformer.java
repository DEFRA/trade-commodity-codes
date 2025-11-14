package uk.gov.defra.cdp.trade.demo.transformers;

import java.util.Collections;
import uk.gov.defra.cdp.trade.demo.domain.IntendedUseRegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.domain.SupplementaryData;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDto;

public class SupplementaryDataDtoTransformer {

  private SupplementaryDataDtoTransformer() {
    throw new IllegalStateException("Utility class");
  }

  public static SupplementaryDataDto from(SupplementaryData supplementaryData) {
    IntendedUseRegulatoryAuthority intendedUseRegulatoryAuthority =
        IntendedUseRegulatoryAuthority.builder()
            .intendedUse("")
            .regulatoryAuthority(supplementaryData.getRegulatoryAuthority())
            .build();

    return SupplementaryDataDto.builder()
        .commodityCode(supplementaryData.getCommodityCode())
        .eppoCode(supplementaryData.getEppoCode())
        .regulatoryAuthority(Collections.singletonList(supplementaryData.getRegulatoryAuthority()))
        .marketingStandard(supplementaryData.getMarketingStandard())
        .validityPeriod(supplementaryData.getCertificateValidityPeriod())
        .intendedUseRegulatoryAuthorities(Collections.singletonList(intendedUseRegulatoryAuthority))
        .build();
  }
}
