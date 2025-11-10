package uk.gov.defra.cdp.trade.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommodityGroupDto {
  private String commodityCode;
  private String commodityGroup;
}
