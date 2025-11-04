package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.defra.cdp.trade.demo.domain.converter.ChedTypeConverter;
import uk.gov.defra.cdp.trade.demo.domain.enumerations.ChedType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "commodity_configuration")
public class CommodityConfiguration {

  @Id
  private long id;

  @Column(name = "commodity_code")
  private String commodityCode;

  @Convert(converter = ChedTypeConverter.class)
  private ChedType type;

  @Column(name = "requires_test_and_trial")
  private boolean requiresTestAndTrial;

  @Column(name = "requires_finished_or_propagated")
  private boolean requiresFinishedOrPropagated;
}
