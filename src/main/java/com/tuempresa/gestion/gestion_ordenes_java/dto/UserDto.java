package com.tuempresa.gestion.gestion_ordenes_java.dto;

import java.time.LocalDateTime;

public record UserDto(
    Long userId,
    String name,
    String username,
    String phone,
    Boolean isActive,
    LocalDateTime createDate,
    LocalDateTime updateDate,
    String updateProgram
) {}
