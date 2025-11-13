package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityAttribute;

@Repository
public interface CommodityAttributeRepository extends
    CrudRepository<CommodityAttribute, String> {

  List<CommodityAttribute> findAllByCommodityCodeIn(
      List<String> commodityCodes);
}
