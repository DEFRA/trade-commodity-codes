package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityAttribute;

@Repository
public interface CommodityAttributeRepository extends
    CrudRepository<CommodityAttribute, String> {
  @Query(nativeQuery = true, value = "select distinct result.code, result.traces_commodity_code, "
          + "result.propagation "
          + "from commodity_attributes ca inner join lateral"
          + "(select * from commodity_attributes where "
          + "commodity_attributes.traces_commodity_code = ca.traces_commodity_code limit 1) "
          + "result on ca.traces_commodity_code IN :commodityCodes")
  List<CommodityAttribute> findAllByCommodityCodeIn(
      List<String> commodityCodes);
}
