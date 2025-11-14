package uk.gov.defra.cdp.trade.demo.enumerations;

import java.util.Arrays;
import java.util.Optional;

public enum CertificateTypeAndCodeMapping {

  CVED_A("CVED-A", 'A'),
  CVED_P("CVED-P", 'P'),
  CED("CED", 'D');

  private String certType;
  private Character certCode;

  CertificateTypeAndCodeMapping(String certType, Character certCode) {
    this.certType = certType;
    this.certCode = certCode;
  }

  public String getCertType() {
    return certType;
  }

  public Character getCertCode() {
    return certCode;
  }

  public static Optional<Character> getCertCodeByCertType(String certType) {
    return Arrays.stream(CertificateTypeAndCodeMapping.values())
        .filter(certTypeAndCodeMapping -> certTypeAndCodeMapping.getCertType().equals(certType))
        .map(CertificateTypeAndCodeMapping::getCertCode).findFirst();
  }
}
