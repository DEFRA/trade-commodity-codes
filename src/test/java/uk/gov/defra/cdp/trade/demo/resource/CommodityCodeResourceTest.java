package uk.gov.defra.cdp.trade.demo.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.COMMODITY_CODE;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.SPECIES_NAME;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryDataDto;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryDataDtoV2;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.cdp.trade.demo.domain.SearchVariety;
import uk.gov.defra.cdp.trade.demo.enumerations.ChedType;
import uk.gov.defra.cdp.trade.demo.dto.CommodityAttributeDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCodeDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityConfigurationDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityEppoCodesMappingDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupsDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDtoV2;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDto;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDtoV2;
import uk.gov.defra.cdp.trade.demo.fixtures.CommodityCodeFixtures;
import uk.gov.defra.cdp.trade.demo.fixtures.CommodityGroupFixtures;
import uk.gov.defra.cdp.trade.demo.service.CommodityAttributeService;
import uk.gov.defra.cdp.trade.demo.service.CommodityCodeService;
import uk.gov.defra.cdp.trade.demo.service.CommodityConfigurationService;
import uk.gov.defra.cdp.trade.demo.service.SupplementaryDataService;

class CommodityCodeResourceTest {

  private static final String VALID_EPPO_CODE = "MABSD";
  private static final String VALID_COMMODITY_CODE = "08081080";
  private static final String VALID_VARIETY = "Honey Belle";
  private static final String CERT_TYPE = "CHED-PP";
  private static final String COMMODITY_CODE_010202 = "010202";
  private static final String COMMODITY_10294023 = "10294023";

  private final CommodityCodeService commodityCodeService = mock(CommodityCodeService.class);
  private final SupplementaryDataService supplementaryDataService = mock(
      SupplementaryDataService.class
  );
  private final CommodityConfigurationService commodityConfigService = mock(
      CommodityConfigurationService.class
  );
  private final CommodityAttributeService commodityAttributeService = mock(CommodityAttributeService.class);

  private final CommodityCodeResource resource = new CommodityCodeResource(
      commodityCodeService, supplementaryDataService, commodityConfigService, commodityAttributeService
  );

  @Test
  void getByTopLevelReturnsCorrectPayload() {
    // Given
    List<CommodityCodeDto> commodityCodes = List.of(
        CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA
    );
    when(commodityCodeService.getTopLevel(anyString())).thenReturn(commodityCodes);

    // When
    List<CommodityCodeDto> response =
        resource.getTopLevel(CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, "");

    // Then
    assertThat(response).isEqualTo(commodityCodes);
  }

  @Test
  void getByTopLevel_ReturnsCorrectPayload_WhenCertTypeAndSpeciesArePassed() {
    // Given
    List<CommodityCodeDto> commodityCodes = List.of(
        CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA
    );
    when(commodityCodeService.getTopLevel(anyString(), anyString())).thenReturn(commodityCodes);

    // When
    List<CommodityCodeDto> response =
        resource.getTopLevel(CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, "06042020");

    // Then
    assertThat(response).isEqualTo(commodityCodes);
  }

  @Test
  void getByParentCode_ReturnsCorrectListOfCommodityCodes_WhenNoSpeciesParameter() {
    List<CommodityCodeDto> commodityCodes = CommodityCodeFixtures.getMultipleItemList();
    when(commodityCodeService.getByParentCode(
        CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, COMMODITY_CODE_010202))
        .thenReturn(commodityCodes);

    List<CommodityCodeDto> response = resource.getByParentCode(
        CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, COMMODITY_CODE_010202, null);

    assertThat(response).isEqualTo(commodityCodes);
    verify(commodityCodeService, never()).getByParentCodeAndSpecies(any(), any(), any());
  }

  @Test
  void getByParentCode_ReturnsCorrectListOfCommodityCodes_WhenSpeciesParameter() {
    List<CommodityCodeDto> commodityCodes = CommodityCodeFixtures.getMultipleItemList();
    when(commodityCodeService.getByParentCodeAndSpecies(
        CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, COMMODITY_CODE_010202, "Species"))
        .thenReturn(commodityCodes);

    List<CommodityCodeDto> response = resource.getByParentCode(
        CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, COMMODITY_CODE_010202, "Species");

    assertThat(response).isEqualTo(commodityCodes);
    verify(commodityCodeService, never()).getByParentCode(any(), any());
  }

