package com.tuempresa.gestion.gestion_ordenes_java.dto;

import java.util.List;

public record PaginationResponse<T>(
    List<T> data,
    int currentPage,
    int lastPage,
    int perPage,
    long total
) {}
