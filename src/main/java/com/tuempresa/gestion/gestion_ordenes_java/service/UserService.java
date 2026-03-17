package com.tuempresa.gestion.gestion_ordenes_java.service;

import com.tuempresa.gestion.gestion_ordenes_java.dto.UserDto;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.LoginRequest;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.RegisterRequest;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.UpdateProfileRequest;
import com.tuempresa.gestion.gestion_ordenes_java.exception.BadRequestException;
import com.tuempresa.gestion.gestion_ordenes_java.exception.ResourceNotFoundException;
import com.tuempresa.gestion.gestion_ordenes_java.exception.ValidationException;
import com.tuempresa.gestion.gestion_ordenes_java.mapper.UserMapper;
import com.tuempresa.gestion.gestion_ordenes_java.model.User;
import com.tuempresa.gestion.gestion_ordenes_java.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new ValidationException(
                "Errores de validación",
                Map.of("username", "Este usuario ya existe")
            );
        }

        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(request.username().trim());
        user.setName(request.name().trim());
        user.setPhone(request.phone().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setIsActive(true);
        user.setCreateDate(now);
        user.setUpdateDate(now);
        user.setUpdateProgram("register_form");

        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    public UserDto login(LoginRequest request) {
        String username = request.email().trim();
        User user = userRepository.findByUsernameIgnoreCaseAndIsActiveTrue(username)
            .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        return UserMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getProfile(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return UserMapper.toDto(user);
    }

    public UserDto updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        user.setName(request.name().trim());
        user.setPhone(request.phone().trim());
        user.setUpdateDate(LocalDateTime.now());
        user.setUpdateProgram("profile_update");

        return UserMapper.toDto(user);
    }
}
