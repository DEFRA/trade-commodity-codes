package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityClass;

public interface CommodityClassRepository extends CrudRepository<CommodityClass, Integer> {

  @Query(nativeQuery = true, value = "SELECT DISTINCT cc.code, cc.class "
      + "FROM inspection_responsibility ir INNER JOIN commodity_class cc ON "
      + "cc.traces_commodity_code = ir.traces_commodity_code "
      + "WHERE ir.traces_commodity_code = :commodityCode")
  List<CommodityClass> findAllByCommodityCode(String commodityCode);
}
