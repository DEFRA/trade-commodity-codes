package uk.gov.defra.cdp.trade.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.domain.CommodityAttribute;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityAttributeRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityAttributeDto;

@ExtendWith(MockitoExtension.class)
class CommodityAttributeServiceTest {

  private static final String COMMODITY_CODE = "CODE";
  private static final String COMMODITY_CODE_2 = "CODE_2";
  private static final String SOURCE = "source";

  @Mock
  private CommodityAttributeRepository repo;

  @InjectMocks
  private CommodityAttributeService underTest;

  @Test
  void getCommodityAttributes_ReturnsCommodityAttributes_whenDatabaseReturnsFullList() {
    List<String> codes = List.of(COMMODITY_CODE);
    CommodityAttribute commodityAttribute = mock(CommodityAttribute.class);
    List<CommodityAttribute> commodityAttributes = List.of(commodityAttribute);

    when(repo.findAllByCommodityCodeIn(any())).thenReturn(commodityAttributes);
    when(commodityAttribute.getCommodityCode()).thenReturn(COMMODITY_CODE);
    when(commodityAttribute.getPropagation()).thenReturn(SOURCE);

    List<CommodityAttributeDto> commodityAttributeDtos = underTest.getCommodityAttributes(
        codes);

    assertThat(commodityAttributeDtos).isEqualTo(List.of(
        buildCommodityAttribute(COMMODITY_CODE, SOURCE)
    ));

    verify(repo).findAllByCommodityCodeIn(codes);
  }

  @Test
  void getCommodityAttributes_ReturnsNotFoundCodeAttributes_whenDatabaseReturnsEmptyList() {
    List<String> codes = List.of(COMMODITY_CODE);

    when(repo.findAllByCommodityCodeIn(any())).thenReturn(Collections.emptyList());

    List<CommodityAttributeDto> commodityConfigurationDtos = underTest.getCommodityAttributes(
        codes
    );

    assertThat(commodityConfigurationDtos).isEqualTo(List.of(
        buildCommodityAttribute(COMMODITY_CODE, null)
    ));

    verify(repo).findAllByCommodityCodeIn(codes);
  }

  @Test
  void getCommodityAttributes_ReturnsMergedCodeAttributes_whenDatabaseReturnsPartialList() {
    List<String> codes = List.of(COMMODITY_CODE, COMMODITY_CODE_2);
    CommodityAttribute commodityAttribute = mock(CommodityAttribute.class);
    List<CommodityAttribute> commodityAttributes = List.of(commodityAttribute);

    when(repo.findAllByCommodityCodeIn(any())).thenReturn(commodityAttributes);
    when(commodityAttribute.getCommodityCode()).thenReturn(COMMODITY_CODE);
    when(commodityAttribute.getPropagation()).thenReturn(SOURCE);

    List<CommodityAttributeDto> commodityAttributeDtos = underTest.getCommodityAttributes(
        codes);

    assertThat(commodityAttributeDtos).isEqualTo(List.of(
        buildCommodityAttribute(COMMODITY_CODE, SOURCE),
        buildCommodityAttribute(COMMODITY_CODE_2, null)
    ));

    verify(repo).findAllByCommodityCodeIn(codes);
  }

  private CommodityAttributeDto buildCommodityAttribute(String commodityCode, String propagation) {
    return CommodityAttributeDto.builder()
        .commodityCode(commodityCode)
        .propagation(propagation)
        .build();
  }
}
