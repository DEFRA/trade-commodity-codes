package uk.gov.defra.cdp.trade.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CommoditySearchParametersDto {
  private final String eppoCode;
  private final String speciesName;
  private final List<Integer> excludeSpeciesIds;
  private final int pageNumber;
  private final boolean exactMatch;
}
