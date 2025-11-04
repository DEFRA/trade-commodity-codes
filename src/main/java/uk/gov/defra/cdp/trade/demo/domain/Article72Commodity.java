package uk.gov.defra.cdp.trade.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "article_72_commodities")
public class Article72Commodity {

  @Id
  private int id;
  private String commodityCode;
  private String eppoCode;
  private String commodityGroup;
}
