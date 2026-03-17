package com.tuempresa.gestion.gestion_ordenes_java.repository;

import com.tuempresa.gestion.gestion_ordenes_java.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByUsernameIgnoreCaseAndIsActiveTrue(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
