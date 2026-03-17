package com.tuempresa.gestion.gestion_ordenes_java.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "El usuario es obligatorio")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}