  @Test
  void getByCommodityCodeReturnsCorrectListOfCommodityCodes() {
    List<CommodityCodeDto> commodityCodes = CommodityCodeFixtures.getMultipleItemList();

    when(commodityCodeService.getByCommodityCode(
        CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, COMMODITY_CODE_010202))
        .thenReturn(commodityCodes);

    List<CommodityCodeDto> response =
        resource.getByCommodityCode(
            CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, COMMODITY_CODE_010202);
    // Then
    assertThat(response).isEqualTo(commodityCodes);
  }

  @Test
  void getByAllParentsReturnsCorrectPayload() {
    // Given
    List<CommodityCodeDto> commodityCodes = List.of(
        CommodityCodeFixtures.COMMODITY_CODE_DTO_01_CVEDA,
        CommodityCodeFixtures.COMMODITY_CODE_DTO_0101_CVEDA
    );
    when(commodityCodeService.getAllParents(anyString(), anyString(), anyString())).thenReturn(commodityCodes);

    // When
    List<CommodityCodeDto> response =
        resource.getAllParents(CommodityCodeFixtures.CVED_A_FORMAT_PAYLOAD_CONFIG, "0101", "");

    // Then
    assertThat(response).isEqualTo(commodityCodes);
  }

  @Test
  void getSupplementalDataForEppoCodes_ReturnsCorrectResponse() {
    // Given
    List<SupplementaryDataDto> supplementaryDataDtoList = List.of(new SupplementaryDataDto());
    when(supplementaryDataService.getSupplementaryData(anyString(), anyList()))
        .thenReturn(supplementaryDataDtoList);

    // When
    ResponseEntity<List<SupplementaryDataDto>> response = resource
        .getSupplementalDataForEppoCodes(CERT_TYPE, VALID_COMMODITY_CODE,
            Collections.singletonList(VALID_EPPO_CODE));

    // Then
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(supplementaryDataDtoList);
  }

  @Test
  void postSupplementalDataForEppoCodes_ReturnsCorrectResponse() {
    // Given
    List<SupplementaryDataDto> supplementaryDataDtoList = List.of(new SupplementaryDataDto());
    when(supplementaryDataService.getSupplementaryData(anyString(), anyList()))
        .thenReturn(supplementaryDataDtoList);

    // When
    ResponseEntity<List<SupplementaryDataDto>> response = resource
        .postSupplementalDataForEppoCodes(CERT_TYPE, VALID_COMMODITY_CODE,
            Collections.singletonList(VALID_EPPO_CODE));

    // Then
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(supplementaryDataDtoList);
  }

  @Test
  void postSupplementalDataForEppoCodesVersion2_ReturnsCorrectResponse() {
    // Given
    List<SupplementaryDataDtoV2> supplementaryDataDtoV2List = List.of(new SupplementaryDataDtoV2());
    when(supplementaryDataService.getSupplementaryDataV2(anyList(), anyList()))
        .thenReturn(supplementaryDataDtoV2List);

    // When
    ResponseEntity<List<SupplementaryDataDtoV2>> response = resource
        .postSupplementalDataForEppoCodes(CERT_TYPE,
            Collections.singletonList(
                CommodityEppoCodesMappingDto.builder()
                    .commodityCode(VALID_COMMODITY_CODE)
                    .eppoCodes(Collections.singletonList(VALID_EPPO_CODE))
                    .build()
            ));

    // Then
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(supplementaryDataDtoV2List);
  }

  @Test
  void getSupplementalDataForEppoCodesV2_ReturnsCorrectResponse() {
    // Given
    List<SupplementaryDataDtoV2> supplementaryDataDtoList = List.of(new SupplementaryDataDtoV2());
    when(supplementaryDataService.getSupplementaryDataV2(anyString(), anyList()))
        .thenReturn(supplementaryDataDtoList);

    // When
    ResponseEntity<List<SupplementaryDataDtoV2>> response = resource
        .getSupplementalDataForEppoCodesV2(CERT_TYPE, VALID_COMMODITY_CODE,
            List.of(VALID_EPPO_CODE));

    // Then
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(supplementaryDataDtoList);
  }

