package uk.gov.defra.cdp.trade.demo.service;

import static uk.gov.defra.cdp.trade.demo.domain.specifications.ChedppSpeciesSpecification.buildChedppSpeciesSpecification;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.domain.ChedpSpecies;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;
import uk.gov.defra.cdp.trade.demo.domain.repository.ChedpSpeciesRepository;
import uk.gov.defra.cdp.trade.demo.domain.repository.ChedppSpeciesRepository;
import uk.gov.defra.cdp.trade.demo.exceptions.NotFoundException;

@Service
public class CommoditySpeciesService {

  private final ChedppSpeciesRepository chedppSpeciesRepository;
  private final ChedpSpeciesRepository chedpSpeciesRepository;
  private static final String SPECIES_NAME = "speciesName";
  private static final int NUMBER_OF_RESULTS = 25;

  @Autowired
  public CommoditySpeciesService(ChedppSpeciesRepository chedppSpeciesRepository,
      ChedpSpeciesRepository chedpSpeciesRepository) {
    this.chedppSpeciesRepository = chedppSpeciesRepository;
    this.chedpSpeciesRepository = chedpSpeciesRepository;
  }

  public Page<ChedppSpecies> getChedppSpecies(String commodityCode, String eppoCode,
      String speciesName, List<Integer> excludeSpeciesIds, int pageNumber, boolean exactMatch) {

    Specification<ChedppSpecies> specification = buildChedppSpeciesSpecification(commodityCode,
        eppoCode, speciesName, excludeSpeciesIds, exactMatch);

    Pageable pageable = PageRequest
        .of(pageNumber, NUMBER_OF_RESULTS, Sort.by(SPECIES_NAME).ascending());

    return chedppSpeciesRepository.findAll(specification, pageable);
  }

  public ChedppSpecies getSingleChedppSpecies(String commodityCode, int speciesId) {
    return chedppSpeciesRepository.findFirstByCommodityCodeAndSpeciesId(commodityCode, speciesId)
        .orElseThrow(NotFoundException::new);
  }

  public ChedpSpecies getChedpSpecies(String commodityCode, String speciesName) {
    return chedpSpeciesRepository.findFirstByCommodityCodeAndSpeciesName(commodityCode, speciesName)
        .orElseThrow(NotFoundException::new);
  }

  public int getChedppSpeciesCount(String commodityCode) {
    return chedppSpeciesRepository.countChedppSpeciesByCommodityCode(commodityCode);
  }
}
