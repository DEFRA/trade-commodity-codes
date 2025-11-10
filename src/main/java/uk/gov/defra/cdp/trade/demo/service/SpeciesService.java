package uk.gov.defra.cdp.trade.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.cdp.trade.demo.domain.converter.CertTypeConverter;
import uk.gov.defra.cdp.trade.demo.domain.repository.CommodityCodeSpeciesRepository;

@Service
public class SpeciesService {

  private final CommodityCodeSpeciesRepository commodityCodeSpeciesRepository;

  @Autowired
  public SpeciesService(
      CommodityCodeSpeciesRepository commodityCodeSpeciesRepository) {
    this.commodityCodeSpeciesRepository = commodityCodeSpeciesRepository;
  }

  public List<String> getCommoditySpecies(String certType, String species) {
    return commodityCodeSpeciesRepository.findDistinctSpeciesWithCertTypeIgnoringCase(
        CertTypeConverter.convertToDbFormat(certType), species);
  }
}
