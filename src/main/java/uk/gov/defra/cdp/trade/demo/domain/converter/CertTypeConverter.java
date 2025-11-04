package uk.gov.defra.cdp.trade.demo.domain.converter;

public class CertTypeConverter {

  private CertTypeConverter() {}

  public static String convertToDbFormat(String certType) {
    String certTypeUpperCase = certType.toUpperCase();
    switch (certTypeUpperCase) {
      case "CVEDA":
        certTypeUpperCase = "CVED-A";
        break;
      case "CVEDP":
        certTypeUpperCase = "CVED-P";
        break;
      case "CHEDPP":
        certTypeUpperCase = "CHED-PP";
        break;
      default: // CED
        break;
    }
    return certTypeUpperCase;
  }
}
