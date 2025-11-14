package uk.gov.defra.cdp.trade.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCodeRepository;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityGroupRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCodeDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupsDto;
import uk.gov.defra.cdp.trade.demo.fixtures.CommodityCodeFixtures;
import uk.gov.defra.cdp.trade.demo.fixtures.CommodityGroupFixtures;

@ExtendWith(MockitoExtension.class)
class CommodityCodeServiceTest {

  private static final String CERT_CVED_A = "CVED-A";
  private static final String CERT_CHED_PP = "CHED-PP";
  private static final String PATH_CVEDA = "cveda";
  private static final String CVEDA_UPPERCASE = "CVEDA";
  private static final String PATH_CVEDP = "cvedp";
  private static final String CERT_CVED_P = "CVED-P";
  private static final String CVEDP_UPPERCASE = "CVEDP";
  private static final String PATH_CED = "ced";
  private static final String CERT_CED = "CED";
  private static final String PATH_CHEDPP = "chedpp";

  @Mock
  private CommodityCodeRepository commodityCodeRepository;
  @Mock
  private CommodityGroupRepository commodityGroupRepository;
  private CommodityCodeService commodityCodeService;

  @BeforeEach
  void setUp() {
    commodityCodeService = new CommodityCodeService(commodityCodeRepository,
        commodityGroupRepository);
  }


