package com.tuempresa.gestion.gestion_ordenes_java.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(message = "El código es obligatorio")
    String code,

    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    BigDecimal price,

    String createdBy
) {}
