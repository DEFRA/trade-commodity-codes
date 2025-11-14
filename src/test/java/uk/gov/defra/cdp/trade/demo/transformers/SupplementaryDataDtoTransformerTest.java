package uk.gov.defra.cdp.trade.demo.transformers;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.defra.cdp.trade.demo.domain.IntendedUseRegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.domain.SupplementaryData;
import uk.gov.defra.cdp.trade.demo.dto.SupplementaryDataDto;
import uk.gov.defra.cdp.trade.demo.enumerations.RegulatoryAuthority;
import uk.gov.defra.cdp.trade.demo.fixtures.SupplementaryDataFixtures;

@RunWith(MockitoJUnitRunner.class)
class SupplementaryDataDtoTransformerTest {

  @Test
  void from_ReturnsCorrectDto() {
    // Given
    SupplementaryData supplementaryData = SupplementaryDataFixtures.supplementaryData();
    SupplementaryDataDto supplementaryDataDto = SupplementaryDataFixtures.supplementaryDataDto();
    supplementaryDataDto.setVarieties(null);
    supplementaryDataDto.setClasses(null);
    supplementaryDataDto.setIntendedUseRegulatoryAuthorities(Collections.singletonList(
        IntendedUseRegulatoryAuthority.builder()
            .intendedUse("")
            .regulatoryAuthority(RegulatoryAuthority.PHSI)
            .build()));
    // When
    SupplementaryDataDto result = SupplementaryDataDtoTransformer.from(supplementaryData);

    // Then
    assertThat(result).isEqualTo(supplementaryDataDto);
  }

  @Test
  void from_SetsIntendedUse() {
    // Given
    SupplementaryData supplementaryData = SupplementaryDataFixtures.supplementaryData();
    SupplementaryDataDto supplementaryDataDto = SupplementaryDataFixtures.supplementaryDataDto();
    supplementaryDataDto.setVarieties(null);
    supplementaryDataDto.setClasses(null);
    supplementaryDataDto.setIntendedUseRegulatoryAuthorities(Collections.singletonList(
        IntendedUseRegulatoryAuthority.builder()
            .intendedUse("")
            .regulatoryAuthority(RegulatoryAuthority.PHSI)
            .build()));

    // When
    SupplementaryDataDto result = SupplementaryDataDtoTransformer.from(supplementaryData);

    // Then
    assertThat(result).isEqualTo(supplementaryDataDto);
  }
}
