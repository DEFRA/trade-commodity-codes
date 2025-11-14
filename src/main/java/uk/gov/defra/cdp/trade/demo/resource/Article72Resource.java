package uk.gov.defra.cdp.trade.demo.resource;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.cdp.trade.demo.domain.Article72SearchPayload;
import uk.gov.defra.cdp.trade.demo.dto.Article72CommodityDto;
import uk.gov.defra.cdp.trade.demo.service.Article72Service;

@RestController
@RequestMapping("/article-72")
public class Article72Resource {

  private Article72Service article72Service;

  public Article72Resource(Article72Service article72Service) {
    this.article72Service = article72Service;
  }

  @PostMapping
  public ResponseEntity<Article72CommodityDto> search(
      @RequestBody @Valid Article72SearchPayload commoditySearch) {

    return ResponseEntity.ok(article72Service.search(commoditySearch.getCommodityCode(),
        commoditySearch.getEppoCode(), commoditySearch.getCommodityGroup()));
  }
}
