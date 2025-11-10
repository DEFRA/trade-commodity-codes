package uk.gov.defra.cdp.trade.demo.domain.repository;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uk.gov.defra.cdp.trade.demo.domain.SupplementaryData;
import uk.gov.defra.cdp.trade.demo.enumerations.RegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.dto.CommodityEppoCodesMappingDto;

public interface SupplementaryDataRepository extends CrudRepository<SupplementaryData, Integer> {
  @Query(
      nativeQuery = true,
      value =
          "SELECT DISTINCT "
              + "ir.code, ir.traces_commodity_code, ir.eppo_code, "
              + "ir.inspection_responsibility, hm.hmi_marketing_standard, "
              + "hm.certificate_validity_period "
              + "FROM "
              + " (SELECT DISTINCT code, traces_commodity_code, eppo_code, "
              + "                  inspection_responsibility "
              + "  FROM inspection_responsibility "
              + "  WITH (NOLOCK index(ix_inspection_responsibility_traces_commodity_and_eppo_code))"
              + "  WHERE traces_commodity_code = :commodityCode "
              + "  AND eppo_code IN :eppoCodes"
              + "  ) ir "
              + "LEFT OUTER JOIN hmi_marketing hm  "
              + "WITH (NOLOCK index(ix_hmi_marketing_traces_commodity_and_eppo_code)) "
              + "ON hm.traces_commodity_code = ir.traces_commodity_code "
              + "AND hm.eppo_code = ir.eppo_code "
              + "LEFT JOIN commodity_eppo_variety cev "
              + "WITH (NOLOCK index(ix_commodity_eppo_variety)) "
              + "ON cev.traces_commodity_code = ir.traces_commodity_code "
              + "AND cev.eppo_code = ir.eppo_code AND cev.variety = hm.variety")
  List<SupplementaryData> findAllByCommodityCodeAndEppoCodeIn(
      String commodityCode, List<String> eppoCodes);

  default List<SupplementaryData> findAllByCommodityCodeAndEppoCodeInv2(
      EntityManager entityManager, List<String> commodityCodes,
      String commodityToEppoCodeMappingsStructure,
      List<CommodityEppoCodesMappingDto> commodityToEppoCodeMappings) {
    String queryString = "SELECT DISTINCT "
        + "ir.code, ir.traces_commodity_code, ir.eppo_code, "
        + "ir.inspection_responsibility, hm.hmi_marketing_standard, "
        + "hm.certificate_validity_period "
        + "FROM "
        + "  (SELECT code, traces_commodity_code, eppo_code, inspection_responsibility "
        + "    FROM inspection_responsibility "
        + "    WITH (NOLOCK index(ix_inspection_responsibility_traces_commodity_and_eppo_code)) "
        + "    WHERE traces_commodity_code IN :commodityCodes "
        + "    AND eppo_code IN "
        + "    (SELECT cte.eppo FROM (VALUES " + commodityToEppoCodeMappingsStructure + ") "
        + "      AS cte(code, eppo) "
        + "      WHERE traces_commodity_code = cte.code "
        + "    ) "
        + "  ) ir "
        + "LEFT OUTER JOIN hmi_marketing hm "
        + "WITH (NOLOCK index(ix_hmi_marketing_traces_commodity_and_eppo_code)) "
        + "ON hm.traces_commodity_code = ir.traces_commodity_code "
        + "AND hm.eppo_code = ir.eppo_code "
        + "LEFT JOIN commodity_eppo_variety cev "
        + "WITH (NOLOCK index(ix_commodity_eppo_variety)) "
        + "ON cev.traces_commodity_code = ir.traces_commodity_code "
        + "AND cev.eppo_code = ir.eppo_code AND cev.variety = hm.variety";
    jakarta.persistence.Query query = entityManager.createNativeQuery(queryString);
    query.setParameter("commodityCodes", commodityCodes);

    IntStream.range(0, commodityToEppoCodeMappings.size())
        .forEach(indexCommodity -> {
          String commodityCode = commodityToEppoCodeMappings.get(indexCommodity).getCommodityCode();
          List<String> eppoCodes = commodityToEppoCodeMappings.get(indexCommodity).getEppoCodes();
          query.setParameter("c" + indexCommodity, commodityCode);

          IntStream.range(0, eppoCodes.size())
              .forEach(indexEppoCode -> {
                query.setParameter(
                    "c" + indexCommodity + "e" + indexEppoCode, eppoCodes.get(indexEppoCode));
              });
        });

    List<SupplementaryData> supplementaryDataList = new ArrayList<>();
    for (Object result : query.getResultList()) {
      Object[] dto = (Object[]) result;
      supplementaryDataList.add(SupplementaryData.builder()
          .code((String) dto[0])
          .commodityCode((String) dto[1])
          .eppoCode((String) dto[2])
          .regulatoryAuthority(RegulatoryAuthority.valueOf((String) dto[3])).build());
    }
    return supplementaryDataList;
  }
}
