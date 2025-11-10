package uk.gov.defra.cdp.trade.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.defra.cdp.trade.demo.integration.API.getAllParentsUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getByCommodityCodeUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getByParentCodeUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getCommodityAttributesUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getCommodityCategoriesUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getCommodityGroupUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getCommodityGroupsUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getSupplementalPostUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getSupplementalPostUrlV2;
import static uk.gov.defra.cdp.trade.demo.integration.API.getSupplementalUrl;
import static uk.gov.defra.cdp.trade.demo.integration.API.getSupplementalUrlForSpeciesName;
import static uk.gov.defra.cdp.trade.demo.integration.API.getSupplementalUrlForSpeciesNameV2;
import static uk.gov.defra.cdp.trade.demo.integration.API.getSupplementalUrlV2;
import static uk.gov.defra.cdp.trade.demo.integration.API.getSupplementalUrlV2UsingMappings;
import static uk.gov.defra.cdp.trade.demo.integration.API.getTopLevelUrl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import uk.gov.defra.cdp.trade.demo.domain.IntendedUseRegulatoryAuthority;
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
import uk.gov.defra.cdp.trade.demo.enumerations.MarketingStandard;
import uk.gov.defra.cdp.trade.demo.enumerations.RegulatoryAuthority;

class CommodityCodeEndpointsIT extends IntegrationBase {

