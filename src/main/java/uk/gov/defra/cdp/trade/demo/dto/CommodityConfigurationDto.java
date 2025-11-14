package uk.gov.defra.cdp.trade.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommodityConfigurationDto {

  private String commodityCode;
  private boolean requiresTestAndTrial;
  private boolean requiresFinishedOrPropagated;
}
