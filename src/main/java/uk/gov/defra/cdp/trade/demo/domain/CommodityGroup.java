package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "v_commodity_group")
@IdClass(CommodityGroup.class)
public class CommodityGroup {

  @Id
  @Column(name = "traces_commodity_code")
  private String commodityCode;

  @Id
  @Column(name = "commodityGroup")
  private String commodityGroups;
}
