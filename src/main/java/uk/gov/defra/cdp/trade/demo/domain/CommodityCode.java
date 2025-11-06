package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@IdClass(CommodityCodeId.class)
@Table(name = "v_commodity_code")
public class CommodityCode {

  @Id
  private String code;
  @Id
  private String certType;
  private String displayCode;
  private String description;
  private String immediateParent;
  private boolean isCommodity;
  private boolean isParent;
}
