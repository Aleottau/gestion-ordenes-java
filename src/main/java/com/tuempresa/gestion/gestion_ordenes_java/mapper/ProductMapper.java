package com.tuempresa.gestion.gestion_ordenes_java.mapper;

import com.tuempresa.gestion.gestion_ordenes_java.dto.ProductDto;
import com.tuempresa.gestion.gestion_ordenes_java.model.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductDto toDto(Product product) {
        return new ProductDto(
            product.getId(),
            product.getCode(),
            product.getName(),
            product.getPrice(),
            product.getCreatedBy(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
