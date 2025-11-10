package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.Article72Commodity;

@Repository
public interface Article72Repository extends CrudRepository<Article72Commodity, Integer> {

  Optional<Article72Commodity> findFirstByCommodityCodeAndEppoCodeAndCommodityGroup(
      String commodityCode, String eppoCode, String commodityGroup);
}
