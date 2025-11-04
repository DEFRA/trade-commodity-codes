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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "v_chedp_species")
@IdClass(CommodityCodeSpeciesId.class)
@Builder
public class ChedpSpecies {

  @Id
  private String commodityCode;
  private Integer speciesId;
  private String speciesName;
  private String commodityTypeName;
  private String className;
  private String familyName;
}
