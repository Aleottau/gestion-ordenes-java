package com.tuempresa.gestion.gestion_ordenes_java.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CreateOrderRequest(
    @NotBlank(message = "El cliente es obligatorio")
    String customerName,

    @NotNull(message = "La fecha es obligatoria")
    LocalDate orderDate,

    String notes,

    @NotEmpty(message = "Debes agregar al menos un ítem")
    List<@Valid OrderItemRequest> items
) {}
