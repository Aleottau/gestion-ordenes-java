package com.tuempresa.gestion.gestion_ordenes_java.controller;

import com.tuempresa.gestion.gestion_ordenes_java.dto.UserDto;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.UpdateProfileRequest;
import com.tuempresa.gestion.gestion_ordenes_java.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto show(@PathVariable Long id) {
        return userService.getProfile(id);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(id, request);
    }
}
