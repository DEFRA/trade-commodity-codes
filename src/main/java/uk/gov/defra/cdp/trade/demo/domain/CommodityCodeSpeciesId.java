package uk.gov.defra.cdp.trade.demo.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommodityCodeSpeciesId implements Serializable {
  private String commodityCode;
  private int speciesId;
}
