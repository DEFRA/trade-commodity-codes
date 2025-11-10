package uk.gov.defra.cdp.trade.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityCategoryDto {
  private String certificateType;
  private String commodityCode;
  private String data;

  public static CommodityCategoryDto from(CommodityCategory commodityCategory) {
    return CommodityCategoryDto.builder()
        .certificateType(commodityCategory.getCertificateType())
        .commodityCode(commodityCategory.getCommodityCode())
        .data(commodityCategory.getData())
        .build();
  }
}
