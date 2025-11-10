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
import uk.gov.defra.cdp.trade.demo.domain.CommodityConfiguration;
import uk.gov.defra.cdp.trade.demo.enumerations.ChedType;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityConfigurationRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityConfigurationDto;

@ExtendWith(MockitoExtension.class)
class CommodityConfigurationServiceTest {

  private static final String COMMODITY_CODE = "CODE";
  private static final String COMMODITY_CODE_2 = "CODE_2";
  private static final ChedType COMMODITY_TYPE = ChedType.CHEDPP;

  @Mock
  private CommodityConfigurationRepository repo;

  @InjectMocks
  private CommodityConfigurationService underTest;

  @Test
  void getCommodityConfigurations_ReturnsCommodityConfigurations_whenDatabaseReturnsFullList() {
    List<String> codes = List.of(COMMODITY_CODE);
    CommodityConfiguration configuration = mock(CommodityConfiguration.class);
    List<CommodityConfiguration> configurations = List.of(configuration);

    when(repo.findAllByCommodityCodeInAndType(any(), any())).thenReturn(configurations);
    when(configuration.getCommodityCode()).thenReturn(COMMODITY_CODE);
    when(configuration.isRequiresTestAndTrial()).thenReturn(true);
    when(configuration.isRequiresFinishedOrPropagated()).thenReturn(true);

    List<CommodityConfigurationDto> configurationDtos = underTest.getCommodityConfigurations(
        codes, COMMODITY_TYPE
    );

    assertThat(configurationDtos).isEqualTo(List.of(
        buildCommodityConfiguration(COMMODITY_CODE, true, true)
    ));

    verify(repo).findAllByCommodityCodeInAndType(codes, COMMODITY_TYPE);
  }

  @Test
  void getCommodityConfigurations_ReturnsNotFoundCommodityConfigurations_whenDatabaseReturnsEmptyList() {
    List<String> codes = List.of(COMMODITY_CODE);

    when(repo.findAllByCommodityCodeInAndType(any(), any())).thenReturn(Collections.emptyList());

    List<CommodityConfigurationDto> configurationDtos = underTest.getCommodityConfigurations(
        codes, COMMODITY_TYPE
    );

    assertThat(configurationDtos).isEqualTo(List.of(
        buildCommodityConfiguration(COMMODITY_CODE, false, false)
    ));

    verify(repo).findAllByCommodityCodeInAndType(codes, COMMODITY_TYPE);
  }

  @Test
  void getCommodityConfigurations_ReturnsMergedCommodityConfigurations_whenDatabaseReturnsPartialList() {
    List<String> codes = List.of(COMMODITY_CODE, COMMODITY_CODE_2);
    CommodityConfiguration configuration = mock(CommodityConfiguration.class);
    List<CommodityConfiguration> configurations = List.of(configuration);

    when(repo.findAllByCommodityCodeInAndType(any(), any())).thenReturn(configurations);
    when(configuration.getCommodityCode()).thenReturn(COMMODITY_CODE);
    when(configuration.isRequiresTestAndTrial()).thenReturn(true);
    when(configuration.isRequiresFinishedOrPropagated()).thenReturn(true);

    List<CommodityConfigurationDto> configurationDtos = underTest.getCommodityConfigurations(
        codes, COMMODITY_TYPE
    );

    assertThat(configurationDtos).isEqualTo(List.of(
        buildCommodityConfiguration(COMMODITY_CODE, true, true),
        buildCommodityConfiguration(COMMODITY_CODE_2, false, false)
    ));

    verify(repo).findAllByCommodityCodeInAndType(codes, COMMODITY_TYPE);
  }

  private CommodityConfigurationDto buildCommodityConfiguration(String commodityCode,
      boolean requiresTestAndTrial, boolean requiresFinishedOrPropagated) {
    return CommodityConfigurationDto.builder()
        .commodityCode(commodityCode)
        .requiresTestAndTrial(requiresTestAndTrial)
        .requiresFinishedOrPropagated(requiresFinishedOrPropagated)
        .build();
  }
}
