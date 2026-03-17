package com.tuempresa.gestion.gestion_ordenes_java.dto;

import java.time.LocalDate;
import java.util.List;

public record OrderFilter(
    LocalDate startDate,
    LocalDate endDate,
    List<String> statuses,
    String sortBy,
    String sortDir,
    int page,
    int perPage
) {}
