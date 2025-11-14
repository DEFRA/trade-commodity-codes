package uk.gov.defra.cdp.trade.demo.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCategoryDto;
import uk.gov.defra.cdp.trade.demo.service.CommodityCategoryService;

@RestController
@RequestMapping("/commodity-categories")
public class CommodityCategoryResource {
  private final CommodityCategoryService commodityCategoryService;

  @Autowired
  public CommodityCategoryResource(CommodityCategoryService commodityCategoryService) {
    this.commodityCategoryService = commodityCategoryService;
  }

  @GetMapping(value = "/{certType}-{commodityCode}")
  public CommodityCategoryDto get(
      @PathVariable("certType") String certType,
      @PathVariable("commodityCode") String commodityCode) {

    return commodityCategoryService.get(certType.replace("-", "").toLowerCase(), commodityCode)
        .orElse(null);
  }
}