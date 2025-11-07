package uk.gov.defra.cdp.trade.demo.config.tls;

import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Component
@Slf4j
public class CertificateLoader {

    private final String cdpCertificate;
    private final String rdsCertificate;

    public CertificateLoader(
            @Value("${cdp.certificate}") String cdpCertificate,
            @Value("${aws.rds.certificate:}") String rdsCertificate) {
        this.cdpCertificate = cdpCertificate;
        this.rdsCertificate = rdsCertificate;
    }
    
    public X509Certificate loadCdpCertificate() {
        return loadCertificate(cdpCertificate);
    }

    public X509Certificate loadRdsCertificate() {
        return loadCertificate(rdsCertificate);
    }

    private X509Certificate loadCertificate(String certificate) {
      X509Certificate cert = null;
      if (certificate == null || certificate.isEmpty()) {
        log.info("No custom certificates to load");
        return cert;
      }

      byte[] certData = Base64.getDecoder().decode(certificate);

      CertificateFactory cf;
      try {
        cf = CertificateFactory.getInstance("X.509");
      } catch (CertificateException e) {
        log.error("Failed to get X.509 CertificateFactory: {}", e.getMessage());
        throw new IllegalStateException("Cannot initialize certificate factory", e);
      }

      try {
        ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
        cert = (X509Certificate) cf.generateCertificate(certStream);

        log.info("Successfully loaded certificate: (Subject: {})",
            cert.getSubjectX500Principal().getName());

      } catch (CertificateException e) {
        log.error("Failed to parse certificate: {}. Skipping.", e.getMessage());
      }

      return cert;
    }

}
