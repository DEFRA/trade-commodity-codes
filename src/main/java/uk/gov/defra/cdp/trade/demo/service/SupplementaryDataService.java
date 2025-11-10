package uk.gov.defra.cdp.trade.demo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;
import uk.gov.defra.cdp.trade.demo.domain.CommodityClass;
import uk.gov.defra.cdp.trade.demo.domain.CommodityVariety;
import uk.gov.defra.cdp.trade.demo.domain.IntendedUseRegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.domain.SearchVariety;
import uk.gov.defra.cdp.trade.demo.domain.SupplementaryData;
import uk.gov.defra.cdp.trade.demo.domain.repository.ChedppSpeciesRepository;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityClassRepository;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityVarietyRepository;
import uk.gov.defra.cdp.trade.demo.domain.repository.SupplementaryDataRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityEppoCodesMappingDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityVarietyDtoV2;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDto;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDtoV2;
import uk.gov.defra.cdp.trade.demo.exceptions.NotFoundException;
import uk.gov.defra.cdp.trade.demo.transformers.CommodityVarietyDtoTransformer;
import uk.gov.defra.cdp.trade.demo.transformers.CommodityVarietyDtoV2Transformer;
import uk.gov.defra.cdp.trade.demo.transformers.SupplementaryDataDtoTransformer;
import uk.gov.defra.cdp.trade.demo.transformers.SupplementaryDataDtoV2Transformer;

@Service
public class SupplementaryDataService {

  private final SupplementaryDataRepository supplementaryDataRepository;
  private final CommodityVarietyRepository commodityVarietyRepository;
  private final CommodityClassRepository commodityClassRepository;
  private final ChedppSpeciesRepository chedppSpeciesRepository;
  @PersistenceContext
  private final EntityManager entityManager;

  @Autowired
  public SupplementaryDataService(
      SupplementaryDataRepository supplementaryDataRepository,
      CommodityVarietyRepository commodityVarietyRepository,
      CommodityClassRepository commodityClassRepository,
      ChedppSpeciesRepository chedppSpeciesRepository,
      EntityManager entityManager) {
    this.supplementaryDataRepository = supplementaryDataRepository;
    this.commodityVarietyRepository = commodityVarietyRepository;
    this.commodityClassRepository = commodityClassRepository;
    this.chedppSpeciesRepository = chedppSpeciesRepository;
    this.entityManager = entityManager;
  }

  private static List<String> getClasses(List<CommodityClass> classes) {
    return classes.stream()
        .map(CommodityClass::getClazz)
        .sorted()
        .toList();
  }

  private static List<String> getVarieties(String eppoCode, List<CommodityVariety> varieties) {
    return varieties.stream()
        .filter(v -> v.getEppoCode().equals(eppoCode))
        .map(CommodityVariety::getVariety)
        .sorted()
        .distinct()
        .toList();
  }

  public List<CommodityVarietyDto> getVarietyMarketingStandard(String commodityCode,
      List<SearchVariety> searchVarieties) {

    HashMap<String, String> uniqueSearches = new HashMap<>();
    searchVarieties.forEach(sv -> uniqueSearches.put(sv.getEppoCode(), sv.getVariety()));
    List<String> eppoCodes = new ArrayList<>(uniqueSearches.keySet());
    List<String> varieties = new ArrayList<>(uniqueSearches.values());
    List<CommodityVarietyDto> varietyDtos = commodityVarietyRepository
        .findByCommodityCodeAndEppoCodeInAndVarietyIn(commodityCode, eppoCodes, varieties)
        .stream()
        .filter(cv -> uniqueSearches.get(cv.getEppoCode()).equals(cv.getVariety()))
        .map(CommodityVarietyDtoTransformer::from)
        .toList();

    if (varietyDtos.size() == 1) {
      return varietyDtos;
    }

    return varietyDtos.stream()
        .filter(x -> x.getIntendedUse() == null)
        .toList();
  }

  public List<CommodityVarietyDtoV2> getVarietyMarketingStandardV2(String commodityCode,
      List<SearchVariety> searchVarieties) {

    Map<String, String> uniqueSearches = searchVarieties.stream()
        .collect(Collectors.toMap(
            SearchVariety::getEppoCode,
            SearchVariety::getVariety,
            (first, second) -> second));

    List<String> eppoCodes = new ArrayList<>(uniqueSearches.keySet());
    List<String> varieties = new ArrayList<>(uniqueSearches.values());

    return commodityVarietyRepository
        .findByCommodityCodeAndEppoCodeInAndVarietyIn(commodityCode, eppoCodes, varieties)
        .stream()
        .filter(cv -> uniqueSearches.get(cv.getEppoCode()).equals(cv.getVariety()))
        .map(CommodityVarietyDtoV2Transformer::from)
        .toList();
  }

  public SupplementaryDataDto getSupplementaryData(String commodityCode, String speciesName) {
    ChedppSpecies chedppSpecies = chedppSpeciesRepository.findFirstByCommodityCodeAndSpeciesName(
            commodityCode, speciesName)
        .orElseThrow(NotFoundException::new);

    SupplementaryDataDto supplementaryDataDto = getSupplementaryData(commodityCode,
        Collections.singletonList(chedppSpecies.getEppoCode())).get(0);

    supplementaryDataDto.setSpeciesId(String.valueOf(chedppSpecies.getSpeciesId()));
    return supplementaryDataDto;
  }

