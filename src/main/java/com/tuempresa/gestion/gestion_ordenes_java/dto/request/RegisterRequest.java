package com.tuempresa.gestion.gestion_ordenes_java.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "El usuario es obligatorio")
    String username,

    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotBlank(message = "El teléfono es obligatorio")
    String phone,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password
) {}
