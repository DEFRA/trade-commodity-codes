package uk.gov.defra.cdp.trade.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.defra.cdp.trade.demo.CommodityCategoryFixtures;
import uk.gov.defra.cdp.trade.demo.domain.ChedpSpecies;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;
import uk.gov.defra.cdp.trade.demo.domain.repository.ChedpSpeciesRepository;
import uk.gov.defra.cdp.trade.demo.domain.repository.ChedppSpeciesRepository;
import uk.gov.defra.cdp.trade.demo.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class CommoditySpeciesServiceTest {

  private static final String COMMODITY_CODE = "01";
  private static final String EPPO_CODE = "MABSD";
  private static final String SPECIES_NAME = "Malus";
  private static final String SPECIES_NAME_KEY = "speciesName";
  private static final List<Integer> EXCLUDE_SPECIES_IDS = Arrays.asList(23093, 23092);
  private static final int SPECIES_ID = 12345;
  private static final int PAGE_NUMBER = 0;
  private static final int PAGE_SIZE = 25;

  @Mock
  ChedppSpeciesRepository chedppSpeciesRepository;

  @Mock
  ChedpSpeciesRepository chedpSpeciesRepository;

  private CommoditySpeciesService commoditySpeciesService;

  @BeforeEach
  void setUp() {
    commoditySpeciesService = new CommoditySpeciesService(chedppSpeciesRepository,
        chedpSpeciesRepository);
  }

  @Test
  void getChedppSpeciesShouldCallChedppSpeciesRepositoryWithCorrectParameters() {
    // Given
    Page<ChedppSpecies> response = CommodityCategoryFixtures
        .chedppSpeciesPaginatedResponseFixture();
    Pageable expectedPageRequest = PageRequest
        .of(PAGE_NUMBER, PAGE_SIZE, Sort.by(SPECIES_NAME_KEY).ascending());
    when(chedppSpeciesRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(response);

    // When
    var result = commoditySpeciesService
        .getChedppSpecies(COMMODITY_CODE, EPPO_CODE, SPECIES_NAME, EXCLUDE_SPECIES_IDS,
            PAGE_NUMBER, false);

    // Then
    verify(chedppSpeciesRepository, times(1))
        .findAll(any(Specification.class), eq(expectedPageRequest));
    assertThat(result.getContent().get(0))
        .isEqualToComparingFieldByField(CommodityCategoryFixtures.chedppSpeciesFixture());
  }

  @Test
  void getChedppSingleSpeciesShouldCallChedppSpeciesRepositoryWithCorrectParameters() {
    // Given
    Optional<ChedppSpecies> response = Optional
        .of(CommodityCategoryFixtures.chedppSpeciesFixture());
    when(chedppSpeciesRepository.findFirstByCommodityCodeAndSpeciesId(COMMODITY_CODE, SPECIES_ID))
        .thenReturn(response);

    // When
    var result = commoditySpeciesService.getSingleChedppSpecies(COMMODITY_CODE, SPECIES_ID);

    // Then
    verify(chedppSpeciesRepository, times(1))
        .findFirstByCommodityCodeAndSpeciesId(COMMODITY_CODE, SPECIES_ID);
    assertThat(result)
        .isEqualToComparingFieldByField(CommodityCategoryFixtures.chedppSpeciesFixture());
  }

  @Test
  void getChedppSingleSpeciesShouldThrowNotFoundExceptionWhenRepositoryReturnsEmptyOptional() {
    // Given
    Optional<ChedppSpecies> response = Optional.empty();
    when(chedppSpeciesRepository.findFirstByCommodityCodeAndSpeciesId(COMMODITY_CODE, SPECIES_ID))
        .thenReturn(response);

    // When// Then
    assertThatThrownBy(
        () -> commoditySpeciesService.getSingleChedppSpecies(COMMODITY_CODE, SPECIES_ID))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getChedpSpecies_ReturnsChedpSpecies_WhenCommodityCodeAndSpeciesNamePassed() {
    // Given
    Optional<ChedpSpecies> response = Optional
        .of(CommodityCategoryFixtures.chedpSpeciesFixture());
    when(chedpSpeciesRepository.findFirstByCommodityCodeAndSpeciesName(any(), any()))
        .thenReturn(response);

    // When
    var result = commoditySpeciesService.getChedpSpecies(COMMODITY_CODE, SPECIES_NAME);

    // Then
    verify(chedpSpeciesRepository, times(1))
        .findFirstByCommodityCodeAndSpeciesName(any(), any());
    assertThat(result).isEqualTo(response.get());
  }

  @Test
  void getChedpSpecies_ReturnsNotFound_WhenCommodityCodeAndSpeciesNameNotPassed() {
    // Given
    when(chedpSpeciesRepository.findFirstByCommodityCodeAndSpeciesName(any(), any()))
        .thenThrow(NotFoundException.class);

    // When // Then
    verify(chedpSpeciesRepository, never())
        .findFirstByCommodityCodeAndSpeciesName(any(), any());
    assertThatThrownBy(() -> commoditySpeciesService.getChedpSpecies(COMMODITY_CODE, SPECIES_NAME))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getChedppSpeciesCount_ReturnsCount_WhenCommodityCodeIsPassed() {
    // Given
    when(chedppSpeciesRepository.countChedppSpeciesByCommodityCode(anyString()))
        .thenReturn(5);

    // When
    int result = commoditySpeciesService.getChedppSpeciesCount(COMMODITY_CODE);

    // Then
    verify(chedppSpeciesRepository, times(1))
        .countChedppSpeciesByCommodityCode(any());
    assertThat(result).isEqualTo(5);
  }
}
