package uk.gov.defra.cdp.trade.demo.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCodeSpeciesRepository;

@ExtendWith(MockitoExtension.class)
class SpeciesServiceTest {

  private static final String CVEDP = "cvedp";
  private static final String CVED_P = "CVED-P";
  private static final String SPECIES_SEARCH_STRING = "mal";

  @Mock
  private CommodityCodeSpeciesRepository commodityCodeSpeciesRepository;

  @InjectMocks
  private SpeciesService speciesService;

  @Test
  void getCommoditySpecies_CallsRepositoryWithCorrectParameters() {
    speciesService.getCommoditySpecies(CVEDP, SPECIES_SEARCH_STRING);

    verify(commodityCodeSpeciesRepository)
        .findDistinctSpeciesWithCertTypeIgnoringCase(CVED_P, SPECIES_SEARCH_STRING);
  }
}
