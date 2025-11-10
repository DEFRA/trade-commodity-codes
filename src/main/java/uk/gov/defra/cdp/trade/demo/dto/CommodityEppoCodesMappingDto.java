package uk.gov.defra.cdp.trade.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommodityEppoCodesMappingDto {

  private String commodityCode;
  private List<String> eppoCodes;
}
