package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityVariety;

public interface CommodityVarietyRepository extends CrudRepository<CommodityVariety, Integer> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT DISTINCT "
              + "ROW_NUMBER() OVER(ORDER BY hm.code ASC) AS id, ir.eppo_code, "
              + " ir.traces_commodity_code, ir.inspection_responsibility, "
              + "hm.hmi_marketing_standard, hm.variety, hm.certificate_validity_period "
              + "FROM inspection_responsibility ir LEFT JOIN hmi_marketing hm ON "
              + "hm.traces_commodity_code = ir.traces_commodity_code "
                  + "AND hm.eppo_code = ir.eppo_code "
              + "INNER JOIN commodity_eppo_variety cev "
              + "ON cev.traces_commodity_code = ir.traces_commodity_code "
              + "AND cev.eppo_code = ir.eppo_code AND cev.variety = hm.variety "
              + "WHERE ir.traces_commodity_code = :commodityCode AND "
              + "ir.eppo_code IN :eppoCodes")
  List<CommodityVariety> findAllByCommodityCodeAndEppoCodeIn(
      String commodityCode, List<String> eppoCodes);

  @Query(
      nativeQuery = true,
      value =
          "SELECT DISTINCT "
              + "ROW_NUMBER() OVER(ORDER BY hm.code ASC) AS id, ir.eppo_code, "
              + "ir.traces_commodity_code, ir.inspection_responsibility, "
              + "hm.hmi_marketing_standard, hm.variety, hm.certificate_validity_period "
              + "FROM inspection_responsibility ir LEFT JOIN hmi_marketing hm ON "
              + "hm.traces_commodity_code = ir.traces_commodity_code "
                  + "AND hm.eppo_code = ir.eppo_code "
              + "INNER JOIN commodity_eppo_variety cev "
              + "ON cev.traces_commodity_code = ir.traces_commodity_code "
              + "AND cev.eppo_code = ir.eppo_code AND cev.variety = hm.variety "
              + "WHERE ir.traces_commodity_code = :commodityCode "
              + "AND ir.eppo_code IN :eppoCodes AND hm.variety IN :varieties")
  List<CommodityVariety> findByCommodityCodeAndEppoCodeInAndVarietyIn(
      String commodityCode, List<String> eppoCodes, List<String> varieties);
}
