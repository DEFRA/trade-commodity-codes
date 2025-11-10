package uk.gov.defra.cdp.trade.demo.transformers;

import java.util.ArrayList;
import java.util.List;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCodeDto;

public class CommodityCodeDtoTransformer {

  private CommodityCodeDtoTransformer() {
    throw new IllegalStateException("Utility class");
  }

  public static CommodityCodeDto from(CommodityCode commodityCode) {
    List<String> displayCodeFull = null;
    if (!commodityCode.getDisplayCode().isEmpty()) {
      displayCodeFull = parseCommodityCode(commodityCode.getCode());
    }

    return CommodityCodeDto.builder()
        .code(commodityCode.getCode())
        .displayCode(commodityCode.getDisplayCode())
        .displayCodeFull(displayCodeFull)
        .description(commodityCode.getDescription())
        .certificateType(commodityCode.getCertType())
        .parentCode(commodityCode.getImmediateParent())
        .isCommodity(commodityCode.isCommodity())
        .isParent(commodityCode.isParent())
        .build();
  }

  private static List<String> parseCommodityCode(String commodityCode) {
    List<String> commodityCodeParts = new ArrayList<>();

    // validate data, must be even number
    if (commodityCode.length() % 2 == 1) {
      return commodityCodeParts;
    }

    // otherwise all is ok so far
    for (int looper = 0; looper < commodityCode.length(); looper += 2) {
      String partialCode = commodityCode.substring(looper, looper + 2);
      commodityCodeParts.add(partialCode);
    }

    return commodityCodeParts;
  }
}
