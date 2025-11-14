package uk.gov.defra.cdp.trade.demo.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.enumerations.CertificateTypeAndCodeMapping;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCodeRepository;

@Service
public class CertificateTypeService {

  private final CommodityCodeRepository commodityCodeRepository;

  @Autowired
  public CertificateTypeService(CommodityCodeRepository commodityCodeRepository) {
    this.commodityCodeRepository = commodityCodeRepository;
  }

  public Map<String, List<Character>> getAllCertTypes(List<String> commodityCodes) {
    List<CommodityCode> commodityCodesReturned = commodityCodeRepository
        .findCommodityCodeByCodeIn(commodityCodes);
    return
        commodityCodesReturned.stream()
        .map(CertificateTypeService::mapCommodityCodeToPair)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(groupingBy(Pair::getLeft, mapping(Pair::getRight, toList())));
  }

  private static Optional<Pair<String, Character>> mapCommodityCodeToPair(
      CommodityCode commodityCode) {
    Optional<Character> certTypeCode = getCertTypeCode(commodityCode.getCertType());
    return certTypeCode.map(character -> Pair.of(commodityCode.getCode(), character));
  }

  private static Optional<Character> getCertTypeCode(String certType) {
    return CertificateTypeAndCodeMapping.getCertCodeByCertType(certType);
  }

}
