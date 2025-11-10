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
public class CommodityCodeDto {

  private String code;
  private String displayCode;
  private List<String> displayCodeFull;
  private String description;
  private String certificateType;
  private String parentCode;
  private boolean isCommodity;
  private boolean isParent;
}
