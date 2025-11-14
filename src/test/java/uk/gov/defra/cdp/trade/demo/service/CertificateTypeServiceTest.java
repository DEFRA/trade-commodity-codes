package uk.gov.defra.cdp.trade.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.enumerations.CertificateTypeAndCodeMapping;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCodeRepository;

@ExtendWith(MockitoExtension.class)
class CertificateTypeServiceTest {

  private CertificateTypeService certificateTypeService;

  @Mock
  private CommodityCodeRepository commodityCodeRepository;

  private List<CommodityCode> daoReturnData;

  private static final String commodityCodes_1 = "01";
  private static final String commodityCodes_2 = "02";

  private static final CertificateTypeAndCodeMapping certType_CVED_A = CertificateTypeAndCodeMapping.CVED_A;
  private static final CertificateTypeAndCodeMapping certType_CVED_P = CertificateTypeAndCodeMapping.CVED_P;
  private static final CertificateTypeAndCodeMapping certType_CED = CertificateTypeAndCodeMapping.CED;
  private static final String CERT_TYPE_TO_BE_IGNORED_CHED_PP = "CHED-PP";
  private static final String INVALID_CERT_TYPE = "INVALID";

  private CommodityCode commodityCode_01_CVED_A;
  private CommodityCode commodityCode_02_CVED_P;
  private CommodityCode commodityCode_02_CED;
  private CommodityCode commodityCode_CHED_PP;

  @BeforeEach
  void setUp() {
    certificateTypeService = new CertificateTypeService(commodityCodeRepository);

    commodityCode_01_CVED_A = new CommodityCode();
    commodityCode_01_CVED_A.setCertType(certType_CVED_A.getCertType());
    commodityCode_01_CVED_A.setCode(commodityCodes_1);

    commodityCode_02_CVED_P = new CommodityCode();
    commodityCode_02_CVED_P.setCertType(certType_CVED_P.getCertType());
    commodityCode_02_CVED_P.setCode(commodityCodes_2);

    commodityCode_02_CED = new CommodityCode();
    commodityCode_02_CED.setCertType(certType_CED.getCertType());
    commodityCode_02_CED.setCode(commodityCodes_2);

    commodityCode_CHED_PP = new CommodityCode();
    commodityCode_CHED_PP.setCertType(CERT_TYPE_TO_BE_IGNORED_CHED_PP);
    commodityCode_CHED_PP.setCode(commodityCodes_1);
  }

  @Test
  void testGetAllCertTypes_withOneCommodityCode() {
    //Given
    daoReturnData = List.of(commodityCode_01_CVED_A);
    when(commodityCodeRepository.findCommodityCodeByCodeIn(List.of(commodityCodes_1))).thenReturn(daoReturnData);

    //When
    Map<String, List<Character>> actualResponse = certificateTypeService.getAllCertTypes(List.of(
        commodityCodes_1));

    Map<String, List<Character>> expectedData = Map.of(
        commodityCodes_1, List.of(certType_CVED_A.getCertCode())
    );
    assertThat(actualResponse).isEqualTo(expectedData);
  }

  @Test
  void testGetAllCertTypes_withTwoCommodityCodes() {
    //Given
    daoReturnData = List.of(commodityCode_01_CVED_A, commodityCode_02_CVED_P);
    when(commodityCodeRepository.findCommodityCodeByCodeIn(List.of(
        commodityCodes_1, commodityCodes_2))).thenReturn(daoReturnData);

    //When
    Map<String, List<Character>> actualResponse = certificateTypeService.getAllCertTypes(List.of(
        commodityCodes_1, commodityCodes_2));

    Map<String, List<Character>> expectedData = Map.of(
        commodityCodes_1, List.of(certType_CVED_A.getCertCode()),
        commodityCodes_2, List.of(certType_CVED_P.getCertCode())
    );
    assertThat(actualResponse).isEqualTo(expectedData);
  }

  @Test
  void testGetAllCertTypes_ForACommodityHavingTwoCertTypes() {
    //Given
    daoReturnData = List.of(commodityCode_02_CVED_P, commodityCode_02_CED);
    when(commodityCodeRepository.findCommodityCodeByCodeIn(List.of(commodityCodes_2))).thenReturn(daoReturnData);

    //When
    Map<String, List<Character>> actualResponse = certificateTypeService.getAllCertTypes(List.of(commodityCodes_2));

    Map<String, List<Character>> expectedData = Map.of(
        commodityCodes_2, List.of(certType_CVED_P.getCertCode(), certType_CED.getCertCode())
    );

    assertThat(actualResponse).isEqualTo(expectedData);
  }

  @Test
  void testGetAllCertTypes_returnEmptyDataForInvalidCertType() {
    //Given
    when(commodityCodeRepository.findCommodityCodeByCodeIn(List.of(commodityCodes_1))).thenReturn(List.of(commodityCode_CHED_PP));

    Map<String, List<Character>> actualResponse = certificateTypeService.getAllCertTypes(List.of(commodityCodes_1));

    assertThat(actualResponse).isEqualTo(Map.of());
  }

  @Test
  void testGetAllCertTypes_withAllCombinations() {
    //Given
    daoReturnData = List.of(commodityCode_01_CVED_A, commodityCode_02_CVED_P, commodityCode_02_CED, commodityCode_CHED_PP);
    when(commodityCodeRepository.findCommodityCodeByCodeIn(
        List.of(commodityCodes_1, commodityCodes_2, CERT_TYPE_TO_BE_IGNORED_CHED_PP, INVALID_CERT_TYPE))).thenReturn(daoReturnData);

    //When
    Map<String, List<Character>> actualResponse = certificateTypeService.getAllCertTypes(List.of(
        commodityCodes_1, commodityCodes_2, CERT_TYPE_TO_BE_IGNORED_CHED_PP, INVALID_CERT_TYPE));

    Map<String, List<Character>> expectedData = Map.of(
        commodityCodes_1, List.of(certType_CVED_A.getCertCode()),
        commodityCodes_2, List.of(certType_CVED_P.getCertCode(), certType_CED.getCertCode())
    );

    assertThat(actualResponse).isEqualTo(expectedData);
  }

}
