package uk.gov.defra.cdp.trade.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.defra.cdp.trade.demo.enumerations.MarketingStandard;
import uk.gov.defra.cdp.trade.demo.enumerations.RegulatoryAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommodityVarietyDtoV2 {

  private String commodityCode;
  private String eppoCode;
  private RegulatoryAuthority regulatoryAuthority;
  private MarketingStandard marketingStandard;
  private String variety;
  private String validityPeriod;
}
