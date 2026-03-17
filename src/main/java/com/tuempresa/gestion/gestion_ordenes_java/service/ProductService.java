package com.tuempresa.gestion.gestion_ordenes_java.service;

import com.tuempresa.gestion.gestion_ordenes_java.dto.PaginationResponse;
import com.tuempresa.gestion.gestion_ordenes_java.dto.ProductDto;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.ProductRequest;
import com.tuempresa.gestion.gestion_ordenes_java.exception.ResourceNotFoundException;
import com.tuempresa.gestion.gestion_ordenes_java.exception.ValidationException;
import com.tuempresa.gestion.gestion_ordenes_java.mapper.ProductMapper;
import com.tuempresa.gestion.gestion_ordenes_java.model.Product;
import com.tuempresa.gestion.gestion_ordenes_java.repository.ProductRepository;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public PaginationResponse<ProductDto> getProducts(String search, int page, int perPage) {
        int safePage = Math.max(page - 1, 0);
        int safePerPage = perPage <= 0 ? 10 : perPage;
        Pageable pageable = PageRequest.of(safePage, safePerPage);
        String term = (search == null || search.isBlank()) ? null : search.trim();
        Page<Product> result = productRepository.search(term, pageable);

        return new PaginationResponse<>(
            result.getContent().stream().map(ProductMapper::toDto).toList(),
            result.getNumber() + 1,
            Math.max(result.getTotalPages(), 1),
            safePerPage,
            result.getTotalElements()
        );
    }

    public ProductDto create(ProductRequest request) {
        validateUniqueCode(request.code(), null);
        Product product = new Product();
        product.setCode(request.code().trim());
        product.setName(request.name().trim());
        product.setPrice(request.price());
        product.setCreatedBy(request.createdBy());
        Product saved = productRepository.save(product);
        return ProductMapper.toDto(saved);
    }

    public ProductDto update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        validateUniqueCode(request.code(), id);
        product.setCode(request.code().trim());
        product.setName(request.name().trim());
        product.setPrice(request.price());
        product.setCreatedBy(request.createdBy());
        return ProductMapper.toDto(product);
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        productRepository.delete(product);
    }

    private void validateUniqueCode(String code, Long productId) {
        boolean exists = productId == null
            ? productRepository.existsByCodeIgnoreCase(code)
            : productRepository.existsByCodeIgnoreCaseAndIdNot(code, productId);

        if (exists) {
            throw new ValidationException(
                "Errores de validación",
                Map.of("code", "El código ya está registrado")
            );
        }
    }
}