  @Test
  void postSupplementalDataForEppoCodesV2_ReturnsCorrectResponse() {
    // Given
    List<SupplementaryDataDtoV2> supplementaryDataDtoList = List.of(new SupplementaryDataDtoV2());
    when(supplementaryDataService.getSupplementaryDataV2(anyString(), anyList()))
        .thenReturn(supplementaryDataDtoList);

    // When
    ResponseEntity<List<SupplementaryDataDtoV2>> response = resource
        .postSupplementalDataForEppoCodesV2(CERT_TYPE, VALID_COMMODITY_CODE,
            List.of(VALID_EPPO_CODE));

    // Then
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(supplementaryDataDtoList);
  }

  @Test
  void getSupplementalDataForSpeciesName_ReturnsResponseOkWithCorrectSupplementalData() {
    when(supplementaryDataService.getSupplementaryData(COMMODITY_CODE, SPECIES_NAME))
        .thenReturn(supplementaryDataDto());

    ResponseEntity<SupplementaryDataDto> responseEntity =
        resource.getSupplementalDataForSpeciesName(COMMODITY_CODE, SPECIES_NAME);

    assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(responseEntity.getBody()).isEqualTo(supplementaryDataDto());
  }

  @Test
  void getSupplementalDataForSpeciesNameV2_ReturnsCorrectResponse() {
    when(supplementaryDataService.getSupplementaryDataV2(COMMODITY_CODE, SPECIES_NAME))
        .thenReturn(supplementaryDataDtoV2());

    ResponseEntity<SupplementaryDataDtoV2> responseEntity =
        resource.getSupplementalDataForSpeciesNameV2(COMMODITY_CODE, SPECIES_NAME);

    assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(responseEntity.getBody()).isEqualTo(supplementaryDataDtoV2());
  }

  @Test
  void getVarietyMarketingStandard_ReturnsCorrectResponse_WhenCalledWithValidPayload() {
    // Given
    List<CommodityVarietyDto> commodityVarietyDtoList = List.of(new CommodityVarietyDto());
    List<SearchVariety> searchVarieties = List.of(
        SearchVariety.builder().eppoCode(VALID_EPPO_CODE).variety(VALID_VARIETY).build()
    );

    when(supplementaryDataService.getVarietyMarketingStandard(anyString(), anyList()))
        .thenReturn(commodityVarietyDtoList);

    // When
    ResponseEntity<List<CommodityVarietyDto>> response = resource
        .getVarietyMarketingStandard(CERT_TYPE, VALID_COMMODITY_CODE, searchVarieties);

    // Then
    verify(supplementaryDataService)
        .getVarietyMarketingStandard(VALID_COMMODITY_CODE, searchVarieties);
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(commodityVarietyDtoList);
  }

  @Test
  void getVarietyMarketingStandard_ReturnsCorrectResponse_WhenCalledWithInvalidPayload() {
    // Given
    List<SearchVariety> searchVarieties = List.of(
        SearchVariety.builder()
            .eppoCode("")
            .variety(VALID_VARIETY)
            .build(),
        SearchVariety.builder()
            .eppoCode(null)
            .variety(VALID_VARIETY)
            .build(),
        SearchVariety.builder()
            .eppoCode(VALID_EPPO_CODE)
            .variety("")
            .build(),
        SearchVariety.builder()
            .eppoCode(VALID_EPPO_CODE)
            .variety(null)
            .build());

    // When
    ResponseEntity<List<CommodityVarietyDto>> response = resource
        .getVarietyMarketingStandard(CERT_TYPE, VALID_COMMODITY_CODE, searchVarieties);

    // Then
    verify(supplementaryDataService, never()).getVarietyMarketingStandard(anyString(), anyList());
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(Collections.emptyList());
  }

  @Test
  void getVarietyMarketingStandardV2_ReturnsCorrectResponse_WhenCalledWithValidPayload() {
    // Given
    List<CommodityVarietyDtoV2> commodityVarietyDtoList = List.of(new CommodityVarietyDtoV2());
    List<SearchVariety> searchVarieties = List.of(
        SearchVariety.builder().eppoCode(VALID_EPPO_CODE).variety(VALID_VARIETY).build()
    );
    when(supplementaryDataService.getVarietyMarketingStandardV2(anyString(), anyList()))
        .thenReturn(commodityVarietyDtoList);

    // When
    ResponseEntity<List<CommodityVarietyDtoV2>> response = resource.getVarietyMarketingStandardV2(
        CERT_TYPE, VALID_COMMODITY_CODE, searchVarieties);

    // Then
    verify(supplementaryDataService).getVarietyMarketingStandardV2(VALID_COMMODITY_CODE,
        searchVarieties);
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(commodityVarietyDtoList);
  }

