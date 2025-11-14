package uk.gov.defra.cdp.trade.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Article72CommodityDto {

  private String commodityCode;
  private String eppoCode;
  private String commodityGroup;
  private boolean isLowRiskArticle72;
}
