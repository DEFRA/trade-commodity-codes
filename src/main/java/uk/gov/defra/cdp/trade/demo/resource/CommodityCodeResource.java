package uk.gov.defra.cdp.trade.demo.resource;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import io.micrometer.core.annotation.Timed;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.cdp.trade.demo.domain.SearchVariety;
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
import uk.gov.defra.cdp.trade.demo.enumerations.ChedType;
import uk.gov.defra.cdp.trade.demo.service.CommodityAttributeService;
import uk.gov.defra.cdp.trade.demo.service.CommodityCodeService;
import uk.gov.defra.cdp.trade.demo.service.CommodityConfigurationService;
import uk.gov.defra.cdp.trade.demo.service.SupplementaryDataService;

@Slf4j
@RestController
@RequestMapping("/commodity-codes")
public class CommodityCodeResource {

  private static final String COMMODITY_CODE_READ_PERMISSION = "'commoditycode.read'";

  private final CommodityCodeService commodityCodeService;
  private final SupplementaryDataService supplementaryDataService;
  private final CommodityConfigurationService commodityConfigService;
  private final CommodityAttributeService commodityAttributeService;

  @Autowired
  public CommodityCodeResource(CommodityCodeService commodityCodeService,
      SupplementaryDataService supplementaryDataService,
      CommodityConfigurationService commodityConfigService,
      CommodityAttributeService commodityAttributeService) {
    this.commodityCodeService = commodityCodeService;
    this.supplementaryDataService = supplementaryDataService;
    this.commodityConfigService = commodityConfigService;
    this.commodityAttributeService = commodityAttributeService;
  }

  @GetMapping(value = "/{certType}/top-level")
  @Timed("controller.getTopLevelDuration.time")
  public List<CommodityCodeDto> getTopLevel(@PathVariable("certType") String certType,
      @RequestParam(name = "species", required = false, defaultValue = "") String species) {
    String certToLog = replaceNewLines(certType);
    String speciesToLog = replaceNewLines(species);
    log.info("Top level commodity codes for certificate type: {} and species: {}",
        certToLog, speciesToLog);
    return !isEmpty(species) ? commodityCodeService.getTopLevel(certType, species)
        : commodityCodeService.getTopLevel(certType);
  }

  @GetMapping(value = "/{certType}/parent-code/{parentCode}")
  @Timed("controller.getByParentDuration.time")
  public List<CommodityCodeDto> getByParentCode(
      @PathVariable("certType") String certType,
      @PathVariable("parentCode") String parentCode,
      @RequestParam(name = "species", required = false) String species) {
    String parentCodeToLog = replaceNewLines(parentCode);
    List<CommodityCodeDto> commodityCodeList;
    if (species != null) {
      String speciesToLog = replaceNewLines(species);
      log.info("Commodity codes by parent code: {} and species: {}", parentCodeToLog,
          speciesToLog);
      commodityCodeList = commodityCodeService.getByParentCodeAndSpecies(certType, parentCode,
          species);
    } else {
      log.info("Commodity codes by parent code: {}", parentCodeToLog);
      commodityCodeList = commodityCodeService.getByParentCode(certType, parentCode);
    }
    return commodityCodeList;
  }

  @GetMapping(value = "/{certType}/commodity-code/{commodityCode}")
  @Timed("controller.getByCodeDuration.time")
  public List<CommodityCodeDto> getByCommodityCode(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode) {
    String commodityCodeToLog = replaceNewLines(commodityCode);
    log.info("Commodity codes by commodity code: {}", commodityCodeToLog);
    return commodityCodeService
        .getByCommodityCode(certType, commodityCode);
  }

  @GetMapping(value = "/{certType}/commodity-code/{commodityCode}/supplemental-data",
      params = "eppoCodes")
  @Timed("controller.getSupplementalDataForEppoCodes.time")
  public ResponseEntity<List<SupplementaryDataDto>> getSupplementalDataForEppoCodes(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode,
      @RequestParam("eppoCodes") List<String> eppoCodes) {

    List<SupplementaryDataDto> supplementaryDataList =
        supplementaryDataService.getSupplementaryData(commodityCode, eppoCodes);

    return new ResponseEntity<>(supplementaryDataList, HttpStatus.OK);
  }

  @PostMapping(value = "/{certType}/commodity-code/{commodityCode}/supplemental-data")
  @Timed("controller.postSupplementalDataForEppoCodes.time")
  public ResponseEntity<List<SupplementaryDataDto>> postSupplementalDataForEppoCodes(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode,
      @RequestBody List<String> eppoCodes) {

    return getSupplementalDataForEppoCodes(certType, commodityCode, eppoCodes);
  }

  @PostMapping(value = "/{certType}/supplemental-data")
  @Timed("controller.postSupplementalDataForEppoCodes.time")
  public ResponseEntity<List<SupplementaryDataDtoV2>> postSupplementalDataForEppoCodes(
      @PathVariable("certType") String certType,
      @RequestBody List<CommodityEppoCodesMappingDto> commodityToEppoCodeMappings) {

    List<String> commodityCodes = commodityToEppoCodeMappings.stream()
        .map(CommodityEppoCodesMappingDto::getCommodityCode)
        .toList();

    List<SupplementaryDataDtoV2> supplementaryDataList = supplementaryDataService
        .getSupplementaryDataV2(commodityCodes, commodityToEppoCodeMappings);

    return new ResponseEntity<>(supplementaryDataList, HttpStatus.OK);
  }

  @GetMapping(path = "/v2/{certType}/commodity-code/{commodityCode}/supplemental-data",
      params = "eppoCodes")
  @Timed("controller.getSupplementalDataForEppoCodesV2.time")
  public ResponseEntity<List<SupplementaryDataDtoV2>> getSupplementalDataForEppoCodesV2(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode,
      @RequestParam("eppoCodes") List<String> eppoCodes) {

    return ResponseEntity.ok(supplementaryDataService.getSupplementaryDataV2(commodityCode,
        eppoCodes));
  }

