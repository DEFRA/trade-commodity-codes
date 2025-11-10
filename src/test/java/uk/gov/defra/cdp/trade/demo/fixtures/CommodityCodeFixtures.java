package uk.gov.defra.cdp.trade.demo.fixtures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCodeDto;

public class CommodityCodeFixtures {

  public static final String CVED_A_FORMAT_PAYLOAD_CONFIG = "cveda";

  private static CommodityCode commodityCode(String code, String displayCode, String description,
      String certType, String immediateParent, boolean isCommodity, boolean isParent) {
    CommodityCode commodityCode = new CommodityCode();
    commodityCode.setCode(code);
    commodityCode.setDisplayCode(displayCode);
    commodityCode.setDescription(description);
    commodityCode.setCertType(certType);
    commodityCode.setImmediateParent(immediateParent);
    commodityCode.setCommodity(isCommodity);
    commodityCode.setParent(isParent);
    return commodityCode;
  }

  public static CommodityCodeDto COMMODITY_CODE_DTO_01_CVEDA =
      new CommodityCodeDto(
          "01",
          "01",
          Arrays.asList("01"),
          "LIVE ANIMALS",
          "CVED-A",
          null,
          false,
          true);
  public static final CommodityCodeDto COMMODITY_CODE_DTO_12099180_CHEDPP =
      new CommodityCodeDto(
          "12099180",
          "10",
          Arrays.asList("12", "09", "91", "80"),
          "apple tree (ornamental)",
          "CHED-PP",
          null,
          true,
          true);

  public static final CommodityCode COMMODITY_CODE_DB_12_CHEDPP =
      commodityCode(
          "12",
          "09",
          "apple tree (ornamental)",
          "CHED-PP",
          null,
          true,
          true);

  public static final CommodityCode COMMODITY_CODE_DB_1209_CHEDPP =
      commodityCode(
          "12099180",
          "10",
          "apple tree (ornamental)",
          "CHED-PP",
          "120991",
          true,
          true);

  public static final CommodityCodeDto COMMODITY_CODE_DTO_0101_CVEDA =
      new CommodityCodeDto(
          "0101",
          "01",
          Arrays.asList("01", "01"),
          "Live horses, asses, mules and hinnies",
          "CVED-A",
          "01",
          true,
          false);
  public static final CommodityCodeDto COMMODITY_CODE_DTO_0102_CVEDA =
      new CommodityCodeDto(
          "0102",
          "02",
          Arrays.asList("01", "02"),
          "Live bovine animals",
          "CVED-A",
          "01",
          true,
          false);

  public static CommodityCode COMMODITY_CODE_DB_01_CVEDA =
      commodityCode(
          "01",
          "01",
          "LIVE ANIMALS",
          "CVED-A",
          null,
          false,
          true);
  public static CommodityCode COMMODITY_CODE_DB_02_CVEDP =
      commodityCode(
          "02",
          "02",
          "MEAT AND EDIBLE MEAT OFFAL",
          "CVED-P",
          null,
          false,
          true);
  public static CommodityCode COMMODITY_CODE_DB_01_CED =
      commodityCode(
          "01",
          "01",
          "LIVE ANIMALS",
          "CED",
          null,
          false,
          true);
  public static CommodityCode COMMODITY_CODE_DB_0101_CVEDA =
      commodityCode(
          "0101",
          "01",
          "Live horses, asses, mules and hinnies",
          "CVED-A",
          "01",
          true,
          false);


  public static List<CommodityCodeDto> getMultipleItemList() {

    List<CommodityCodeDto> commodityCodes = new ArrayList<>();
    commodityCodes.add(COMMODITY_CODE_DTO_01_CVEDA);
    commodityCodes.add(COMMODITY_CODE_DTO_0101_CVEDA);
    commodityCodes.add(COMMODITY_CODE_DTO_0102_CVEDA);

    return commodityCodes;
  }

  public static List<CommodityCode> getMultipleCommodityCodes() {
    return getMultipleItemList()
        .stream()
        .map(CommodityCodeFixtures::fromDTO)
        .toList();
  }

  public static List<CommodityCode> getMultipleCommodityCodesForChedpp() {
    List<CommodityCodeDto> commodityCodeDtos = List.of(COMMODITY_CODE_DTO_12099180_CHEDPP);
    return commodityCodeDtos
        .stream()
        .map(CommodityCodeFixtures::fromDTO)
        .toList();
  }

  public static CommodityCode fromDTO(CommodityCodeDto commodityCodeDTO) {
    return commodityCode(
        commodityCodeDTO.getCode(),
        commodityCodeDTO.getDisplayCode(),
        commodityCodeDTO.getDescription(),
        commodityCodeDTO.getCertificateType(),
        commodityCodeDTO.getParentCode(),
        commodityCodeDTO.isCommodity(),
        commodityCodeDTO.isParent());
  }

  public static String matchOnlyFromStart(String searchTerm) {
    return searchTerm + "%";
  }
}
