package uk.gov.defra.cdp.trade.demo.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.defra.cdp.trade.demo.exceptions.FilterTooShortException;
import uk.gov.defra.cdp.trade.demo.service.SpeciesService;

@ExtendWith(MockitoExtension.class)
class SpeciesResourceTest {

  private static final String CVED_P = "cved-p";
  private static final String SEARCH_STRING_1 = "mal";
  private static final String SEARCH_STRING_2 = "zzz";
  private static final String INVALID_SEARCH_STRING = "a";

  @Mock
  SpeciesService speciesService;

  @InjectMocks
  SpeciesResource speciesResource;

  @Test
  void getSpecies_ReturnsCorrectResponse_WhenServiceReturnsData() {
    List<String> speciesNames = List.of("Merlangius merlangus");
    when(speciesService.getCommoditySpecies(CVED_P, SEARCH_STRING_1))
        .thenReturn(speciesNames);

    var response = speciesResource.getSpecies(CVED_P, SEARCH_STRING_1);

    verify(speciesService).getCommoditySpecies(CVED_P, SEARCH_STRING_1);
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(speciesNames);
  }

  @Test
  void getSpecies_ReturnsEmptyList_WhenNoSpeciesFound() {
    when(speciesService.getCommoditySpecies(CVED_P, SEARCH_STRING_2))
        .thenReturn(List.of());

    var response = speciesResource.getSpecies(CVED_P, SEARCH_STRING_2);

    verify(speciesService).getCommoditySpecies(CVED_P, SEARCH_STRING_2);
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getBody()).isEqualTo(List.of());
  }

  @Test
  void getSpecies_ThrowsFilterTooShortException_WhenFilterIsLessThanThreeCharacters() {
    assertThatThrownBy(() -> speciesResource.getSpecies(CVED_P, INVALID_SEARCH_STRING))
        .isInstanceOf(FilterTooShortException.class);
  }
}