  public List<SupplementaryDataDto> getSupplementaryData(
      String commodityCode,
      List<String> eppoCodes) {

    List<SupplementaryData> daos = supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(
        commodityCode, eppoCodes);
    List<SupplementaryDataDto> supplementaryDataDtoList = daos.stream()
        .map(SupplementaryDataDtoTransformer::from)
        .toList();

    HashMap<String, SupplementaryDataDto> eppoCodeMap = new HashMap<>();

    supplementaryDataDtoList.forEach(
        dto -> {
          IntendedUseRegulatoryAuthority intendedUseRegulatoryAuthority =
              dto.getIntendedUseRegulatoryAuthorities().get(0);

          if (eppoCodeMap.containsKey(dto.getEppoCode())) {
            eppoCodeMap.merge(
                dto.getEppoCode(),
                dto,
                (oldValue, newValue) -> {
                  oldValue
                      .getIntendedUseRegulatoryAuthorities()
                      .add(intendedUseRegulatoryAuthority);
                  newValue.setIntendedUseRegulatoryAuthorities(
                      oldValue.getIntendedUseRegulatoryAuthorities());

                  newValue.setRegulatoryAuthority(
                      Stream.concat(
                              oldValue.getRegulatoryAuthority().stream(),
                              newValue.getRegulatoryAuthority().stream())
                          .distinct()
                          .toList());
                  return newValue;
                });
          } else {
            List<IntendedUseRegulatoryAuthority> intendedUseRegulatoryAuthorities =
                new ArrayList<>();
            intendedUseRegulatoryAuthorities.add(intendedUseRegulatoryAuthority);
            dto.setIntendedUseRegulatoryAuthorities(
                intendedUseRegulatoryAuthorities);
            eppoCodeMap.put(dto.getEppoCode(), dto);
          }
        });

    List<SupplementaryDataDto> finalSupplementaryDataDtoList =
        new ArrayList<>(eppoCodeMap.values());

    if (eppoCodeMap.size() > 0) {
      List<String> commodityClasses = getClasses(
          commodityClassRepository.findAllByCommodityCode(commodityCode));
      List<CommodityVariety> commodityVarieties = commodityVarietyRepository
          .findAllByCommodityCodeAndEppoCodeIn(commodityCode, eppoCodes);

      finalSupplementaryDataDtoList.forEach(dto -> {
        dto.setClasses(commodityClasses);
        dto.setVarieties(getVarieties(dto.getEppoCode(), commodityVarieties));
      });
    }

    return finalSupplementaryDataDtoList;
  }

  public SupplementaryDataDtoV2 getSupplementaryDataV2(String commodityCode, String speciesName) {
    ChedppSpecies chedppSpecies = chedppSpeciesRepository.findFirstByCommodityCodeAndSpeciesName(
            commodityCode, speciesName)
        .orElseThrow(NotFoundException::new);

    SupplementaryDataDtoV2 supplementaryDataDto = getSupplementaryDataV2(commodityCode,
        List.of(chedppSpecies.getEppoCode())).get(0);
    supplementaryDataDto.setSpeciesId(String.valueOf(chedppSpecies.getSpeciesId()));

    return supplementaryDataDto;
  }

  public List<SupplementaryDataDtoV2> getSupplementaryDataV2(
      String commodityCode,
      List<String> eppoCodes) {

    List<SupplementaryData> supplementaryDataList =
        supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(commodityCode, eppoCodes);

    if (supplementaryDataList.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> commodityClasses =
        getClasses(commodityClassRepository.findAllByCommodityCode(commodityCode));
    List<CommodityVariety> commodityVarieties =
        commodityVarietyRepository.findAllByCommodityCodeAndEppoCodeIn(commodityCode, eppoCodes);

    return supplementaryDataList.stream()
        .map(supplementaryData -> {
          SupplementaryDataDtoV2 dto = SupplementaryDataDtoV2Transformer.from(supplementaryData);
          dto.setClasses(commodityClasses);
          dto.setVarieties(getVarieties(dto.getEppoCode(), commodityVarieties));
          return dto;
        })
        .toList();
  }

  public List<SupplementaryDataDtoV2> getSupplementaryDataV2(
      List<String> commodityCodes,
      List<CommodityEppoCodesMappingDto> commodityToEppoCodeMappings) {

    String cteMappingStructure = getCommodityToEppoCodeSqlStructure(commodityToEppoCodeMappings);

    List<SupplementaryData> supplementaryDataList =
        supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeInv2(entityManager,
            commodityCodes, cteMappingStructure, commodityToEppoCodeMappings);

    return supplementaryDataList.stream()
        .map(SupplementaryDataDtoV2Transformer::from)
        .toList();
  }

  private String getCommodityToEppoCodeSqlStructure(
      List<CommodityEppoCodesMappingDto> commodityToEppoCodeMappings) {
    return IntStream.range(0, commodityToEppoCodeMappings.size())
        .mapToObj(indexCommodity -> IntStream.range(0,
                commodityToEppoCodeMappings.get(indexCommodity).getEppoCodes().size())
            .mapToObj(indexEppoCode ->
                String.format("(:%s, :%s)",
                    "c" + indexCommodity, "c" + indexCommodity + "e" + indexEppoCode))
            .collect(Collectors.joining(","))
        ).collect(Collectors.joining(","));
  }
}
