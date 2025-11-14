package uk.gov.defra.cdp.trade.demo.resource;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.cdp.trade.demo.exceptions.FilterTooShortException;
import uk.gov.defra.cdp.trade.demo.service.SpeciesService;

@RestController
@RequestMapping("/species")
public class SpeciesResource {

  private final SpeciesService speciesService;

  @Autowired
  public SpeciesResource(SpeciesService speciesService) {
    this.speciesService = speciesService;
  }

  @GetMapping(value = "/{certType}")
  public ResponseEntity<List<String>> getSpecies(
      @PathVariable("certType") String certType, @RequestParam("filter") String filter) {

    if (filter.length() < 3) {
      throw new FilterTooShortException("Species filter must be a minimum of 3 characters");
    }

    return ResponseEntity.ok(speciesService.getCommoditySpecies(certType, filter));
  }
}
