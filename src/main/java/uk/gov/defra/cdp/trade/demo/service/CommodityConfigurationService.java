package uk.gov.defra.cdp.trade.demo.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.enumerations.ChedType;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityConfigurationRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityConfigurationDto;
import uk.gov.defra.cdp.trade.demo.transformers.CommodityConfigurationDtoTransformer;

@Service
public class CommodityConfigurationService {

  private final CommodityConfigurationRepository repo;

  @Autowired
  public CommodityConfigurationService(CommodityConfigurationRepository repo) {
    this.repo = repo;
  }

  public List<CommodityConfigurationDto> getCommodityConfigurations(
      List<String> codes, ChedType type) {
    List<CommodityConfigurationDto> found = repo.findAllByCommodityCodeInAndType(codes, type)
        .stream()
        .map(CommodityConfigurationDtoTransformer::from)
        .toList();
    List<CommodityConfigurationDto> notFound = generateNotFoundConfigs(found, codes);
    return Stream.of(found, notFound).flatMap(Collection::stream).toList();
  }

  private List<CommodityConfigurationDto> generateNotFoundConfigs(
      List<CommodityConfigurationDto> found, List<String> codes) {
    return codes.stream()
        .filter(code -> found.stream().noneMatch(config -> config.getCommodityCode().equals(code)))
        .map(code -> CommodityConfigurationDto.builder()
            .commodityCode(code)
            .requiresTestAndTrial(false)
            .requiresFinishedOrPropagated(false)
            .build()
        )
        .toList();
  }
}
