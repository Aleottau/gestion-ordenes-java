package com.tuempresa.gestion.gestion_ordenes_java.spec;

import com.tuempresa.gestion.gestion_ordenes_java.model.Order;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Order> hasStartDate(LocalDate startDate) {
        return (root, query, cb) ->
            startDate == null ? null : cb.greaterThanOrEqualTo(root.get("orderDate"), startDate);
    }

    public static Specification<Order> hasEndDate(LocalDate endDate) {
        return (root, query, cb) ->
            endDate == null ? null : cb.lessThanOrEqualTo(root.get("orderDate"), endDate);
    }

    public static Specification<Order> hasStatuses(List<String> statuses) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }
            return root.get("status").in(statuses);
        };
    }
}