  private static final String CERT_TYPE = "CVEDA";
  private static final String CHEDPP_CERT_TYPE = "CHEDPP";
  private static final String COMMODITY_CODE = "01";

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void getTopLevelRespondsWithCorrectPayload() {
    List<CommodityCodeDto> expectedCommodityCodes = getTopLevelListMapping().get("01");

    String url = getTopLevelUrl(CERT_TYPE);

    var response = getCommodityCode(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    List<CommodityCodeDto> commodityCodes =
        getResponseAsList(response.getResponseBodyContent(), CommodityCodeDto.class);
    assertThat(commodityCodes).containsExactlyElementsOf(expectedCommodityCodes);
  }

  @Test
  void getTopLevelRespondsWithEmptyPayload() {

    String url = getTopLevelUrl("random");

    var response = getCommodityCode(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);

    List<CommodityCodeDto> commodityCodes =
        getResponseAsList(response.getResponseBodyContent(), CommodityCodeDto.class);
    assertThat(commodityCodes).isEmpty();
  }

  @Test
  void respondsWithNotFoundWithUnknownResource() {

    String url = getTopLevelUrl(CERT_TYPE) + "x";

    var response = getCommodityCode(url);
    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  public void getByParentCodeRespondsWithCorrectPayload() {
    List<CommodityCodeDto> expectedCommodityCodes = getCommodityCodeMapping().get(COMMODITY_CODE);

    String url = getByParentCodeUrl(CERT_TYPE, COMMODITY_CODE);

    var response = getCommodityCode(url);

    List<CommodityCodeDto> commodityCodes =
        getResponseAsList(response.getResponseBodyContent(), CommodityCodeDto.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(commodityCodes).containsExactlyElementsOf(expectedCommodityCodes);
  }

  @Test
  public void getByNonExistentParentCodeRespondsWithEmptyList() {
    String url = getByParentCodeUrl(CERT_TYPE, "badCode");

    var response = getCommodityCode(url);

    List<CommodityCodeDto> commodityCodes =
        getResponseAsList(response.getResponseBodyContent(), CommodityCodeDto.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(commodityCodes).isEmpty();
  }

  @Test
  void getByCommodityCodeRespondsWithCorrectPayload() {
    final String commodityCode = "0102";

    String url = getByCommodityCodeUrl(CERT_TYPE, commodityCode);

    var response = getCommodityCode(url);

    List<CommodityCodeDto> commodityCodes =
        getResponseAsList(response.getResponseBodyContent(), CommodityCodeDto.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(commodityCodes)
        .containsExactly(
            getCommodityCodeDTO(
                commodityCode, "02", "Live bovine animals", "CVED-A", "01", true, false));
  }

  @Test
  public void getByNonExistentCommodityCodeRespondsWithEmptyList() {
    String url = getByCommodityCodeUrl(CERT_TYPE, "badCode");
    var response = getCommodityCode(url);

    List<CommodityCodeDto> commodityCodes =
        getResponseAsList(response.getResponseBodyContent(), CommodityCodeDto.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(commodityCodes).isEmpty();
  }

  @Test
  public void getAllParentsRespondsWithCorrectPayload() {
    List<CommodityCodeDto> expectedCommodityCodes = getAllParentsListMapping().get("01");

    String url = getAllParentsUrl(CERT_TYPE, "01051200");

    var response = getCommodityCode(url);

    List<CommodityCodeDto> commodityCodes =
        getResponseAsList(response.getResponseBodyContent(), CommodityCodeDto.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(commodityCodes).containsExactlyElementsOf(expectedCommodityCodes);
  }

  @Test
  public void getSupplementalDataReturnsCorrectResponseWhenThereIsNoClassOrVarietyInformation() {
    // Given
    final String AECCA_EPPO_CODE = "AECCA";
    final String AECCA_COMMODITY_CODE = "94061000";

    SupplementaryDataDto expected =
        SupplementaryDataDto.builder()
            .commodityCode(AECCA_COMMODITY_CODE)
            .eppoCode(AECCA_EPPO_CODE)
            .regulatoryAuthority(List.of(RegulatoryAuthority.PHSI))
            .marketingStandard(null)
            .varieties(Collections.emptyList())
            .classes(Collections.emptyList())
            .validityPeriod(null)
            .intendedUseRegulatoryAuthorities(
                List.of(
                    IntendedUseRegulatoryAuthority.builder()
                        .intendedUse("")
                        .regulatoryAuthority(RegulatoryAuthority.PHSI)
                        .build()))
            .build();

    // When
    String url =
        getSupplementalUrl(CHEDPP_CERT_TYPE, AECCA_COMMODITY_CODE, List.of(AECCA_EPPO_CODE));
    var response = getCommodityCode(url);

    // Then
    List<SupplementaryDataDto> supplementaryDataDtos =
        getResponseAsList(response.getResponseBodyContent(), SupplementaryDataDto.class);
    SupplementaryDataDto supplementaryData = supplementaryDataDtos.get(0);

    assertThat(supplementaryData).isEqualTo(expected);
    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  public void
      getSupplementalDataBySpeciesName_ReturnsCorrectSupplementalData_WhenCommodityCodeAndSpeciesNameExist() {
    SupplementaryDataDto expectedSupplementaryDataDto =
        SupplementaryDataDto.builder()
            .speciesId("1319830")
            .commodityCode("0808108090")
            .eppoCode("MABAN")
            .regulatoryAuthority(List.of(RegulatoryAuthority.PHSI))
            .marketingStandard(null)
            .varieties(Collections.emptyList())
            .classes(List.of("Class I", "Class II", "Extra Class"))
            .validityPeriod(null)
            .intendedUseRegulatoryAuthorities(
                List.of(
                    IntendedUseRegulatoryAuthority.builder()
                        .intendedUse("")
                        .regulatoryAuthority(RegulatoryAuthority.PHSI)
                        .build()))
            .build();

    String url = getSupplementalUrlForSpeciesName("0808108090", "Malus angustifolia");

    var response = getCommodityCode(url);

    SupplementaryDataDto supplementaryData =
        getResponseAsObject(response.getResponseBodyContent(), SupplementaryDataDto.class);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    assertThat(supplementaryData).isEqualTo(expectedSupplementaryDataDto);
  }

  @Test
  void getSupplementalDataBySpeciesName_ReturnsHttp404_WhenInvalidCommodity() {
    String url = getSupplementalUrlForSpeciesName("99999999", "Malus angustifolia");

    var response = getCommodityCode(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void getSupplementalDataBySpeciesName_ReturnsHttp404_WhenInvalidSpeciesName() {
    String url = getSupplementalUrlForSpeciesName("0808108090", "Invalid");
    var response = getCommodityCode(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void getSupplementalDataReturnsCorrectResponseWhenThereIsClassOrVarietyInformation() {
    // Given
    final String PRNPS_EPPO_CODE = "PRNPS";
    final String PRNPS_COMMODITY_CODE = "08093090";
    final String PRNPS_VALIDITY_PERIOD = "7";

    SupplementaryDataDto expected =
        SupplementaryDataDto.builder()
            .commodityCode(PRNPS_COMMODITY_CODE)
            .eppoCode(PRNPS_EPPO_CODE)
            .regulatoryAuthority(List.of(RegulatoryAuthority.JOINT))
            .marketingStandard(MarketingStandard.SMS)
            .varieties(List.of("None"))
            .classes(List.of("Class I", "Class II", "Extra Class"))
            .validityPeriod(PRNPS_VALIDITY_PERIOD)
            .intendedUseRegulatoryAuthorities(
                List.of(
                    IntendedUseRegulatoryAuthority.builder()
                        .intendedUse("")
                        .regulatoryAuthority(RegulatoryAuthority.JOINT)
                        .build()))
            .build();

    // When
    String url =
        getSupplementalUrl(CHEDPP_CERT_TYPE, PRNPS_COMMODITY_CODE, List.of(PRNPS_EPPO_CODE));
    var response = getCommodityCode(url);

    // Then
    List<SupplementaryDataDto> supplementaryDataDtos =
        getResponseAsList(response.getResponseBodyContent(), SupplementaryDataDto.class);
    SupplementaryDataDto supplementaryData = supplementaryDataDtos.get(0);

    assertThat(supplementaryData).isEqualTo(expected);
    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  void postSupplementalDataReturnsEppoAndMarketingStandardWhenGivenEppoAndVariety() {
    // Given
    final String EPPO_CODE = "MABSD";
    final String COMMODITY_CODE = "0808108090";
    final String VALIDITY_PERIOD = "7";
    final String VARIETY = "Spartan";

    List<ObjectNode> postParams = getEppoAndVarietyJsonBody(Map.of(EPPO_CODE, VARIETY));

    CommodityVarietyDto expected =
        CommodityVarietyDto.builder()
            .commodityCode(COMMODITY_CODE)
            .eppoCode(EPPO_CODE)
            .regulatoryAuthority(RegulatoryAuthority.JOINT)
            .marketingStandard(MarketingStandard.SMS)
            .variety(VARIETY)
            .validityPeriod(VALIDITY_PERIOD)
            .intendedUse(null)
            .build();

    // When
    String url = getSupplementalPostUrl(CHEDPP_CERT_TYPE, COMMODITY_CODE, List.of(EPPO_CODE));
    var response = postCommodityCode(postParams, url);

    // Then
    List<CommodityVarietyDto> commodityVarietyDtos =
        getResponseAsList(response.getResponseBodyContent(), CommodityVarietyDto.class);
    CommodityVarietyDto commodityVarietyDto = commodityVarietyDtos.get(0);

    assertThat(commodityVarietyDto).isEqualTo(expected);
    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  void getSupplementalDataV2_ReturnsCorrectResponse_WhenThereIsNoClassOrVarietyInformation() {
    // Given
    final String AECCA_EPPO_CODE = "AECCA";
    final String AECCA_COMMODITY_CODE = "94061000";

    // When
    String url =
        getSupplementalUrlV2(CHEDPP_CERT_TYPE, AECCA_COMMODITY_CODE, List.of(AECCA_EPPO_CODE));
    var response = getCommodityCode(url);

    // Then
    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    List<SupplementaryDataDtoV2> result =
        getResponseAsList(response.getResponseBodyContent(), SupplementaryDataDtoV2.class);
    assertThat(result)
        .containsExactly(
            SupplementaryDataDtoV2.builder()
                .commodityCode(AECCA_COMMODITY_CODE)
                .eppoCode(AECCA_EPPO_CODE)
                .regulatoryAuthority(RegulatoryAuthority.PHSI)
                .marketingStandard(null)
                .varieties(Collections.emptyList())
                .classes(Collections.emptyList())
                .validityPeriod(null)
                .build());
  }

  @Test
  void getSupplementalDataV2UsingMappings_ReturnsCorrectResponse() {
    // Given
    final String AECCA_EPPO_CODE = "AECCA";
    final String AECCA_COMMODITY_CODE = "94061000";

    List<CommodityEppoCodesMappingDto> mappings =
        List.of(
            CommodityEppoCodesMappingDto.builder()
                .commodityCode("94061000")
                .eppoCodes(List.of("AECCA"))
                .build());

    // When
    String url = getSupplementalUrlV2UsingMappings(CHEDPP_CERT_TYPE);
    var response = postCommodityCode(mappings, url);

    // Then
    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    List<SupplementaryDataDtoV2> result =
        getResponseAsList(response.getResponseBodyContent(), SupplementaryDataDtoV2.class);
    assertThat(result)
        .containsExactly(
            SupplementaryDataDtoV2.builder()
                .commodityCode(AECCA_COMMODITY_CODE)
                .eppoCode(AECCA_EPPO_CODE)
                .regulatoryAuthority(RegulatoryAuthority.PHSI)
                .marketingStandard(null)
                .validityPeriod(null)
                .build());
  }

  @Test
  void
      getSupplementalDataBySpeciesNameV2_ReturnsCorrectSupplementalData_WhenCommodityCodeAndSpeciesNameExist() {

    String url = getSupplementalUrlForSpeciesNameV2("0808108090", "Malus angustifolia");
    var response = getCommodityCode(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    SupplementaryDataDtoV2 result =
        getResponseAsObject(response.getResponseBodyContent(), SupplementaryDataDtoV2.class);
    assertThat(result)
        .isEqualTo(
            SupplementaryDataDtoV2.builder()
                .speciesId("1319830")
                .commodityCode("0808108090")
                .eppoCode("MABAN")
                .regulatoryAuthority(RegulatoryAuthority.PHSI)
                .marketingStandard(null)
                .varieties(Collections.emptyList())
                .classes(List.of("Class I", "Class II", "Extra Class"))
                .validityPeriod(null)
                .build());
  }

  @Test
  void getSupplementalDataBySpeciesNameV2_ReturnsHttp404_WhenInvalidCommodity() {
    String url = getSupplementalUrlForSpeciesNameV2("99999999", "Malus angustifolia");
    var response = getCommodityCode(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void getSupplementalDataBySpeciesNameV2_ReturnsHttp404_WhenInvalidSpeciesName() {
    String url = getSupplementalUrlForSpeciesNameV2("0808108090", "Invalid");
    var response = getCommodityCode(url);

    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void getSupplementalDataV2_ReturnsCorrectResponse_WhenThereIsClassOrVarietyInformation() {
    // Given
    final String PRNPS_EPPO_CODE = "PRNPS";
    final String PRNPS_COMMODITY_CODE = "08093090";
    final String PRNPS_VALIDITY_PERIOD = "7";

    // When
    String url =
        getSupplementalUrlV2(CHEDPP_CERT_TYPE, PRNPS_COMMODITY_CODE, List.of(PRNPS_EPPO_CODE));
    var response = getCommodityCode(url);

    // Then
    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    List<SupplementaryDataDtoV2> result =
        getResponseAsList(response.getResponseBodyContent(), SupplementaryDataDtoV2.class);
    assertThat(result)
        .containsExactly(
            SupplementaryDataDtoV2.builder()
                .commodityCode(PRNPS_COMMODITY_CODE)
                .eppoCode(PRNPS_EPPO_CODE)
                .regulatoryAuthority(RegulatoryAuthority.JOINT)
                .marketingStandard(MarketingStandard.SMS)
                .varieties(List.of("None"))
                .classes(List.of("Class I", "Class II", "Extra Class"))
                .validityPeriod(PRNPS_VALIDITY_PERIOD)
                .build());
  }

  @Test
  void postSupplementalDataV2_ReturnsEppoAndMarketingStandard_WhenGivenEppoAndVariety() {
    // Given
    final String EPPO_CODE = "MABSD";
    final String COMMODITY_CODE = "0808108090";
    final String VALIDITY_PERIOD = "7";
    final String VARIETY = "Spartan";

    List<ObjectNode> postParams = getEppoAndVarietyJsonBody(Map.of(EPPO_CODE, VARIETY));

    // When
    String url = getSupplementalPostUrlV2(CHEDPP_CERT_TYPE, COMMODITY_CODE, List.of(EPPO_CODE));
    var response = postCommodityCode(postParams, url);

    // Then
    assertThat(response.getStatus().value()).isEqualTo(HttpStatus.SC_OK);
    List<CommodityVarietyDtoV2> result =
        getResponseAsList(response.getResponseBodyContent(), CommodityVarietyDtoV2.class);
    assertThat(result)
        .containsExactly(
            CommodityVarietyDtoV2.builder()
                .commodityCode(COMMODITY_CODE)
                .eppoCode(EPPO_CODE)
                .regulatoryAuthority(RegulatoryAuthority.JOINT)
                .marketingStandard(MarketingStandard.SMS)
                .variety(VARIETY)
                .validityPeriod(VALIDITY_PERIOD)
                .build());
  }

  @Test
  void getCommodityGroupsReturnsCorrectResponse() {
    // Given
    final String SPECIES_COMMODITY_CODE = "09109931";
    CommodityGroupsDto expectedCommodityGroups =
        CommodityGroupsDto.builder()
            .commodityCode(SPECIES_COMMODITY_CODE)
            .commodityGroups(List.of("Vegetables"))
            .build();

    // When
    String url = getCommodityGroupsUrl(List.of(SPECIES_COMMODITY_CODE));
    var response = getCommodityCode(url);

    // Then
    List<CommodityGroupsDto> commodityGroupsDto =
        getResponseAsList(response.getResponseBodyContent(), CommodityGroupsDto.class);
    assertThat(commodityGroupsDto).containsExactly(expectedCommodityGroups);
  }

  @Test
  void getCommodityConfigurationsReturnsCorrectResponseForTestAndTrial() {
    final String commodityCode = "0713500010";

    String url = getCommodityCategoriesUrl(List.of(commodityCode));
    var response = getCommodityCode(url);

    List<CommodityConfigurationDto> result =
        getResponseAsList(response.getResponseBodyContent(), CommodityConfigurationDto.class);

    assertThat(result).containsExactly(buildCommodityConfigDto(commodityCode, true, false));
  }

  @Test
  void getCommodityConfigurationsReturnsCorrectResponseForFinishedOrPropagated() {
    final String commodityCode = "06011010";

    String url = getCommodityCategoriesUrl(List.of(commodityCode));
    var response = getCommodityCode(url);

    List<CommodityConfigurationDto> result =
        getResponseAsList(response.getResponseBodyContent(), CommodityConfigurationDto.class);

    assertThat(result).containsExactly(buildCommodityConfigDto(commodityCode, false, true));
  }

  @Test
  void getCommodityConfigurationsReturnsCorrectResponseForMultipleCodes() {
    final String commodityCode1 = "0713500010";
    final String commodityCode2 = "06011010";
    final String commodityCode3 = "1111";

    String url = getCommodityCategoriesUrl(List.of(commodityCode1, commodityCode2, commodityCode3));
    var response = getCommodityCode(url);

    List<CommodityConfigurationDto> result =
        getResponseAsList(response.getResponseBodyContent(), CommodityConfigurationDto.class);

    assertThat(result)
        .containsExactly(
            buildCommodityConfigDto(commodityCode1, true, false),
            buildCommodityConfigDto(commodityCode2, false, true),
            buildCommodityConfigDto(commodityCode3, false, false));
  }

  @Test
  void getCommodityGroupReturnsCorrectResponse() {
    // Given
    final String SPECIES_COMMODITY_CODE = "09109931";
    CommodityGroupDto expectedCommodityGroup =
        CommodityGroupDto.builder()
            .commodityCode(SPECIES_COMMODITY_CODE)
            .commodityGroup("Vegetables")
            .build();

    // When
    String url = getCommodityGroupUrl(List.of(SPECIES_COMMODITY_CODE));
    var response = getCommodityCode(url);

    // Then
    List<CommodityGroupDto> commodityGroupDto =
        getResponseAsList(response.getResponseBodyContent(), CommodityGroupDto.class);
    assertThat(commodityGroupDto).containsExactly(expectedCommodityGroup);
  }

  @Test
  void getCommodityAttributesReturnsCorrectResponseForBulb() {
    final String commodityCode = "06011010";

    String url = getCommodityAttributesUrl(List.of(commodityCode));
    var response = getCommodityCode(url);

    List<CommodityAttributeDto> result =
        getResponseAsList(response.getResponseBodyContent(), CommodityAttributeDto.class);

    assertThat(result).containsExactly(buildCommodityAttributeDto(commodityCode, "bulb"));
  }

  @Test
  void getCommodityAttributesReturnsCorrectResponseForPlant() {
    final String commodityCode = "06024000";

    String url = getCommodityAttributesUrl(List.of(commodityCode));
    var response = getCommodityCode(url);

    List<CommodityAttributeDto> result =
        getResponseAsList(response.getResponseBodyContent(), CommodityAttributeDto.class);

    assertThat(result).containsExactly(buildCommodityAttributeDto(commodityCode, "plant"));
  }

  @Test
  void getCommodityAttributesReturnsCorrectResponseForMultipleCodes() {
    final String commodityCode1 = "06011020";
    final String commodityCode2 = "0601209010";
    final String commodityCode3 = "1111";

    String url = getCommodityAttributesUrl(List.of(commodityCode1, commodityCode2, commodityCode3));
    var response = getCommodityCode(url);

    List<CommodityAttributeDto> result =
        getResponseAsList(response.getResponseBodyContent(), CommodityAttributeDto.class);

    assertThat(result)
        .containsExactly(
            buildCommodityAttributeDto(commodityCode1, "bulb"),
            buildCommodityAttributeDto(commodityCode2, "plant"),
            buildCommodityAttributeDto(commodityCode3, null));
  }

  private List<ObjectNode> getEppoAndVarietyJsonBody(Map<String, String> eppoVarietyMap) {
    List<ObjectNode> searchParams = new ArrayList<>();

    eppoVarietyMap.forEach(
        (key, value) -> {
          ObjectNode childNode = mapper.createObjectNode();
          childNode.put("eppoCode", key);
          childNode.put("variety", value);
          searchParams.add(childNode);
        });

    return searchParams;
  }

  private CommodityAttributeDto buildCommodityAttributeDto(
      String commodityCode, String propagation) {
    return CommodityAttributeDto.builder()
        .commodityCode(commodityCode)
        .propagation(propagation)
        .build();
  }

  private CommodityConfigurationDto buildCommodityConfigDto(
      String commodityCode, boolean requiresTestAndTrial, boolean requiresFinishedOrPropagated) {
    return CommodityConfigurationDto.builder()
        .commodityCode(commodityCode)
        .requiresTestAndTrial(requiresTestAndTrial)
        .requiresFinishedOrPropagated(requiresFinishedOrPropagated)
        .build();
  }

  private List<CommodityCodeDto> buildCommodityListForTopLevel() {
    return Arrays.asList(
        getCommodityCodeDTO("01", "01", "LIVE ANIMALS", "CVED-A", null, false, true),
        getCommodityCodeDTO(
            "03",
            "03",
            "FISH AND CRUSTACEANS, MOLLUSCS AND OTHER AQUATIC INVERTEBRATES",
            "CVED-A",
            null,
            false,
            true),
        getCommodityCodeDTO(
            "04",
            "04",
            "DAIRY PRODUCE; BIRDS' EGGS; NATURAL HONEY; EDIBLE PRODUCTS OF ANIMAL ORIGIN, NOT ELSEWHERE SPECIFIED OR INCLUDED",
            "CVED-A",
            null,
            false,
            true),
        getCommodityCodeDTO(
            "05",
            "05",
            "PRODUCTS OF ANIMAL ORIGIN, NOT ELSEWHERE SPECIFIED OR INCLUDED",
            "CVED-A",
            null,
            false,
            true),
        getCommodityCodeDTO(
            "95",
            "95",
            "TOYS, GAMES AND SPORTS REQUISITES; PARTS AND ACCESSORIES THEREOF",
            "CVED-A",
            null,
            false,
            true));
  }

  private List<CommodityCodeDto> buildCommodityListForParent() {
    return Arrays.asList(
        getCommodityCodeDTO(
            "0101", "01", "Live horses, asses, mules and hinnies", "CVED-A", "01", true, false),
        getCommodityCodeDTO("0102", "02", "Live bovine animals", "CVED-A", "01", true, false),
        getCommodityCodeDTO("0103", "03", "Live swine", "CVED-A", "01", true, false),
        getCommodityCodeDTO("0104", "04", "Live sheep and goats", "CVED-A", "01", false, true),
        getCommodityCodeDTO(
            "0105",
            "05",
            "Live poultry, that is to say, fowls of the species Gallus domesticus, ducks, geese, turkeys and guinea fowls",
            "CVED-A",
            "01",
            false,
            true),
        getCommodityCodeDTO("0106", "06", "Other live animals", "CVED-A", "01", false, true));
  }

  private List<CommodityCodeDto> buildCommodityListForAllParents() {
    return Arrays.asList(
        getCommodityCodeDTO("01", "01", "LIVE ANIMALS", "CVED-A", null, false, true),
        getCommodityCodeDTO(
            "0105",
            "05",
            "Live poultry, that is to say, fowls of the species Gallus domesticus, ducks, geese, turkeys and guinea fowls",
            "CVED-A",
            "01",
            false,
            true),
        getCommodityCodeDTO(
            "01051", "", "Weighing not more than 185 g:", "CVED-A", "0105", false, true),
        getCommodityCodeDTO("01051200", "00", "Turkeys", "CVED-A", "01051", true, false));
  }

  public CommodityCodeDto getCommodityCodeDTO(
      String code,
      String displayCode,
      String description,
      String certType,
      String parentCode,
      boolean isCommodity,
      boolean isParent) {
    return CommodityCodeDto.builder()
        .code(code)
        .displayCode(displayCode)
        .description(description)
        .displayCodeFull(parseCommodityCode(code))
        .certificateType(certType)
        .parentCode(parentCode)
        .isCommodity(isCommodity)
        .isParent(isParent)
        .build();
  }

  private List<String> parseCommodityCode(String commodityCode) {
    List<String> commodityCodeParts = new ArrayList<>();

    // validate data, must be even number
    if (commodityCode.length() % 2 == 1) {
      return null;
    }

    // otherwise all is ok so far
    for (int looper = 0; looper < commodityCode.length(); looper += 2) {
      String partialCode = commodityCode.substring(looper, looper + 2);
      commodityCodeParts.add(partialCode);
    }

    return commodityCodeParts;
  }

  private Map<String, List<CommodityCodeDto>> getTopLevelListMapping() {
    return Map.of("01", buildCommodityListForTopLevel());
  }

  private Map<String, List<CommodityCodeDto>> getCommodityCodeMapping() {
    return Map.of("01", buildCommodityListForParent());
  }

  private Map<String, List<CommodityCodeDto>> getAllParentsListMapping() {
    return Map.of("01", buildCommodityListForAllParents());
  }

  private FluxExchangeResult<String> getCommodityCode(String url) {
    return whenApiCallByADInspector(API.get(url)).body();
  }

  private <T> FluxExchangeResult<String> postCommodityCode(T body, String url) {
    return whenApiCallByADInspector(API.post(body, url)).body();
  }
}
