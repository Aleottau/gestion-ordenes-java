package com.tuempresa.gestion.gestion_ordenes_java.dto;

import java.math.BigDecimal;

public record OrderItemDto(
    Long id,
    Long productId,
    String productCode,
    String productName,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal
) {}
