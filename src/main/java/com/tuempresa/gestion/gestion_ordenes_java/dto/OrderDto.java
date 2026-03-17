package com.tuempresa.gestion.gestion_ordenes_java.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrderDto(
    Long id,
    String customerName,
    LocalDate orderDate,
    String status,
    String notes,
    Integer totalItems,
    BigDecimal totalAmount,
    String createdBy,
    Instant createdAt,
    Instant updatedAt,
    List<OrderItemDto> items
) {}
