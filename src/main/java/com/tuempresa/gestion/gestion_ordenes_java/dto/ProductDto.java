package com.tuempresa.gestion.gestion_ordenes_java.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto(
    Long id,
    String code,
    String name,
    BigDecimal price,
    String createdBy,
    Instant createdAt,
    Instant updatedAt
) {}
