package com.tuempresa.gestion.gestion_ordenes_java.repository;

import com.tuempresa.gestion.gestion_ordenes_java.model.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    Optional<Product> findByCodeIgnoreCase(String code);

    @Query(
        value = """
            SELECT * FROM products
            WHERE (:term IS NULL OR name ILIKE CONCAT('%', :term, '%') OR code ILIKE CONCAT('%', :term, '%'))
            ORDER BY created_at DESC
            """,
        countQuery = """
            SELECT COUNT(*) FROM products
            WHERE (:term IS NULL OR name ILIKE CONCAT('%', :term, '%') OR code ILIKE CONCAT('%', :term, '%'))
            """,
        nativeQuery = true
    )
    Page<Product> search(@Param("term") String term, Pageable pageable);
}
