package com.tuempresa.gestion.gestion_ordenes_java.repository;

import com.tuempresa.gestion.gestion_ordenes_java.model.Order;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @EntityGraph(attributePaths = "items")
    Optional<Order> findWithItemsById(Long id);

    @Override
    @EntityGraph(attributePaths = "items")
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}