  @Test
  void getTopLevelReturnsListOfCommodityCodes() {
    // Given
    List<CommodityCode> mockCommodityCodes =
        Collections.singletonList(CommodityCodeFixtures.COMMODITY_CODE_DB_01_CVEDA);
    when(commodityCodeRepository
        .findAllByCertTypeIsLikeAndImmediateParentIsNullOrderByCode(anyString()))
        .thenReturn(mockCommodityCodes);

    // When
    List<CommodityCodeDto> results = commodityCodeService.getTopLevel(CERT_CVED_A);

    // Then
    verify(commodityCodeRepository, times(1))
        .findAllByCertTypeIsLikeAndImmediateParentIsNullOrderByCode(anyString());
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).equals(CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA)).isTrue();
  }

  @Test
  void getTopLevelReturnsEmptyListOfCommodityCodes() {
    // Given

    // When
    List<CommodityCodeDto> results = commodityCodeService.getTopLevel(CERT_CVED_A);

    // Then

    assertThat(results.isEmpty()).isTrue();
  }

  @Test
  void getTopLevelReturnsCorrectlyFormattedCertTypeForCVEDA() {
    // Given
    List<CommodityCode> mockCommodityCodes =
        Collections.singletonList(CommodityCodeFixtures.COMMODITY_CODE_DB_01_CVEDA);
    when(commodityCodeRepository
        .findAllByCertTypeIsLikeAndImmediateParentIsNullOrderByCode(CERT_CVED_A))
        .thenReturn(mockCommodityCodes);

    // When
    List<CommodityCodeDto> results = commodityCodeService.getTopLevel(PATH_CVEDA);

    // Then
    assertThat(results.get(0).getCertificateType()).isEqualTo(CERT_CVED_A);
  }

  @Test
  void getTopLevelReturnsCorrectlyFormattedCertTypeForCVEDP() {
    // Given
    List<CommodityCode> mockCommodityCodes =
        Collections.singletonList(CommodityCodeFixtures.COMMODITY_CODE_DB_02_CVEDP);
    when(commodityCodeRepository
        .findAllByCertTypeIsLikeAndImmediateParentIsNullOrderByCode(CERT_CVED_P))
        .thenReturn(mockCommodityCodes);

    // When
    List<CommodityCodeDto> results = commodityCodeService.getTopLevel(PATH_CVEDP);

    // Then
    assertThat(results.get(0).getCertificateType()).isEqualTo(CERT_CVED_P);
  }

  @Test
  void getTopLevelReturnsCorrectlyFormattedCertTypeForOthers() {
    // Given
    List<CommodityCode> mockCommodityCodes =
        Collections.singletonList(CommodityCodeFixtures.COMMODITY_CODE_DB_01_CED);
    when(commodityCodeRepository
        .findAllByCertTypeIsLikeAndImmediateParentIsNullOrderByCode(CERT_CED))
        .thenReturn(mockCommodityCodes);

    // When
    List<CommodityCodeDto> results = commodityCodeService.getTopLevel(PATH_CED);

    // Then
    assertThat(results.get(0).getCertificateType()).isEqualTo(CERT_CED);
  }

  @Test
  final void shouldReturnListOfCommodityCodesByParentCode() {
    // Given
    List<CommodityCode> commodityCodes = CommodityCodeFixtures.getMultipleCommodityCodes();
    List<CommodityCodeDto> expectedResult = CommodityCodeFixtures.getMultipleItemList();

    when(commodityCodeRepository.findAllByCertTypeIsLikeAndImmediateParentIsLikeOrderByCode(
        CERT_CVED_A, CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA.getCode()))
        .thenReturn(commodityCodes);

    // When
    List<CommodityCodeDto> result =
        commodityCodeService.getByParentCode(
            CVEDA_UPPERCASE, CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA.getCode());

    // Then
    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  void shouldFormatCertificateTypeWhenGettingByParentCode() {
    // Given
    List<CommodityCode> commodityCodes = CommodityCodeFixtures.getMultipleCommodityCodes();

    final Map<String, String> expectedMapping =
        ImmutableMap.<String, String>builder()
            .put(CVEDA_UPPERCASE, CERT_CVED_A)
            .put(CVEDP_UPPERCASE, CERT_CVED_P)
            .put(PATH_CVEDA, CERT_CVED_A)
            .put(PATH_CVEDP, CERT_CVED_P)
            .build();

    for (String certType : expectedMapping.keySet()) {
      ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
      when(commodityCodeRepository
          .findAllByCertTypeIsLikeAndImmediateParentIsLikeOrderByCode(captor.capture(),
              anyString()))
          .thenReturn(commodityCodes);

      // When
      commodityCodeService.getByParentCode(
          certType, CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA.getCode());

      // Then
      assertThat(captor.getValue()).isEqualTo(expectedMapping.get(certType));
    }
  }

  @Test
  void getByParentCodeAndSpecies_ShouldReturnListOfCommodityCodes() {
    // Given
    List<CommodityCode> commodityCodes = CommodityCodeFixtures.getMultipleCommodityCodes();
    List<CommodityCodeDto> expectedResult = CommodityCodeFixtures.getMultipleItemList();

    when(commodityCodeRepository.findChildCommoditiesByCertTypeAndSpecies(
        CERT_CVED_A, "Commodity code", "Species"))
        .thenReturn(commodityCodes);

    when(commodityCodeRepository.findAllCommoditiesByCodeCertTypeAndSpecies(
        anyString(), anyString(), anyString()))
        .thenReturn(commodityCodes);

    // When
    List<CommodityCodeDto> result = commodityCodeService.getByParentCodeAndSpecies(CVEDA_UPPERCASE,
        "Commodity code", "Species");

    // Then
    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  void getByParentCodeAndSpecies_SetsCommodityToFalse_WhenSpeciesNotPresent() {
    // Given
    List<CommodityCode> commodityCodes = CommodityCodeFixtures.getMultipleCommodityCodesForChedpp();
    List<CommodityCodeDto> expectedResult = List.of(CommodityCodeDto
        .builder()
        .code("12099180")
        .certificateType("CHED-PP")
        .description("apple tree (ornamental)")
        .displayCode("10")
        .isCommodity(false)
        .isParent(true)
        .displayCodeFull(List.of("12", "09", "91", "80")).build());

    when(commodityCodeRepository.findChildCommoditiesByCertTypeAndSpecies(
        CERT_CHED_PP, "Commodity code", "Species"))
        .thenReturn(commodityCodes);

    when(commodityCodeRepository.findAllCommoditiesByCodeCertTypeAndSpecies(
        anyString(), anyString(), anyString()))
        .thenReturn(Collections.emptyList());

    // When
    List<CommodityCodeDto> result = commodityCodeService.getByParentCodeAndSpecies(CERT_CHED_PP,
        "Commodity code", "Species");

    // Then
    assertThat(result).isEqualTo(expectedResult);
    verify(commodityCodeRepository, times(1))
        .findAllCommoditiesByCodeCertTypeAndSpecies(anyString(), anyString(), anyString());
  }

  @Test
  void getByParentCodeAndSpecies_ShouldFormatCertificateType() {
    // Given
    Map.of(
        CVEDA_UPPERCASE, CERT_CVED_A,
        CVEDP_UPPERCASE, CERT_CVED_P,
        PATH_CVEDA, CERT_CVED_A,
        PATH_CVEDP, CERT_CVED_P
    ).forEach((inputType, expectedType) -> {
      ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
      when(commodityCodeRepository.findChildCommoditiesByCertTypeAndSpecies(
          captor.capture(), anyString(), anyString())).thenReturn(List.of());

      // When
      commodityCodeService.getByParentCodeAndSpecies(inputType, "Commodity code", "Species");

      // Then
      assertThat(captor.getValue()).isEqualTo(expectedType);
    });
  }

  @Test
  final void shouldReturnListOfCommodityCodesByCommodityCode() {
    // Given
    List<CommodityCode> commodityCodes = CommodityCodeFixtures.getMultipleCommodityCodes();
    List<CommodityCodeDto> expectedResult = CommodityCodeFixtures.getMultipleItemList();

    when(commodityCodeRepository
        .findAllByCertTypeIsLikeAndCodeIsLikeOrderByCode(eq(CERT_CVED_A), anyString()))
        .thenReturn(commodityCodes);

    // When
    List<CommodityCodeDto> result =
        commodityCodeService.getByCommodityCode(
            CVEDA_UPPERCASE, CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA.getCode());

    // Then
    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  void shouldFormatCertificateTypeWhenGettingByCommodityCode() {
    // Given
    List<CommodityCode> commodityCodes = CommodityCodeFixtures.getMultipleCommodityCodes();

    final Map<String, String> expectedMapping =
        ImmutableMap.<String, String>builder()
            .put(CVEDA_UPPERCASE, CERT_CVED_A)
            .put(CVEDP_UPPERCASE, CERT_CVED_P)
            .put(PATH_CVEDA, CERT_CVED_A)
            .put(PATH_CVEDP, CERT_CVED_P)
            .build();

    for (String certType : expectedMapping.keySet()) {
      ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
      when(commodityCodeRepository
          .findAllByCertTypeIsLikeAndCodeIsLikeOrderByCode(captor.capture(), anyString()))
          .thenReturn(commodityCodes);

      // When
      commodityCodeService.getByCommodityCode(
          certType, CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA.getCode());

      // Then
      assertThat(captor.getValue()).isEqualTo(expectedMapping.get(certType));
    }
  }

  @Test
  void getAllParentsReturnsListOfCommodityCodesOfParents() {
    // Given
    when(commodityCodeRepository
        .findCommodityCodeByCertTypeIsLikeAndCodeIsOrderByCode(anyString(), anyString()))
        .thenReturn(CommodityCodeFixtures.COMMODITY_CODE_DB_0101_CVEDA)
        .thenReturn(CommodityCodeFixtures.COMMODITY_CODE_DB_01_CVEDA);

    // When
    List<CommodityCodeDto> results = commodityCodeService.getAllParents(CERT_CVED_A, "0101", null);

    // Then
    verify(commodityCodeRepository, times(2))
        .findCommodityCodeByCertTypeIsLikeAndCodeIsOrderByCode(anyString(), anyString());
    assertThat(results.size()).isEqualTo(2);
    assertThat(results.get(0)).isEqualTo(CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA);
    assertThat(results.get(1)).isEqualTo(CommodityCodeFixtures.COMMODITY_CODE_DTO_0101_CVEDA);
  }

  @Test
  void getAllParentsReturnsEmptyListOfCommodityCodes() {
    // Given
    // When
    List<CommodityCodeDto> emptyCodeResults = commodityCodeService.getAllParents(CERT_CVED_A, "",
        null);
    List<CommodityCodeDto> nullCodeResults = commodityCodeService.getAllParents(CERT_CVED_A, null,
        null);
    List<CommodityCodeDto> randomCodeResults = commodityCodeService.getAllParents(CERT_CVED_A,
        "bhy23", null);
    List<CommodityCodeDto> randomCertType = commodityCodeService.getAllParents("abcd", "0101",
        null);
    List<CommodityCodeDto> emptyCertType = commodityCodeService.getAllParents("", "0101", null);

    // Then
    assertThat(emptyCodeResults).isEmpty();
    assertThat(nullCodeResults).isEmpty();
    assertThat(randomCodeResults).isEmpty();
    assertThat(randomCertType).isEmpty();
    assertThat(emptyCertType).isEmpty();
  }

  @Test
  void getAllParents_SetsCommodityToFalse_WhenSpeciesNotPresent() {
    // Given
    List<CommodityCodeDto> expectedResult = List.of(CommodityCodeDto
        .builder()
        .code("12")
        .certificateType("CHED-PP")
        .description("apple tree (ornamental)")
        .displayCode("09")
        .isCommodity(false)
        .isParent(true)
        .displayCodeFull(List.of("12")).build());

    when(commodityCodeRepository
        .findCommodityCodeByCertTypeIsLikeAndCodeIsOrderByCode(eq(CERT_CHED_PP), anyString()))
        .thenReturn(CommodityCodeFixtures.COMMODITY_CODE_DB_12_CHEDPP)
        .thenReturn(CommodityCodeFixtures.COMMODITY_CODE_DB_1209_CHEDPP);

    when(commodityCodeRepository
        .findAllCommoditiesByCodeCertTypeAndSpecies(anyString(), anyString(), anyString()))
        .thenReturn(Collections.emptyList());

    // When
    List<CommodityCodeDto> results = commodityCodeService.getAllParents(CERT_CHED_PP, "12099180",
        "apple tree (ornamental");

    // Then
    verify(commodityCodeRepository, times(1))
        .findCommodityCodeByCertTypeIsLikeAndCodeIsOrderByCode(eq(CERT_CHED_PP), anyString());
    verify(commodityCodeRepository, times(1))
        .findAllCommoditiesByCodeCertTypeAndSpecies(anyString(), anyString(), anyString());
    assertThat(results.size()).isEqualTo(1);
    assertThat(results).isEqualTo(expectedResult);
  }

  @Test
  void testIOExceptionOnObjectMapper() throws IOException {
    // Given
    commodityCodeService = new CommodityCodeService(commodityCodeRepository,
        commodityGroupRepository);

    // When
    List<CommodityCodeDto> results = commodityCodeService.getTopLevel(PATH_CHEDPP);

    // Then
    assertThat(results).isEmpty();
  }

  @Test
  void testJsonProcessingExceptionOnObjectMapper() throws IOException {
    // Given
    commodityCodeService = new CommodityCodeService(commodityCodeRepository,
        commodityGroupRepository);

    // When
    List<CommodityCodeDto> results = commodityCodeService.getTopLevel(PATH_CHEDPP);

    // Then
    assertThat(results).isEmpty();
  }

  @Test
  void getCommodityGroupsShouldReturnAnEmptyListWhenNoCommodityGroupsAreFound() {
    // Given
    List<String> commodityCodes = Arrays.asList("000001", "000002");
    when(commodityGroupRepository.findDistinctByCommodityCodeInOrderByCommodityCode(commodityCodes))
        .thenReturn(Collections.emptyList());

    // When
    List<CommodityGroupsDto> results = commodityCodeService.getCommodityGroups(commodityCodes);

    // Then
    assertThat(results).isEmpty();
  }

  @Test
  void getCommodityGroupsShouldReturnAListOfCommodityGroupDtos() {
    // Given
    List<String> commodityCodes = Arrays.asList("000001", "000002");
    when(commodityGroupRepository.findDistinctByCommodityCodeInOrderByCommodityCode(commodityCodes))
        .thenReturn(Collections.singletonList(CommodityGroupFixtures.commodityGroups()));

    // When
    List<CommodityGroupsDto> results = commodityCodeService.getCommodityGroups(commodityCodes);

    // Then
    assertThat(results.get(0)).isEqualTo(CommodityGroupFixtures.commodityGroupsDto());
  }

  @Test
  void getCommodityGroup_ShouldReturnAnEmptyList_WhenNoCommodityGroupsAreFound() {
    // Given
    List<String> commodityCodes = Arrays.asList("000001", "000002");
    when(commodityGroupRepository.findDistinctByCommodityCodeInOrderByCommodityCode(commodityCodes))
        .thenReturn(Collections.emptyList());

    // When
    List<CommodityGroupDto> results = commodityCodeService.getCommodityGroup(commodityCodes);

    // Then
    assertThat(results).isEmpty();
  }

  @Test
  void getCommodityGroup_ShouldReturnAListOfCommodityGroupDtos_WhenNoCommodityGroupsAreFound() {
    // Given
    List<String> commodityCodes = Arrays.asList("000001", "000002");
    when(commodityGroupRepository.findDistinctByCommodityCodeInOrderByCommodityCode(commodityCodes))
        .thenReturn(Collections.singletonList(CommodityGroupFixtures.commodityGroup()));

    // When
    List<CommodityGroupDto> results = commodityCodeService.getCommodityGroup(commodityCodes);

    // Then
    assertThat(results.get(0)).isEqualTo(CommodityGroupFixtures.commodityGroupDto());
  }
}
