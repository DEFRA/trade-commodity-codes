package uk.gov.defra.cdp.trade.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.cdp.trade.demo.domain.Article72Commodity;
import uk.gov.defra.cdp.trade.demo.domain.repository.Article72Repository;
import uk.gov.defra.cdp.trade.demo.dto.Article72CommodityDto;

@ExtendWith(MockitoExtension.class)
class Article72ServiceTest {

  private static final int ID = 1;
  private static final String COMMODITY_CODE = "100000";
  private static final String EPPO_CODE = "MABDZ";
  private static final String COMMODITY_GROUP = "Fruit and nuts";

  @Mock
  private Article72Repository article72Repository;

  @InjectMocks
  private Article72Service article72Service;

  @Test
  void search_ReturnsCorrectDto_WhenRepositoryFindsAMatchingRecord() {
    // Given
    Article72Commodity article72Commodity = new Article72Commodity(ID, COMMODITY_CODE, EPPO_CODE,
        COMMODITY_GROUP);
    when(article72Repository.findFirstByCommodityCodeAndEppoCodeAndCommodityGroup(
        COMMODITY_CODE, EPPO_CODE, COMMODITY_GROUP)).thenReturn(Optional.of(article72Commodity));

    // When
    Article72CommodityDto actual = article72Service
        .search(COMMODITY_CODE, EPPO_CODE, COMMODITY_GROUP);

    // Then
    Article72CommodityDto expected = Article72CommodityDto.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .commodityGroup(COMMODITY_GROUP)
        .isLowRiskArticle72(true)
        .build();
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void search_ReturnsCorrectDto_WhenRepositoryDoesNotFindAMatchingRecord() {
    // Given
    when(article72Repository.findFirstByCommodityCodeAndEppoCodeAndCommodityGroup(
        COMMODITY_CODE, EPPO_CODE, COMMODITY_GROUP)).thenReturn(Optional.empty());

    // When
    Article72CommodityDto actual = article72Service
        .search(COMMODITY_CODE, EPPO_CODE, COMMODITY_GROUP);

    // Then
    Article72CommodityDto expected = Article72CommodityDto.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .commodityGroup(COMMODITY_GROUP)
        .isLowRiskArticle72(false)
        .build();
    assertThat(actual).isEqualTo(expected);
  }
}
