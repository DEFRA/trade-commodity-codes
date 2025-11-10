package uk.gov.defra.cdp.trade.demo.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.COMMODITY_CODE;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.COMMODITY_DESCRIPTION;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.EPPO_CODE;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.SPECIES_ID;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.SPECIES_NAME;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.VARIETY;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.commodityClass;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.commodityVariety;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.commodityVarietyDto;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.commodityVarietyDtoV2;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.searchVariety;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryData;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryDataDto;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryDataDtoV2;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryDataDtoV2NoClassesAndVarieties;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryDataDtoWithSpeciesId;
import static uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures.supplementaryDataDtoWithSpeciesIdV2;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;
import uk.gov.defra.cdp.trade.demo.domain.CommodityVariety;
import uk.gov.defra.cdp.trade.demo.domain.IntendedUseRegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.domain.SearchVariety;
import uk.gov.defra.cdp.trade.demo.domain.SupplementaryData;
import uk.gov.defra.cdp.trade.demo.enumerations.RegulatoryAuthority;
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

@ExtendWith(MockitoExtension.class)
class SupplementaryDataServiceTest {

  @InjectMocks
  private SupplementaryDataService dataService;

  @Mock
  private SupplementaryDataRepository supplementaryDataRepository;
  @Mock
  private CommodityVarietyRepository commodityVarietyRepository;
  @Mock
  private CommodityClassRepository commodityClassRepository;
  @Mock
  private ChedppSpeciesRepository chedppSpeciesRepository;
  @Mock
  private EntityManager entityManager;

  @Test
  void getSupplementaryData_ReturnsSupplementaryData_WhenDataIsPresent() {
    // Given
    List<String> eppoCodes = Collections.singletonList(EPPO_CODE);
    when(supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Collections.singletonList(supplementaryData()));
    when(commodityVarietyRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Collections.singletonList(commodityVariety()));
    when(commodityClassRepository.findAllByCommodityCode(COMMODITY_CODE))
        .thenReturn(Collections.singletonList(commodityClass()));

    // When
    List<SupplementaryDataDto> response = dataService
        .getSupplementaryData(COMMODITY_CODE, eppoCodes);

    // Then
    verify(supplementaryDataRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityVarietyRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityClassRepository).findAllByCommodityCode(COMMODITY_CODE);
    assertThat(response).containsExactly(supplementaryDataDto());
  }

  @Test
  void getSupplementaryData_ReturnsSupplementaryData_WhenCommodityCodeAndSpeciesNameExist() {
    ChedppSpecies chedppSpecies =
        new ChedppSpecies(COMMODITY_CODE, EPPO_CODE, 1380239, SPECIES_NAME, COMMODITY_DESCRIPTION);
    List<String> eppoCodes = Collections.singletonList(EPPO_CODE);

    when(chedppSpeciesRepository.findFirstByCommodityCodeAndSpeciesName(COMMODITY_CODE,
        SPECIES_NAME))
        .thenReturn(Optional.of(chedppSpecies));
    when(supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Collections.singletonList(supplementaryData()));
    when(commodityVarietyRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Collections.singletonList(commodityVariety()));
    when(commodityClassRepository.findAllByCommodityCode(COMMODITY_CODE))
        .thenReturn(Collections.singletonList(commodityClass()));

    SupplementaryDataDto supplementaryDataDtoResponse =
        dataService.getSupplementaryData(COMMODITY_CODE, SPECIES_NAME);

    assertThat(supplementaryDataDtoResponse).isEqualTo(supplementaryDataDtoWithSpeciesId());
  }

