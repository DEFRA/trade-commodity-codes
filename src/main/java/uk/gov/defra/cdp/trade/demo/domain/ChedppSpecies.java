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
@Table(name = "v_chedpp_species")
@IdClass(CommodityCodeSpeciesId.class)
@Builder
public class ChedppSpecies {

  @Id
  private String commodityCode;

  private String eppoCode;
  @Id
  private int speciesId;

  private String speciesName;

  private String commodityDescription;
}
