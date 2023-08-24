package uk.ac.ic.doc.blocc.dashboard.transaction.specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.SensorChaincodeTransaction;

public class SensorChaincodeTransactionSpecification {

  public static Specification<SensorChaincodeTransaction> filterByParameters(Integer containerNum,
      Long sinceTimestamp, Long untilTimestamp) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (containerNum != null) {
        predicates.add(criteriaBuilder.equal(root.get("key").get("containerNum"), containerNum));
      }
      if (sinceTimestamp != null) {
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(root.get("createdTimestamp"), sinceTimestamp));
      }
      if (untilTimestamp != null) {
        predicates.add(
            criteriaBuilder.lessThanOrEqualTo(root.get("createdTimestamp"), untilTimestamp));
      }

      query.orderBy(criteriaBuilder.asc(root.get("createdTimestamp")));

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
