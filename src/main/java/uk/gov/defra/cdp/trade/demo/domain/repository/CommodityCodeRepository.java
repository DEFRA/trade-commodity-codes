package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCodeId;

@Repository
public interface CommodityCodeRepository extends JpaRepository<CommodityCode, CommodityCodeId> {

  List<CommodityCode> findAllByCertTypeIsLikeAndImmediateParentIsNullOrderByCode(
      final String certType);

  List<CommodityCode> findAllByCertTypeIsLikeAndImmediateParentIsLikeOrderByCode(
      final String certType, final String parentCommodityCode);

  List<CommodityCode> findAllByCertTypeIsLikeAndCodeIsLikeOrderByCode(
      final String certType, final String code);

  CommodityCode findCommodityCodeByCertTypeIsLikeAndCodeIsOrderByCode(
      final String certType, final String code);

  List<CommodityCode> findCommodityCodeByCodeIn(final List<String> commodityCodes);

  @Query(nativeQuery = true, value = "WITH ChildCodes AS ( "
      + "  SELECT code, code as 'initial_child' "
      + "  FROM v_commodity_code "
      + "  WHERE cert_type = ?1 "
      + "    AND immediate_parent = ?2 "
      + "  UNION ALL "
      + "  SELECT child.code, parent.initial_child "
      + "  FROM v_commodity_code AS child "
      + "    INNER JOIN ChildCodes AS parent "
      + "      ON child.immediate_parent = parent.code "
      + "    WHERE child.cert_type = ?1 "
      + ") "
      + "SELECT DISTINCT vcc.* "
      + "FROM v_commodity_code_species vccs "
      + "  JOIN ChildCodes cc ON cc.code = vccs.code "
      + "  JOIN v_commodity_code vcc ON vcc.code = cc.initial_child "
      + " AND vcc.cert_type = vccs.cert_type "
      + "WHERE vccs.species = ?3 "
      + "  AND vcc.cert_type = ?1 "
      + "ORDER BY code")
  List<CommodityCode> findChildCommoditiesByCertTypeAndSpecies(
      final String certType, final String parentCommodityCode, final String species);

  @Query(nativeQuery = true, value = "WITH ChildCodes AS ( "
      + "  SELECT code, code as 'initial_child' "
      + "  FROM v_commodity_code "
      + "  WHERE cert_type = ?1 "
      + "    AND immediate_parent IS NULL "
      + "  UNION ALL "
      + "  SELECT child.code, parent.initial_child "
      + "  FROM v_commodity_code AS child "
      + "    INNER JOIN ChildCodes AS parent "
      + "      ON child.immediate_parent = parent.code "
      + "    WHERE child.cert_type = ?1 "
      + ") "
      + "SELECT DISTINCT vcc.* "
      + "FROM v_commodity_code_species vccs "
      + "  JOIN ChildCodes cc ON cc.code = vccs.code "
      + "  JOIN v_commodity_code vcc ON vcc.code = cc.initial_child "
      + " AND vcc.cert_type = vccs.cert_type "
      + "WHERE vcc.cert_type = ?1 "
      + "  AND vccs.species = ?2 "
      + "ORDER BY code")
  List<CommodityCode> findTopLevelCommoditiesByCertTypeAndSpecies(
      final String certType, final String species);

  @Query(nativeQuery = true, value = "SELECT DISTINCT vccs.* "
      + "FROM v_commodity_code_species vccs "
      + "WHERE vccs.code = ?1 "
      + "  AND vccs.cert_type = ?2 "
      + "  AND vccs.species = ?3 "
      + "ORDER BY code")
  List<CommodityCode> findAllCommoditiesByCodeCertTypeAndSpecies(
      String commodityCode, String certType, String species);
}
