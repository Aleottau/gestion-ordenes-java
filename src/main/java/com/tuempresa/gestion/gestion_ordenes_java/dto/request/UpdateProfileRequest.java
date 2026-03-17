package com.tuempresa.gestion.gestion_ordenes_java.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotBlank(message = "El teléfono es obligatorio")
    String phone
) {}
