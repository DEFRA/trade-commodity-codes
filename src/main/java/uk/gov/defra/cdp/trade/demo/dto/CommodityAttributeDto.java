package uk.gov.defra.cdp.trade.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommodityAttributeDto {
  private String commodityCode;
  private String propagation;
}
