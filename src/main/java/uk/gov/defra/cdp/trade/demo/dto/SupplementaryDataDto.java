package uk.gov.defra.cdp.trade.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.defra.cdp.trade.demo.domain.IntendedUseRegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.enumerations.MarketingStandard;
import uk.gov.defra.cdp.trade.demo.enumerations.RegulatoryAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplementaryDataDto {

  @JsonInclude(Include.NON_NULL)
  private String speciesId;
  private String commodityCode;
  private String eppoCode;
  private List<RegulatoryAuthority> regulatoryAuthority;
  private MarketingStandard marketingStandard;
  private String validityPeriod;
  private List<String> varieties;
  private List<String> classes;
  private List<IntendedUseRegulatoryAuthority> intendedUseRegulatoryAuthorities;
}
