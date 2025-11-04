package uk.gov.defra.cdp.trade.demo.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommodityCategoryId implements Serializable {
  private String certificateType;
  private String commodityCode;
}
