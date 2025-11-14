package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCategory;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCategoryId;

@Repository
public interface CommodityCategoryRepository
    extends CrudRepository<CommodityCategory, CommodityCategoryId> {

  Optional<CommodityCategory> findByCertificateTypeAndCommodityCode(
      String certificateType, String commodityCode);
}
