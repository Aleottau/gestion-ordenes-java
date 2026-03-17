package com.tuempresa.gestion.gestion_ordenes_java.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusRequest(
    @NotBlank(message = "El estado es obligatorio")
    String status
) {}
