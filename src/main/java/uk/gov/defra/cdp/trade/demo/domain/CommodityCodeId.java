package uk.gov.defra.cdp.trade.demo.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommodityCodeId implements Serializable {

  private String code;
  private String certType;
}
