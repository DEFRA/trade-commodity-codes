package uk.gov.defra.cdp.trade.demo.service;

import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityAttributeRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityAttributeDto;
import uk.gov.defra.cdp.trade.demo.transformers.CommodityAttributeDtoTransformer;

@Service
public class CommodityAttributeService {

  private final CommodityAttributeRepository repo;

  @Autowired
  public CommodityAttributeService(CommodityAttributeRepository repo) {
    this.repo = repo;
  }

  public List<CommodityAttributeDto> getCommodityAttributes(List<String> commodityCodes) {
    List<CommodityAttributeDto> found = repo.findAllByCommodityCodeIn(commodityCodes)
        .stream()
        .map(CommodityAttributeDtoTransformer::from)
        .collect(Collectors.toCollection(ArrayList::new));
    found.addAll(generateNotFoundAttributes(found, commodityCodes));
    return Collections.unmodifiableList(found);
  }

  private List<CommodityAttributeDto> generateNotFoundAttributes(
          List<CommodityAttributeDto> found, List<String> commodityCodes) {
    Set<String> foundCodes = found.stream()
        .map(CommodityAttributeDto::getCommodityCode)
        .collect(Collectors.toSet());
    return commodityCodes.stream()
        .filter(not(foundCodes::contains))
        .map(commodityCode -> CommodityAttributeDto.builder()
            .commodityCode(commodityCode)
            .build()
        )
        .toList();
  }
}