  @PostMapping(value = "/v2/{certType}/commodity-code/{commodityCode}/supplemental-data")
  @Timed("controller.postSupplementalDataForEppoCodesV2.time")
  public ResponseEntity<List<SupplementaryDataDtoV2>> postSupplementalDataForEppoCodesV2(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode,
      @RequestBody List<String> eppoCodes) {

    return ResponseEntity.ok(supplementaryDataService.getSupplementaryDataV2(commodityCode,
        eppoCodes));
  }

  @GetMapping(value = "/chedpp/commodity-code/{commodityCode}/supplemental-data",
      params = "speciesName")
  @Timed("controller.getSupplementalDataForSpeciesName.time")
  public ResponseEntity<SupplementaryDataDto> getSupplementalDataForSpeciesName(
      @PathVariable("commodityCode") String commodityCode,
      @RequestParam("speciesName") String speciesName) {

    SupplementaryDataDto supplementaryDataDto =
        supplementaryDataService.getSupplementaryData(commodityCode, speciesName);

    return new ResponseEntity<>(supplementaryDataDto, HttpStatus.OK);
  }

  @GetMapping(value = "/v2/chedpp/commodity-code/{commodityCode}/supplemental-data",
      params = "speciesName")
  @Timed("controller.getSupplementalDataForSpeciesNameV2.time")
  public ResponseEntity<SupplementaryDataDtoV2> getSupplementalDataForSpeciesNameV2(
      @PathVariable("commodityCode") String commodityCode,
      @RequestParam("speciesName") String speciesName) {

    return ResponseEntity.ok(supplementaryDataService.getSupplementaryDataV2(commodityCode,
        speciesName));
  }

  @PostMapping(value = "/{certType}/commodity-code/{commodityCode}/supplemental")
  @Timed("controller.postVarietyMarketingStandard.time")
  public ResponseEntity<List<CommodityVarietyDto>> getVarietyMarketingStandard(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode,
      @RequestBody List<SearchVariety> searchVarieties) {

    List<SearchVariety> validSearchVarieties = searchVarieties.stream()
        .filter(sv -> sv.getEppoCode() != null && !sv.getEppoCode().isEmpty()
            && sv.getVariety() != null && !sv.getVariety().isEmpty())
        .toList();

    List<CommodityVarietyDto> varieties = Collections.emptyList();

    if (!validSearchVarieties.isEmpty()) {
      varieties = supplementaryDataService
          .getVarietyMarketingStandard(commodityCode, validSearchVarieties);
    }

    return new ResponseEntity<>(varieties, HttpStatus.OK);
  }

  @PostMapping(value = "/v2/{certType}/commodity-code/{commodityCode}/supplemental")
  @Timed("controller.postVarietyMarketingStandardV2.time")
  public ResponseEntity<List<CommodityVarietyDtoV2>> getVarietyMarketingStandardV2(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode,
      @RequestBody List<SearchVariety> searchVarieties) {

    List<SearchVariety> validSearchVarieties = searchVarieties.stream()
        .filter(sv -> sv.getEppoCode() != null
            && !sv.getEppoCode().isEmpty()
            && sv.getVariety() != null
            && !sv.getVariety().isEmpty())
        .toList();

    List<CommodityVarietyDtoV2> varieties = Collections.emptyList();

    if (!validSearchVarieties.isEmpty()) {
      varieties = supplementaryDataService.getVarietyMarketingStandardV2(commodityCode,
          validSearchVarieties);
    }

    return new ResponseEntity<>(varieties, HttpStatus.OK);
  }

  @GetMapping(value = "/{certType}/all-parents/{commodityCode}")
  @Timed("controller.getAllParents.time")
  public List<CommodityCodeDto> getAllParents(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode,
      @RequestParam(value = "species", required = false) String species) {
    String certTypeToLog = replaceNewLines(certType);
    String commodityCodeToLog = replaceNewLines(commodityCode);
    log.info(
        "All parents of commodity code {} and certificate type: {}", certTypeToLog,
        commodityCodeToLog);
    return commodityCodeService
        .getAllParents(certType, commodityCode, species);
  }

  @GetMapping(value = "/groups")
  @Timed("controller.getGroups.time")
  public List<CommodityGroupsDto> getCommodityGroups(
      @RequestParam("commodityCodes") List<String> commodityCodes) {
    return commodityCodeService.getCommodityGroups(commodityCodes);
  }

  @GetMapping(value = "/chedpp/commodity-configuration")
  @Timed("controller.getCommodityConfiguration.time")
  public List<CommodityConfigurationDto> getCommodityConfiguration(
      @RequestParam("commodityCodes") List<String> commodityCodes) {
    return commodityConfigService.getCommodityConfigurations(commodityCodes, ChedType.CHEDPP);
  }

  @GetMapping(value = "/v2/group")
  @Timed("controller.getCommodityGroup.time")
  public List<CommodityGroupDto> getCommodityGroup(
      @RequestParam("commodityCodes") List<String> commodityCodes) {
    return commodityCodeService.getCommodityGroup(commodityCodes);
  }

  @GetMapping(value = "/chedpp/commodity-attributes")
  @Timed("controller.getCommodityAttributes.time")
  public List<CommodityAttributeDto> getCommodityAttributes(
          @RequestParam("commodityCodes") List<String> commodityCodes) {
    return commodityAttributeService.getCommodityAttributes(commodityCodes);
  }

  private String replaceNewLines(String value) {
    return value.replaceAll("[\\n\\r\\t]", "_");
  }
}
