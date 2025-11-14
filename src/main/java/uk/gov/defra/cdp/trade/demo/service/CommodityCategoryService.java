package uk.gov.defra.cdp.trade.demo.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCategoryRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCategoryDto;

@Service
public class CommodityCategoryService {

  private final CommodityCategoryRepository commodityCategoryRepository;

  @Autowired
  public CommodityCategoryService(CommodityCategoryRepository commodityCategoryRepository) {
    this.commodityCategoryRepository = commodityCategoryRepository;
  }

  public Optional<CommodityCategoryDto> get(String certificateType, String commodityCode) {
    return commodityCategoryRepository
        .findByCertificateTypeAndCommodityCode(certificateType, commodityCode)
        .map(CommodityCategoryDto::from);
  }
}
