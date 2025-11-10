package uk.gov.defra.cdp.trade.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCategory;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCategoryRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCategoryDto;

@ExtendWith(MockitoExtension.class)
class CommodityCategoryServiceTest {

  private static final String CVEDA = "cveda";
  private static final String COMMODITY_CODE = "01";

  @Mock
  CommodityCategoryRepository commodityCategoryRepository;

  private CommodityCategoryService commodityCategoryService;

  @BeforeEach
  void setUp() {
    commodityCategoryService = new CommodityCategoryService(commodityCategoryRepository);
  }

  @Test
  void shouldGetTheCommodityCategoryFromTheRepository() {
    // Given
    String data = "someData";
    CommodityCategory commodityCategory = new CommodityCategory(CVEDA, COMMODITY_CODE, data);

    when(commodityCategoryRepository.findByCertificateTypeAndCommodityCode(CVEDA, COMMODITY_CODE))
        .thenReturn(Optional.of(commodityCategory));

    // When
    CommodityCategoryDto result = commodityCategoryService.get(CVEDA, COMMODITY_CODE).get();

    // Then
    assertThat(result.getCertificateType()).isEqualTo(CVEDA);
    assertThat(result.getCommodityCode()).isEqualTo(COMMODITY_CODE);
    assertThat(result.getData()).isEqualTo(data);
  }
}
