package uk.gov.defra.cdp.trade.demo.resource;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.cdp.trade.demo.service.CertificateTypeService;

@RestController
@RequestMapping("/certificate-type")
public class CertificateTypeResource {

  private final CertificateTypeService certificateTypeService;

  @Autowired
  public CertificateTypeResource(CertificateTypeService certificateTypeService) {
    this.certificateTypeService = certificateTypeService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, List<Character>>> getAllCertTypes(
      @RequestParam(value = "commodityCodes") List<String> commodityCodes) {
    return ResponseEntity.ok(certificateTypeService.getAllCertTypes(commodityCodes));
  }

}
