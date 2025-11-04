package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.defra.tracesx.commoditycode.dto.enumerations.MarketingStandard;
import uk.gov.defra.tracesx.commoditycode.dto.enumerations.RegulatoryAuthority;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommodityVariety {

  @Id
  Long id;

  @Column(name = "traces_commodity_code")
  private String commodityCode;

  private String eppoCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "inspection_responsibility")
  private RegulatoryAuthority regulatoryAuthority;

  @Enumerated(EnumType.STRING)
  @Column(name = "hmi_marketing_standard")
  private MarketingStandard marketingStandard;

  private String certificateValidityPeriod;
  private String variety;
}
