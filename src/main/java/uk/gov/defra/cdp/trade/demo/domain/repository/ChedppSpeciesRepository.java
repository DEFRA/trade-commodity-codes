package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCodeSpeciesId;

@Repository
public interface ChedppSpeciesRepository extends
    PagingAndSortingRepository<ChedppSpecies, CommodityCodeSpeciesId>,
    JpaSpecificationExecutor<ChedppSpecies> {

  Page<ChedppSpecies> findAll(Specification specification, Pageable pageable);

  Optional<ChedppSpecies> findFirstByCommodityCodeAndSpeciesId(String commodityCode, int speciesId);

  Optional<ChedppSpecies> findFirstByCommodityCodeAndSpeciesName(String commodityCode,
      String speciesName);

  int countChedppSpeciesByCommodityCode(String commodityCode);
}
