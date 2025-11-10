package uk.gov.defra.cdp.trade.demo.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.cdp.trade.demo.domain.Article72SearchPayload;
import uk.gov.defra.cdp.trade.demo.dto.Article72CommodityDto;
import uk.gov.defra.cdp.trade.demo.service.Article72Service;

@ExtendWith(MockitoExtension.class)
class Article72ResourceTest {

  private static final String COMMODITY_CODE = "100000";
  private static final String EPPO_CODE = "MABDZ";
  private static final String COMMODITY_GROUP = "Fruit and nuts";
  private static final boolean IS_ARTICLE_72 = true;

  @Mock
  private Article72Service article72Service;

  @InjectMocks
  private Article72Resource article72Resource;

  @Test
  void search_ReturnsResponseEntityWithArticle72CommodityDto_WhenCalled() {
    // Given
    Article72SearchPayload searchPayload = new Article72SearchPayload(COMMODITY_CODE, EPPO_CODE,
        COMMODITY_GROUP);

    Article72CommodityDto article72CommodityDto = Article72CommodityDto.builder()
        .commodityCode(COMMODITY_CODE)
        .eppoCode(EPPO_CODE)
        .commodityGroup(COMMODITY_GROUP)
        .isLowRiskArticle72(IS_ARTICLE_72)
        .build();

    when(article72Service.search(COMMODITY_CODE, EPPO_CODE, COMMODITY_GROUP))
        .thenReturn(article72CommodityDto);

    // When
    ResponseEntity<Article72CommodityDto> actual = article72Resource.search(searchPayload);

    // Then
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actual.getBody()).isEqualTo(article72CommodityDto);
  }
}
