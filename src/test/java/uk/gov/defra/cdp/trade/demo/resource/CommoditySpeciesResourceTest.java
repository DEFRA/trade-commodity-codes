package uk.gov.defra.cdp.trade.demo.resource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.dto.CommoditySearchParametersDto;
import uk.gov.defra.cdp.trade.demo.service.CommoditySpeciesService;

@ExtendWith(MockitoExtension.class)
class CommoditySpeciesResourceTest {

  private static final String COMMODITY_CODE = "0123456";

  @Mock
  CommoditySpeciesService commoditySpeciesService;

  CommoditySpeciesResource commoditySpeciesResource;

  @BeforeEach
  void setUp() {
    commoditySpeciesResource = new CommoditySpeciesResource(commoditySpeciesService);
  }

  @Test
  void testGetCallsServiceGetChedppSpeciesWithCorrectParameters() {
    // Given
    final String eppoCode = "MABSD";
    final String speciesName = "Spec";
    final List<Integer> excludeSpeciesIds = List.of(1292309, 9829182);

    // When
    commoditySpeciesResource.get(
        COMMODITY_CODE, eppoCode, speciesName, excludeSpeciesIds, 0, false);

    // Then
    verify(commoditySpeciesService, times(1))
        .getChedppSpecies(COMMODITY_CODE, eppoCode, speciesName, excludeSpeciesIds, 0, false);
  }

  @Test
  void testGetCallsServiceGetSingleChedppSpeciesWithCorrectParameters() {
    // Given
    final int speciesId = 9283298;

    // When
    commoditySpeciesResource.getChedppSpecies(COMMODITY_CODE, speciesId);

    // Then
    verify(commoditySpeciesService, times(1)).getSingleChedppSpecies(COMMODITY_CODE, speciesId);
  }

  @Test
  void getChedpSpecies_ReturnsChedpSpecies_WhenValidCommodityCodeAndSpeciesNamePassed() {
    // Given
    final String speciesName = "Thunnus thynnus";

    // When
    commoditySpeciesResource.getChedpSpecies(COMMODITY_CODE, speciesName);

    // Then
    verify(commoditySpeciesService, times(1)).getChedpSpecies(COMMODITY_CODE, speciesName);
  }

  @Test
  void post_ReturnsPagedChedppSpeciesData_WithNoPageNumberOrExactMatchSupplied() {
    CommoditySearchParametersDto searchParameters = CommoditySearchParametersDto.builder()
        .eppoCode("MABSD")
        .speciesName("Malus domestica")
        .excludeSpeciesIds(List.of(1332926, 1362609))
        .build();

    commoditySpeciesResource.post(COMMODITY_CODE, searchParameters);

    verify(commoditySpeciesService, times(1))
        .getChedppSpecies(COMMODITY_CODE, "MABSD", "Malus domestica",
            List.of(1332926, 1362609), 0, false);
  }

  @Test
  void post_ReturnsPagedChedppSpeciesData_WhenPageNumberAndExactMatchSupplied() {
    CommoditySearchParametersDto searchParameters = CommoditySearchParametersDto.builder()
        .eppoCode("MABSD")
        .speciesName("Malus domestica")
        .excludeSpeciesIds(List.of(1332926, 1362609))
        .pageNumber(5)
        .exactMatch(true)
        .build();

    commoditySpeciesResource.post(COMMODITY_CODE, searchParameters);

    verify(commoditySpeciesService, times(1))
        .getChedppSpecies(COMMODITY_CODE, "MABSD", "Malus domestica",
            List.of(1332926, 1362609), 5, true);
  }

  @Test
  void test_getChedppSpeciesCount_isCalled_WhenValidCommodityCodePassed() {
    // When
    commoditySpeciesResource.getChedppSpeciesCount(COMMODITY_CODE);

    // Then
    verify(commoditySpeciesService, times(1)).getChedppSpeciesCount(COMMODITY_CODE);
  }
}
