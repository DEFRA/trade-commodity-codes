package uk.gov.defra.cdp.trade.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCode;
import uk.gov.defra.cdp.trade.demo.domain.converter.CertTypeConverter;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCodeRepository;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityGroupRepository;
import uk.gov.defra.cdp.trade.demo.dto.CommodityCodeDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupDto;
import uk.gov.defra.cdp.trade.demo.dto.CommodityGroupsDto;
import uk.gov.defra.cdp.trade.demo.transformers.CommodityCodeDtoTransformer;
import uk.gov.defra.cdp.trade.demo.transformers.CommodityGroupDtoTransformer;
import uk.gov.defra.cdp.trade.demo.transformers.CommodityGroupsDtoTransformer;

@Service
public class CommodityCodeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommodityCodeService.class);
  private final CommodityCodeRepository commodityCodeRepository;
  private final CommodityGroupRepository commodityGroupRepository;

  @Autowired
  public CommodityCodeService(CommodityCodeRepository commodityCodeRepository,
      CommodityGroupRepository commodityGroupRepository) {
    this.commodityCodeRepository = commodityCodeRepository;
    this.commodityGroupRepository = commodityGroupRepository;
  }

  public List<CommodityCodeDto> getTopLevel(String certType) {
    return commodityCodeRepository
        .findAllByCertTypeIsLikeAndImmediateParentIsNullOrderByCode(
            CertTypeConverter.convertToDbFormat(certType))
        .stream()
        .map(CommodityCodeDtoTransformer::from)
        .toList();
  }

  public List<CommodityCodeDto> getTopLevel(String certType, String species) {
    return commodityCodeRepository
        .findTopLevelCommoditiesByCertTypeAndSpecies(
            CertTypeConverter.convertToDbFormat(certType), species)
        .stream()
        .map(CommodityCodeDtoTransformer::from)
        .toList();
  }

  public List<CommodityCodeDto> getByParentCode(String certType, String parentCommodityCode) {
    return commodityCodeRepository
        .findAllByCertTypeIsLikeAndImmediateParentIsLikeOrderByCode(
            CertTypeConverter.convertToDbFormat(certType), parentCommodityCode)
        .stream()
        .map(CommodityCodeDtoTransformer::from)
        .toList();
  }

  public List<CommodityCodeDto> getByParentCodeAndSpecies(
      String certType,
      String parentCommodityCode,
      String species) {
    return commodityCodeRepository
        .findChildCommoditiesByCertTypeAndSpecies(CertTypeConverter.convertToDbFormat(certType),
            parentCommodityCode, species)
        .stream()
        .map(CommodityCodeDtoTransformer::from)
        .map(commodityCodeDto -> updateIsCommodity(commodityCodeDto, species))
        .toList();
  }

  public List<CommodityCodeDto> getByCommodityCode(String certType, String commodityCode) {
    return commodityCodeRepository
        .findAllByCertTypeIsLikeAndCodeIsLikeOrderByCode(
            CertTypeConverter.convertToDbFormat(certType), matchOnlyFromStart(commodityCode))
        .stream()
        .map(CommodityCodeDtoTransformer::from)
        .toList();
  }

  public List<CommodityCodeDto> getAllParents(String certType, String initialCommodityCode,
      String species) {
    LOGGER.debug("All parents of commodity code {} for the certificate type {}",
        initialCommodityCode, certType);

    List<CommodityCode> commodityCodes = new ArrayList<>();

    CommodityCode commodityCode =
        commodityCodeRepository.findCommodityCodeByCertTypeIsLikeAndCodeIsOrderByCode(
            CertTypeConverter.convertToDbFormat(certType), initialCommodityCode);
    if (null != commodityCode) {
      commodityCodes.add(commodityCode);

      while (null != commodityCode.getImmediateParent()) {
        CommodityCode parentCommodityCode =
            commodityCodeRepository.findCommodityCodeByCertTypeIsLikeAndCodeIsOrderByCode(
                CertTypeConverter.convertToDbFormat(certType), commodityCode.getImmediateParent());
        commodityCodes.add(parentCommodityCode);
        commodityCode = parentCommodityCode;
      }

      Collections.reverse(commodityCodes);
    }
    if (StringUtils.isNotEmpty(species)) {
      return commodityCodes.stream()
          .map(CommodityCodeDtoTransformer::from)
          .map(commodityCodeDto -> updateIsCommodity(commodityCodeDto, species))
          .toList();
    }
    return commodityCodes.stream()
        .map(CommodityCodeDtoTransformer::from)
        .toList();
  }

  public List<CommodityGroupsDto> getCommodityGroups(List<String> commodityCodes) {
    return commodityGroupRepository
        .findDistinctByCommodityCodeInOrderByCommodityCode(commodityCodes).stream()
        .map(CommodityGroupsDtoTransformer::from)
        .toList();
  }

  private String matchOnlyFromStart(String searchTerm) {
    return searchTerm + "%";
  }

  public List<CommodityGroupDto> getCommodityGroup(List<String> commodityCodes) {
    return commodityGroupRepository
        .findDistinctByCommodityCodeInOrderByCommodityCode(commodityCodes).stream()
        .map(CommodityGroupDtoTransformer::from)
        .toList();
  }

  private CommodityCodeDto updateIsCommodity(CommodityCodeDto commodityCodeDto, String species) {
    List<CommodityCode> commodityCodeList =
        commodityCodeRepository.findAllCommoditiesByCodeCertTypeAndSpecies(
            commodityCodeDto.getCode(), commodityCodeDto.getCertificateType(), species);
    if (commodityCodeList.isEmpty()) {
      commodityCodeDto.setCommodity(false);
    }
    return commodityCodeDto;
  }
}
