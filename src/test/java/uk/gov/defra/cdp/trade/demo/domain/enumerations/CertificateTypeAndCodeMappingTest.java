package uk.gov.defra.cdp.trade.demo.domain.enumerations;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import uk.gov.defra.cdp.trade.demo.enumerations.CertificateTypeAndCodeMapping;

class CertificateTypeAndCodeMappingTest {

  @Test
  void getCertCodeByCertType_successful() {
    CertificateTypeAndCodeMapping certificateTypeAndCodeMapping = CertificateTypeAndCodeMapping.CED;
    Character certTypeCode = CertificateTypeAndCodeMapping.getCertCodeByCertType(
        certificateTypeAndCodeMapping.getCertType()).get();

    assertThat(certificateTypeAndCodeMapping.getCertCode()).isEqualTo(certTypeCode);
  }

  @Test
  void getCertCodeByCertType_failsForInvalidCertType() {
    Optional<Character> certTypeCode = CertificateTypeAndCodeMapping.getCertCodeByCertType("INVALID");

    assertThat(certTypeCode).isEmpty();
  }

}
