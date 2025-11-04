package uk.gov.defra.cdp.trade.demo.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchVariety {
  private String eppoCode;
  private String variety;
}