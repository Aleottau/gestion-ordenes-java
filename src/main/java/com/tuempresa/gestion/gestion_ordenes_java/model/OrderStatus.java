package com.tuempresa.gestion.gestion_ordenes_java.model;

import java.util.Arrays;

public enum OrderStatus {
    NEW("new"),
    CANCELLED("cancelled"),
    INVOICED("invoiced");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static boolean isValid(String status) {
        return Arrays.stream(values())
            .anyMatch(v -> v.value.equalsIgnoreCase(status));
    }
}
