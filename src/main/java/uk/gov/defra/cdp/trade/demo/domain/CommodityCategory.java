package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "v_commodity_category")
@IdClass(CommodityCategoryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommodityCategory {

  @Id
  private String certificateType;
  @Id
  private String commodityCode;
  private String data;
}
