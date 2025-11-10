package uk.gov.defra.cdp.trade.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.domain.repository.Article72Repository;
import uk.gov.defra.cdp.trade.demo.dto.Article72CommodityDto;

@Service
public class Article72Service {

  private Article72Repository article72Repository;

  @Autowired
  public Article72Service(Article72Repository article72Repository) {
    this.article72Repository = article72Repository;
  }

  public Article72CommodityDto search(String commodityCode, String eppoCode,
      String commodityGroup) {

    boolean isLowRiskArticle72 = article72Repository
        .findFirstByCommodityCodeAndEppoCodeAndCommodityGroup(commodityCode, eppoCode,
            commodityGroup)
        .isPresent();

    return Article72CommodityDto.builder()
        .commodityCode(commodityCode)
        .eppoCode(eppoCode)
        .commodityGroup(commodityGroup)
        .isLowRiskArticle72(isLowRiskArticle72)
        .build();
  }
}
