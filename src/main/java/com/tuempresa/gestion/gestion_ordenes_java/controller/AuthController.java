package com.tuempresa.gestion.gestion_ordenes_java.controller;

import com.tuempresa.gestion.gestion_ordenes_java.dto.UserDto;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.LoginRequest;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.RegisterRequest;
import com.tuempresa.gestion.gestion_ordenes_java.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        UserDto user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "message", "Usuario registrado correctamente",
                "user", user
            ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        UserDto user = userService.login(request);
        return ResponseEntity.ok(Map.of(
            "message", "Login correcto",
            "user", user
        ));
    }
}
