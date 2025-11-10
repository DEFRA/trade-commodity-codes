package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityConfiguration;
import uk.gov.defra.cdp.trade.demo.enumerations.ChedType;

@Repository
public interface CommodityConfigurationRepository extends
    CrudRepository<CommodityConfiguration, Long> {

  List<CommodityConfiguration> findAllByCommodityCodeInAndType(
      List<String> commodityCodes, ChedType type
  );
}
