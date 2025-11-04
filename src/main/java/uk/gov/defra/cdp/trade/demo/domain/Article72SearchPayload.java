package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Article72SearchPayload {

  @NotBlank
  private String commodityCode;

  @NotBlank
  private String eppoCode;

  @NotBlank
  private String commodityGroup;
}
