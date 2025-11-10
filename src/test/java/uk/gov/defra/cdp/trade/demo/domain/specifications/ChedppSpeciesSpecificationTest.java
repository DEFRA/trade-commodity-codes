package uk.gov.defra.cdp.trade.demo.domain.specifications;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;

@ExtendWith(MockitoExtension.class)
class ChedppSpeciesSpecificationTest {

  private static final String COMMODITY_CODE_KEY = "commodityCode";
  private static final String EPPO_CODE_KEY = "eppoCode";
  private static final String SPECIES_NAME_KEY = "speciesName";
  private static final String SPECIES_ID_KEY = "speciesId";
  private static final String COMMODITY_CODE = "1234567";
  private static final String EPPO_CODE = "MABSD";
  private static final String SPECIES_NAME = "Apple";
  private static final int SPECIES_ID = 7654321;

  @Mock
  private CriteriaBuilder criteriaBuilderMock;

  @Mock
  private CriteriaQuery criteriaQueryMock;

  @Mock
  private Root<ChedppSpecies> rootMock;

  @Test
  void chedppSpeciesSpecificationShouldIncludeCommodityCodePredicate() {
    // Given
    Path commodityCodePathMock = mock(Path.class);
    when(rootMock.get(COMMODITY_CODE_KEY)).thenReturn(commodityCodePathMock);

    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, null, null, null, false);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    verify(rootMock, times(1)).get(COMMODITY_CODE_KEY);
    verify(criteriaBuilderMock, times(1)).equal(commodityCodePathMock, COMMODITY_CODE);
  }

  @Test
  void chedppSpeciesSpecificationShouldIncludeEppoCodePredicateIfNotNull() {
    // Given
    Path eppoCodePathMock = mock(Path.class);
    when(rootMock.get(COMMODITY_CODE_KEY)).thenReturn(eppoCodePathMock);
    when(rootMock.get(EPPO_CODE_KEY)).thenReturn(eppoCodePathMock);

    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, EPPO_CODE, null, null, false);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    final String expectedPattern = "%" + EPPO_CODE + "%";
    verify(rootMock, times(1)).get(EPPO_CODE_KEY);
    verify(criteriaBuilderMock, times(1)).like(eppoCodePathMock, expectedPattern);
  }

  @Test
  void chedppSpeciesSpecificationShouldNotIncludeEppoCodePredicateIfNull() {
    // Given
    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, null, null, null, false);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    verify(rootMock, never()).get(EPPO_CODE_KEY);
  }

  @Test
  void chedppSpeciesSpecificationShouldIncludeSpeciesNamePredicateIfNotNull() {
    // Given
    Path speciesNamePathMock = mock(Path.class);
    when(rootMock.get(COMMODITY_CODE_KEY)).thenReturn(speciesNamePathMock);
    when(rootMock.get(SPECIES_NAME_KEY)).thenReturn(speciesNamePathMock);

    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, null, SPECIES_NAME, null, false);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    final String expectedPattern = "%" + SPECIES_NAME + "%";
    verify(rootMock, times(1)).get(SPECIES_NAME_KEY);
    verify(criteriaBuilderMock, times(1)).like(speciesNamePathMock, expectedPattern);
  }

  @Test
  void chedppSpeciesSpecificationShouldNotIncludeSpeciesNamePredicateIfNull() {
    // Given
    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, null, null, null, false);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    verify(rootMock, never()).get(SPECIES_NAME_KEY);
  }

  @Test
  void chedppSpeciesSpecificationShouldIncludeexcludeSpeciesIdsPredicateIfListHasItems() {
    // Given
    Path speciesIdPathMock = mock(Path.class);
    when(rootMock.get(COMMODITY_CODE_KEY)).thenReturn(speciesIdPathMock);
    when(rootMock.get(SPECIES_ID_KEY)).thenReturn(speciesIdPathMock);

    // When
    List<Integer> excludeSpeciesIds = Collections.singletonList(SPECIES_ID);
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, null, null, excludeSpeciesIds, false);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    verify(rootMock, times(1)).get(SPECIES_ID_KEY);
    verify(criteriaBuilderMock, times(1)).not(speciesIdPathMock.in(excludeSpeciesIds));
  }

  @Test
  void chedppSpeciesSpecificationShouldNotIncludeexcludeSpeciesIdsPredicateIfListIsNull() {
    // Given
    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, null, null, null, false);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    verify(rootMock, never()).get(SPECIES_ID_KEY);
  }

  @Test
  void chedppSpeciesSpecificationShouldNotIncludeexcludeSpeciesIdsPredicateIfListIsEmpty() {
    // Given
    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, null, null, Collections.emptyList(), false);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    verify(rootMock, never()).get(SPECIES_ID_KEY);
  }

  @Test
  void buildChedppSpeciesSpecification_IncludesEppoCodePredicateWithExactMatch_WhenProvided() {
    // Given
    Path eppoCodePathMock = mock(Path.class);
    when(rootMock.get(COMMODITY_CODE_KEY)).thenReturn(eppoCodePathMock);
    when(rootMock.get(EPPO_CODE_KEY)).thenReturn(eppoCodePathMock);

    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, EPPO_CODE, null, null, true);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    String expectedPattern = EPPO_CODE;
    verify(rootMock, times(1)).get(EPPO_CODE_KEY);
    verify(criteriaBuilderMock, times(1)).equal(eppoCodePathMock, expectedPattern);
  }

  @Test
  void buildChedppSpeciesSpecification_IncludesSpeciesNamePredicateWithExactMatch_WhenProvided() {
    // Given
    Path speciesNamePathMock = mock(Path.class);
    when(rootMock.get(COMMODITY_CODE_KEY)).thenReturn(speciesNamePathMock);
    when(rootMock.get(SPECIES_NAME_KEY)).thenReturn(speciesNamePathMock);

    // When
    Specification<ChedppSpecies> specification =
        ChedppSpeciesSpecification.buildChedppSpeciesSpecification(
            COMMODITY_CODE, null, SPECIES_NAME, null, true);
    specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

    // Then
    String expectedPattern = SPECIES_NAME;
    verify(rootMock, times(1)).get(SPECIES_NAME_KEY);
    verify(criteriaBuilderMock, times(1)).equal(speciesNamePathMock, expectedPattern);
  }
}
