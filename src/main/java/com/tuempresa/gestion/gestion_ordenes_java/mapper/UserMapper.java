package com.tuempresa.gestion.gestion_ordenes_java.mapper;

import com.tuempresa.gestion.gestion_ordenes_java.dto.UserDto;
import com.tuempresa.gestion.gestion_ordenes_java.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        return new UserDto(
            user.getUserId(),
            user.getName(),
            user.getUsername(),
            user.getPhone(),
            user.getIsActive(),
            user.getCreateDate(),
            user.getUpdateDate(),
            user.getUpdateProgram()
        );
    }
}
