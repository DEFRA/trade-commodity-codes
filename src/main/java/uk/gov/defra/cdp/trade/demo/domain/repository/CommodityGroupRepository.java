package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityGroup;

@Repository
public interface CommodityGroupRepository extends JpaRepository<CommodityGroup, Integer> {

  List<CommodityGroup> findDistinctByCommodityCodeInOrderByCommodityCode(
            @Param("commodityCodes") final List<String> commodityCodes);
}
