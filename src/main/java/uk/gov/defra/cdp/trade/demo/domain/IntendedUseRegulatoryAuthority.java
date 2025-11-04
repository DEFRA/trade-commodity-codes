package uk.gov.defra.cdp.trade.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.defra.tracesx.commoditycode.dto.enumerations.RegulatoryAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntendedUseRegulatoryAuthority {
  private String intendedUse;
  private RegulatoryAuthority regulatoryAuthority;
}
