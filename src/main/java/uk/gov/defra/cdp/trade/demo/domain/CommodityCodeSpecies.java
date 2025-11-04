package uk.gov.defra.cdp.trade.demo.domain;

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
@IdClass(CommodityCodeId.class)
@Table(name = "v_commodity_code_species")
public class CommodityCodeSpecies {

  @Id
  private String code;
  @Id
  private String certType;
  private String displayCode;
  private String description;
  private String immediateParent;
  private boolean isCommodity;
  private boolean isParent;
  private String species;
}
