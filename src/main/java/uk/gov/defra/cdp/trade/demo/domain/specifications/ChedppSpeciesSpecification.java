package uk.gov.defra.cdp.trade.demo.domain.specifications;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.defra.cdp.trade.demo.domain.ChedppSpecies;

public class ChedppSpeciesSpecification {

  private static final String COMMODITY_CODE = "commodityCode";
  private static final String SPECIES_ID = "speciesId";
  private static final String EPPO_CODE = "eppoCode";
  private static final String SPECIES_NAME = "speciesName";

  private ChedppSpeciesSpecification() {
    // Private to prevent instantiation
  }

  public static Specification<ChedppSpecies> buildChedppSpeciesSpecification(
      final String commodityCode,
      final String eppoCode,
      final String speciesName,
      final List<Integer> excludeSpeciesIds,
      boolean exactMatch) {

    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(criteriaBuilder.equal(root.get(COMMODITY_CODE), commodityCode));

      if (eppoCode != null) {
        if (exactMatch) {
          predicates.add(criteriaBuilder.equal(root.get(EPPO_CODE), eppoCode));

        } else {
          predicates.add(criteriaBuilder.like(root.get(EPPO_CODE), "%" + eppoCode + "%"));
        }
      }

      if (speciesName != null) {
        if (exactMatch) {
          predicates.add(criteriaBuilder.equal(root.get(SPECIES_NAME), speciesName));
        } else {
          predicates.add(criteriaBuilder.like(root.get(SPECIES_NAME), "%" + speciesName + "%"));
        }
      }

      if (excludeSpeciesIds != null && !excludeSpeciesIds.isEmpty()) {
        predicates.add(criteriaBuilder.not(root.get(SPECIES_ID).in(excludeSpeciesIds)));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    };
  }
}