  @Test
  void getSupplementaryData_ThrowsNotFoundException_WhenCommodityCodeAndSpeciesNameDoNotExist() {
    when(chedppSpeciesRepository.findFirstByCommodityCodeAndSpeciesName(COMMODITY_CODE,
        SPECIES_NAME))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> dataService.getSupplementaryData(COMMODITY_CODE, SPECIES_NAME));
  }

  @Test
  void getSupplementaryData_ReturnsCorrectResponse_WhenIntendedUseDataIsPresent() {
    // Given
    List<String> eppoCodes = Collections.singletonList(EPPO_CODE);
    SupplementaryData x = supplementaryData();
    SupplementaryData y = supplementaryData();
    y.setRegulatoryAuthority(RegulatoryAuthority.JOINT);

    when(supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Arrays.asList(x, y));
    when(commodityVarietyRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Collections.singletonList(commodityVariety()));
    when(commodityClassRepository.findAllByCommodityCode(COMMODITY_CODE))
        .thenReturn(Collections.singletonList(commodityClass()));

    // When
    List<SupplementaryDataDto> response = dataService
        .getSupplementaryData(COMMODITY_CODE, eppoCodes);

    // Then
    verify(supplementaryDataRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityVarietyRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityClassRepository).findAllByCommodityCode(COMMODITY_CODE);
    assertThat(response).hasSize(1);
    SupplementaryDataDto responseDto = response.get(0);
    assertThat(responseDto.getEppoCode()).isEqualTo(supplementaryDataDto().getEppoCode());
    assertThat(responseDto.getMarketingStandard()).isEqualTo(
        supplementaryDataDto().getMarketingStandard());
    assertThat(responseDto.getValidityPeriod()).isEqualTo(
        supplementaryDataDto().getValidityPeriod());
    assertThat(responseDto.getVarieties()).isEqualTo(supplementaryDataDto().getVarieties());
    assertThat(responseDto.getClasses()).isEqualTo(supplementaryDataDto().getClasses());
    assertThat(responseDto.getRegulatoryAuthority()).isEqualTo(
        Arrays.asList(RegulatoryAuthority.PHSI, RegulatoryAuthority.JOINT));
  }

  @Test
  void getSupplementaryData_ReturnsCorrectResponse_WhenNoIntendedUseDataIsPresent() {
    // Given
    List<String> eppoCodes = Collections.singletonList(EPPO_CODE);
    SupplementaryData x = supplementaryData();
    SupplementaryData y = supplementaryData();
    y.setRegulatoryAuthority(RegulatoryAuthority.JOINT);

    when(supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Arrays.asList(x, y));
    when(commodityVarietyRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Collections.singletonList(commodityVariety()));
    when(commodityClassRepository.findAllByCommodityCode(COMMODITY_CODE))
        .thenReturn(Collections.singletonList(commodityClass()));

    // When
    List<SupplementaryDataDto> response = dataService
        .getSupplementaryData(COMMODITY_CODE, eppoCodes);

    // Then
    verify(supplementaryDataRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityVarietyRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityClassRepository).findAllByCommodityCode(COMMODITY_CODE);
    assertThat(response).hasSize(1);
    SupplementaryDataDto responseDto = response.get(0);
    assertThat(responseDto.getEppoCode()).isEqualTo(supplementaryDataDto().getEppoCode());
    assertThat(responseDto.getMarketingStandard()).isEqualTo(
        supplementaryDataDto().getMarketingStandard());
    assertThat(responseDto.getValidityPeriod()).isEqualTo(
        supplementaryDataDto().getValidityPeriod());
    assertThat(responseDto.getVarieties()).isEqualTo(supplementaryDataDto().getVarieties());
    assertThat(responseDto.getClasses()).isEqualTo(supplementaryDataDto().getClasses());
    assertThat(responseDto.getRegulatoryAuthority())
        .isEqualTo(Arrays.asList(RegulatoryAuthority.PHSI, RegulatoryAuthority.JOINT));
    assertThat(responseDto.getIntendedUseRegulatoryAuthorities())
        .isEqualTo(
            Arrays.asList(
                IntendedUseRegulatoryAuthority.builder()
                    .intendedUse("")
                    .regulatoryAuthority(RegulatoryAuthority.PHSI)
                    .build(),
                IntendedUseRegulatoryAuthority.builder()
                    .intendedUse("")
                    .regulatoryAuthority(RegulatoryAuthority.JOINT)
                    .build()));
  }

  @Test
  void getSupplementaryData_ReturnsCorrectResponse_WhenDataIsNotPresent() {
    // Given
    List<String> eppoCodes = Collections.singletonList(EPPO_CODE);
    when(supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Collections.emptyList());

    // When
    List<SupplementaryDataDto> response = dataService
        .getSupplementaryData(COMMODITY_CODE, eppoCodes);

    // Then
    verify(supplementaryDataRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityVarietyRepository, never())
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityClassRepository, never()).findAllByCommodityCode(COMMODITY_CODE);
    assertThat(response).isEmpty();
  }

  @Test
  void getSupplementaryDataV2_ReturnsSupplementaryData_WhenCalledWithEppo() {
    // Given
    List<String> eppoCodes = List.of(EPPO_CODE);
    when(supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(List.of(supplementaryData()));
    when(commodityVarietyRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(List.of(commodityVariety()));
    when(commodityClassRepository.findAllByCommodityCode(COMMODITY_CODE))
        .thenReturn(List.of(commodityClass()));

    // When
    List<SupplementaryDataDtoV2> response = dataService.getSupplementaryDataV2(COMMODITY_CODE,
        eppoCodes);

    // Then
    verify(supplementaryDataRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityVarietyRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityClassRepository).findAllByCommodityCode(COMMODITY_CODE);
    assertThat(response).containsExactly(supplementaryDataDtoV2());
  }

  @Test
  void getSupplementaryDataV2_ReturnsCorrectResponse_WhenCalledWithEppoAndNoResults() {
    // Given
    List<String> eppoCodes = List.of(EPPO_CODE);
    when(supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(Collections.emptyList());

    // When
    List<SupplementaryDataDtoV2> response = dataService.getSupplementaryDataV2(COMMODITY_CODE,
        eppoCodes);

    // Then
    verify(supplementaryDataRepository)
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityVarietyRepository, never())
        .findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes);
    verify(commodityClassRepository, never()).findAllByCommodityCode(COMMODITY_CODE);
    assertThat(response).isEmpty();
  }

  @Test
  void getSupplementaryDataV2_ReturnsSupplementaryData_WhenCalledWithSpecies() {
    ChedppSpecies chedppSpecies = ChedppSpecies.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .speciesId(Integer.parseInt(SPECIES_ID))
        .speciesName(SPECIES_NAME)
        .build();
    List<String> eppoCodes = List.of(EPPO_CODE);

    when(chedppSpeciesRepository
        .findFirstByCommodityCodeAndSpeciesName(COMMODITY_CODE, SPECIES_NAME))
        .thenReturn(Optional.of(chedppSpecies));
    when(supplementaryDataRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(List.of(supplementaryData()));
    when(commodityVarietyRepository.findAllByCommodityCodeAndEppoCodeIn(COMMODITY_CODE, eppoCodes))
        .thenReturn(List.of(commodityVariety()));
    when(commodityClassRepository.findAllByCommodityCode(COMMODITY_CODE))
        .thenReturn(List.of(commodityClass()));

    SupplementaryDataDtoV2 supplementaryDataDtoResponse = dataService.getSupplementaryDataV2(
        COMMODITY_CODE, SPECIES_NAME);

    assertThat(supplementaryDataDtoResponse).isEqualTo(supplementaryDataDtoWithSpeciesIdV2());
  }

  @Test
  void getSupplementaryDataV2_ReturnsSupplementaryData_WhenCalledWithCommodityToEppoCodesMappings() {
    // Given
    List<String> commodityCodes = List.of(COMMODITY_CODE);
    String commodityToEppoCodesMappingStructure = "(:c0, :c0e0)";
    List<CommodityEppoCodesMappingDto> mappings = List.of(
        CommodityEppoCodesMappingDto.builder()
            .commodityCode(COMMODITY_CODE)
            .eppoCodes(List.of(EPPO_CODE))
            .build()
    );
    when(supplementaryDataRepository.
        findAllByCommodityCodeAndEppoCodeInv2(any(), any(), any(), any()))
        .thenReturn(List.of(supplementaryData()));

    // When
    List<SupplementaryDataDtoV2> response =
        dataService.getSupplementaryDataV2(commodityCodes, mappings);

    // Then
    verify(supplementaryDataRepository)
        .findAllByCommodityCodeAndEppoCodeInv2(
            entityManager, commodityCodes, commodityToEppoCodesMappingStructure, mappings);
    assertThat(response).containsExactly(supplementaryDataDtoV2NoClassesAndVarieties());
  }

  @Test
  void getSupplementaryDataV2_ThrowsNotFoundException_WhenCalledWithSpeciesAndNoResults() {
    when(chedppSpeciesRepository.findFirstByCommodityCodeAndSpeciesName(COMMODITY_CODE,
        SPECIES_NAME))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> dataService.getSupplementaryDataV2(COMMODITY_CODE, SPECIES_NAME))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getVarietyMarketingStandard_ReturnsCorrectResponse_WhenDataIsPresent() {
    // Given
    List<SearchVariety> searchVarieties = Collections.singletonList(searchVariety());
    List<String> eppoCodes = Collections.singletonList(EPPO_CODE);
    List<String> varieties = Collections.singletonList(VARIETY);
    when(commodityVarietyRepository
        .findByCommodityCodeAndEppoCodeInAndVarietyIn(COMMODITY_CODE, eppoCodes, varieties))
        .thenReturn(List.of(commodityVariety()));

    // When
    List<CommodityVarietyDto> response = dataService.getVarietyMarketingStandard(COMMODITY_CODE,
        searchVarieties);

    // Then
    assertThat(response).containsExactly(commodityVarietyDto());
  }

  @Test
  void getVarietyMarketingStandard_ReturnsCorrectResponse_WhenCalledForSingleVariety() {
    // Given
    List<SearchVariety> searchVarieties = Collections.singletonList(searchVariety());
    List<String> eppoCodes = Collections.singletonList(EPPO_CODE);
    List<String> varieties = Collections.singletonList(VARIETY);
    when(commodityVarietyRepository
        .findByCommodityCodeAndEppoCodeInAndVarietyIn(COMMODITY_CODE, eppoCodes, varieties))
        .thenReturn(List.of(commodityVariety()));

    // When
    List<CommodityVarietyDto> response = dataService.getVarietyMarketingStandard(COMMODITY_CODE,
        searchVarieties);

    // Then
    assertThat(response).containsExactly(commodityVarietyDto());
  }

  @Test
  void getVarietyMarketingStandard_ReturnsCorrectResponse_WhenIncorrectRecordsArePresent() {
    // Given
    final String CIDSI = "CIDSI";
    final String PYUCO = "PYUCO";
    final String NAVEL = "Navel";
    final String HONEY_BELLE = "Honey Belle";

    List<SearchVariety> searchVarieties = Arrays.asList(
        SearchVariety.builder()
            .eppoCode(CIDSI)
            .variety(NAVEL)
            .build(),
        SearchVariety.builder()
            .eppoCode(PYUCO)
            .variety(HONEY_BELLE)
            .build()
    );

    List<CommodityVariety> commodityVarieties = Arrays.asList(
        CommodityVariety.builder()
            .eppoCode(CIDSI)
            .variety(NAVEL)
            .build(),
        CommodityVariety.builder()
            .eppoCode(PYUCO)
            .variety(HONEY_BELLE)
            .build(),
        CommodityVariety.builder()
            .eppoCode(CIDSI)
            .variety(HONEY_BELLE)
            .build()
    );

    List<String> eppoCodes = Arrays.asList(CIDSI, PYUCO);
    List<String> varieties = Arrays.asList(NAVEL, HONEY_BELLE);

    when(commodityVarietyRepository
        .findByCommodityCodeAndEppoCodeInAndVarietyIn(COMMODITY_CODE, eppoCodes, varieties))
        .thenReturn(commodityVarieties);

    // When
    List<CommodityVarietyDto> response = dataService.getVarietyMarketingStandard(COMMODITY_CODE,
        searchVarieties);

    // Then
    assertThat(response).containsExactly(
        CommodityVarietyDto.builder()
            .eppoCode(CIDSI)
            .variety(NAVEL)
            .build(),
        CommodityVarietyDto.builder()
            .eppoCode(PYUCO)
            .variety(HONEY_BELLE)
            .build()
    );
  }

  @Test
  void getVarietyMarketingStandardV2_ReturnsCorrectResponse() {
    // Given
    List<SearchVariety> searchVarieties = List.of(searchVariety());
    List<String> eppoCodes = List.of(EPPO_CODE);
    List<String> varieties = List.of(VARIETY);
    when(commodityVarietyRepository
        .findByCommodityCodeAndEppoCodeInAndVarietyIn(COMMODITY_CODE, eppoCodes, varieties))
        .thenReturn(List.of(commodityVariety()));

    // When
    List<CommodityVarietyDtoV2> response = dataService.getVarietyMarketingStandardV2(COMMODITY_CODE,
        searchVarieties);

    // Then
    assertThat(response).containsExactly(commodityVarietyDtoV2());
  }

  @Test
  void getVarietyMarketingStandardV2_ReturnsCorrectResponse_WhenNoResults() {
    // Given
    List<SearchVariety> searchVarieties = List.of(searchVariety());
    List<String> eppoCodes = List.of(EPPO_CODE);
    List<String> varieties = List.of(VARIETY);
    when(commodityVarietyRepository
        .findByCommodityCodeAndEppoCodeInAndVarietyIn(COMMODITY_CODE, eppoCodes, varieties))
        .thenReturn(List.of());

    // When
    List<CommodityVarietyDtoV2> response = dataService.getVarietyMarketingStandardV2(COMMODITY_CODE,
        searchVarieties);

    // Then
    assertThat(response).isEmpty();
  }

  @Test
  void getVarietyMarketingStandardV2_ReturnsCorrectResponse_WhenIncorrectRecordsArePresent() {
    // Given
    final String CIDSI = "CIDSI";
    final String PYUCO = "PYUCO";
    final String NAVEL = "Navel";
    final String HONEY_BELLE = "Honey Belle";

    List<SearchVariety> searchVarieties = List.of(
        SearchVariety.builder()
            .eppoCode(CIDSI)
            .variety(NAVEL)
            .build(),
        SearchVariety.builder()
            .eppoCode(PYUCO)
            .variety(HONEY_BELLE)
            .build()
    );

    List<CommodityVariety> commodityVarieties = List.of(
        CommodityVariety.builder()
            .eppoCode(CIDSI)
            .variety(NAVEL)
            .build(),
        CommodityVariety.builder()
            .eppoCode(PYUCO)
            .variety(HONEY_BELLE)
            .build(),
        CommodityVariety.builder()
            .eppoCode(CIDSI)
            .variety(HONEY_BELLE)
            .build()
    );

    List<String> eppoCodes = List.of(CIDSI, PYUCO);
    List<String> varieties = List.of(NAVEL, HONEY_BELLE);

    when(commodityVarietyRepository
        .findByCommodityCodeAndEppoCodeInAndVarietyIn(COMMODITY_CODE, eppoCodes, varieties))
        .thenReturn(commodityVarieties);

    // When
    List<CommodityVarietyDtoV2> response = dataService.getVarietyMarketingStandardV2(COMMODITY_CODE,
        searchVarieties);

    // Then
    assertThat(response).containsExactly(
        CommodityVarietyDtoV2.builder()
            .eppoCode(CIDSI)
            .variety(NAVEL)
            .build(),
        CommodityVarietyDtoV2.builder()
            .eppoCode(PYUCO)
            .variety(HONEY_BELLE)
            .build()
    );
  }
}
