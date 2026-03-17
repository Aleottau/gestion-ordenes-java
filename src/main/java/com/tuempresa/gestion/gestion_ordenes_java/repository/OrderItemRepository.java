package com.tuempresa.gestion.gestion_ordenes_java.repository;

import com.tuempresa.gestion.gestion_ordenes_java.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
