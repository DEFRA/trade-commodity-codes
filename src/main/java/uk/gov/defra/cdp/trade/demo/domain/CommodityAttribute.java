package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "commodity_attributes")
public class CommodityAttribute {

  @Id
  private String code;

  @Column(name = "traces_commodity_code")
  private String commodityCode;

  @Column(name = "propagation")
  private String propagation;
}
