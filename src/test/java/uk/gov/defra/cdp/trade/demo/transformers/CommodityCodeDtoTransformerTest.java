package uk.gov.defra.cdp.trade.demo.transformers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCodeDto;

class CommodityCodeDtoTransformerTest {

  private static final String CERT_CVED_P = "CVED-P";

  @Test
  void from_ReturnsCommodityCodeDto_WhenCommodityCode() {
    //Given
    CommodityCode commodityCode = new CommodityCode();
    commodityCode.setCode("023");
    commodityCode.setDisplayCode("023");
    commodityCode.setDescription("MEAT AND EDIBLE MEAT OFFAL");
    commodityCode.setCertType("CVED-P");
    commodityCode.setCommodity(false);
    commodityCode.setParent(true);

    //When
    CommodityCodeDto commodityCodeDto = CommodityCodeDtoTransformer.from(commodityCode);

    //Then
    assertThat(commodityCodeDto).isEqualTo(CommodityCodeDto.builder()
        .code("023")
        .displayCode("023")
        .certificateType(CERT_CVED_P)
        .description("MEAT AND EDIBLE MEAT OFFAL")
        .displayCodeFull(new ArrayList<>())
        .isCommodity(false)
        .isParent(true)
        .build()
    );
  }
}
