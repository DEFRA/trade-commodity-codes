package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.ChedpSpecies;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCodeSpeciesId;

@Repository
public interface ChedpSpeciesRepository extends
    PagingAndSortingRepository<ChedpSpecies, CommodityCodeSpeciesId>,
    JpaSpecificationExecutor<ChedpSpecies> {

  Optional<ChedpSpecies> findFirstByCommodityCodeAndSpeciesName(String commodityCode,
      String speciesName);
}