  @Test
  void getVarietyMarketingStandardV2_ReturnsCorrectResponse_WhenCalledWithInvalidPayload() {
    // Given
    List<SearchVariety> searchVarieties = List.of(
        SearchVariety.builder()
            .eppoCode("")
            .variety(VALID_VARIETY)
            .build(),
        SearchVariety.builder()
            .eppoCode(null)
            .variety(VALID_VARIETY)
            .build(),
        SearchVariety.builder()
            .eppoCode(VALID_EPPO_CODE)
            .variety("")
            .build(),
        SearchVariety.builder()
            .eppoCode(VALID_EPPO_CODE)
            .variety(null)
            .build());

    // When
    ResponseEntity<List<CommodityVarietyDtoV2>> response = resource
        .getVarietyMarketingStandardV2(CERT_TYPE, VALID_COMMODITY_CODE, searchVarieties);

    // Then
    verify(supplementaryDataService, never()).getVarietyMarketingStandardV2(anyString(), anyList());
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(Collections.emptyList());
  }

  @Test
  void getCommodityGroups_ShouldReturnCorrectResponse_WhenCommodityGroupsAreFound() {
    // Given
    List<String> commodityCodes = List.of(COMMODITY_10294023);
    when(commodityCodeService.getCommodityGroups(Collections.singletonList(COMMODITY_10294023)))
        .thenReturn(List.of(CommodityGroupFixtures.commodityGroupsDto()));

    // When
    var response = resource.getCommodityGroups(commodityCodes);

    // Then
    List<CommodityGroupsDto> expected = List.of(CommodityGroupFixtures.commodityGroupsDto());
    assertThat(response).containsExactlyElementsOf(expected);
    verify(commodityCodeService).getCommodityGroups(commodityCodes);
  }

  @Test
  void getCommodityConfiguration_returnsCommodityCodeConfiguration_whenGivenCommodityCodes() {
    var commodityCodes = List.of(COMMODITY_10294023);
    var commodityConfigurations = List.of(mock(CommodityConfigurationDto.class));

    when(commodityConfigService.getCommodityConfigurations(anyList(), any()))
        .thenReturn(commodityConfigurations);

    List<CommodityConfigurationDto> response = resource.getCommodityConfiguration(commodityCodes);

    assertThat(response).isEqualTo(commodityConfigurations);
    verify(commodityConfigService).getCommodityConfigurations(commodityCodes, ChedType.CHEDPP);
  }

  @Test
  void getCommodityGroup_ShouldReturnCorrectResponse_WhenCommodityGroupsAreFound() {
    // Given
    List<String> commodityCodes = List.of(COMMODITY_10294023);
    when(commodityCodeService.getCommodityGroup(Collections.singletonList(COMMODITY_10294023)))
        .thenReturn(List.of(CommodityGroupFixtures.commodityGroupDto()));

    // When
    var response = resource.getCommodityGroup(commodityCodes);

    // Then
    List<CommodityGroupDto> expected = List.of(CommodityGroupFixtures.commodityGroupDto());
    assertThat(response).containsExactlyElementsOf(expected);
    verify(commodityCodeService).getCommodityGroup(commodityCodes);
  }

  @Test
  void getCommodityAttributes_returnsGetCommodityAttributes_whenGivenCommodityCodes() {
    List<String> commodityCodes = List.of(COMMODITY_10294023);
    List<CommodityAttributeDto> commodityAttributes = List.of(mock(CommodityAttributeDto.class));

    when(commodityAttributeService.getCommodityAttributes(anyList()))
            .thenReturn(commodityAttributes);

    List<CommodityAttributeDto> response = resource.getCommodityAttributes(commodityCodes);

    assertThat(response).isEqualTo(commodityAttributes);
    verify(commodityAttributeService).getCommodityAttributes(commodityCodes);
  }
}
