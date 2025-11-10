package uk.gov.defra.cdp.trade.demo.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCodeId;
import uk.gov.defra.cdp.trade.demo.domain.CommodityCodeSpecies;

@Repository
public interface CommodityCodeSpeciesRepository
    extends JpaRepository<CommodityCodeSpecies, CommodityCodeId> {

  @Query(
      value =
          "SELECT DISTINCT(species) FROM v_commodity_code_species WHERE cert_type = :certType "
              + "AND species LIKE %:species%",
      nativeQuery = true)
  List<String> findDistinctSpeciesWithCertTypeIgnoringCase(String certType, String species);
}
