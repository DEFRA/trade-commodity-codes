package uk.gov.defra.cdp.trade.demo.resource;

import io.micrometer.core.annotation.Timed;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.cdp.trade.demo.domain.ChedpSpecies;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;
import uk.gov.defra.cdp.trade.demo.dto.CommoditySearchParametersDto;
import uk.gov.defra.cdp.trade.demo.service.CommoditySpeciesService;

@RestController
@RequestMapping("/commodity-species")
public class CommoditySpeciesResource {

  private final CommoditySpeciesService commoditySpeciesService;

  @Autowired
  public CommoditySpeciesResource(CommoditySpeciesService commoditySpeciesService) {
    this.commoditySpeciesService = commoditySpeciesService;
  }

  @GetMapping(value = "/chedpp/{commodityCode}")
  @Timed("controller.get.time")
  public ResponseEntity<Page<ChedppSpecies>> get(
      @PathVariable("commodityCode") String commodityCode,
      @RequestParam(value = "eppoCode", required = false) String eppoCode,
      @RequestParam(value = "speciesName", required = false) String speciesName,
      @RequestParam(value = "excludeSpeciesIds", required = false) List<Integer> excludeSpeciesIds,
      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(value = "exactMatch", required = false, defaultValue = "false")
          boolean exactMatch) {

    return ResponseEntity.ok(
        commoditySpeciesService.getChedppSpecies(
            commodityCode, eppoCode, speciesName, excludeSpeciesIds, pageNumber, exactMatch));
  }

  @GetMapping(value = "/chedpp/{commodityCode}/count")
  @Timed("controller.getChedppSpeciesCount.time")
  public ResponseEntity<Integer> getChedppSpeciesCount(
      @PathVariable("commodityCode") String commodityCode) {
    return ResponseEntity.ok(commoditySpeciesService.getChedppSpeciesCount(commodityCode));
  }

  @PostMapping(value = "/chedpp/{commodityCode}")
  @Timed("controller.postChedppSpecies.time")
  public ResponseEntity<Page<ChedppSpecies>> post(
      @PathVariable("commodityCode") String commodityCode,
      @RequestBody CommoditySearchParametersDto searchParameters) {

    return ResponseEntity.ok(
        commoditySpeciesService.getChedppSpecies(
            commodityCode, searchParameters.getEppoCode(), searchParameters.getSpeciesName(),
            searchParameters.getExcludeSpeciesIds(), searchParameters.getPageNumber(),
            searchParameters.isExactMatch()));
  }

  @GetMapping(value = "/chedpp/{commodityCode}/species/{speciesId}")
  @Timed("controller.getChedppSpecies.time")
  public ResponseEntity<ChedppSpecies> getChedppSpecies(
      @PathVariable("commodityCode") final String commodityCode,
      @PathVariable("speciesId") final int speciesId) {

    return ResponseEntity
        .ok(commoditySpeciesService.getSingleChedppSpecies(commodityCode, speciesId));
  }

  @GetMapping(value = "/chedp/{commodityCode}")
  @Timed("controller.getChedpSpecies.time")
  public ResponseEntity<ChedpSpecies> getChedpSpecies(
      @PathVariable("commodityCode") String commodityCode,
      @RequestParam("speciesName") String speciesName) {

    return ResponseEntity.ok(commoditySpeciesService.getChedpSpecies(commodityCode, speciesName));
  }
}
