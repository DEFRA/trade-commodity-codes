package uk.gov.defra.cdp.trade.demo.resource;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.service.CommodityCategoryService;

@ExtendWith(MockitoExtension.class)
class CommodityCategoryResourceTest {

  private static final String CERTIFICATE_TYPE_CVEDP = "cvedp";
  private static final String COMMODITY_CODE = "0123456";

  @Mock
  CommodityCategoryService commodityCategoryService;

  CommodityCategoryResource commodityCategoryResource;

  @BeforeEach
  void setUp() {
    commodityCategoryResource = new CommodityCategoryResource(commodityCategoryService);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("prepareCertTypesStrings")
  void testGetWithCertTypeVariation(String certType) {
    // Given, When
    commodityCategoryResource.get(certType, COMMODITY_CODE);
    //Then
    verify(commodityCategoryService, times(1)).get(CERTIFICATE_TYPE_CVEDP, COMMODITY_CODE);
  }

  static Stream<Arguments> prepareCertTypesStrings() {
    return Stream.of(
        arguments(
            named("LowercaseCertificateTypeAndCommodityCodeCallsServiceGetWithCorrectParameters",
                "cvedp")),
        arguments(
            named("UppercaseCertificateTypeAndCommodityCodeCallsServiceGetWithCorrectParameters",
                "CVEDP")),
        arguments(named("DashedCertificateTypeAndCommodityCodeCallsServiceGetWithCorrectParameters",
            "cved-p")));
  }
}
