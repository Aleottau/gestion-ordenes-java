package com.tuempresa.gestion.gestion_ordenes_java.mapper;

import com.tuempresa.gestion.gestion_ordenes_java.dto.OrderDto;
import com.tuempresa.gestion.gestion_ordenes_java.dto.OrderItemDto;
import com.tuempresa.gestion.gestion_ordenes_java.model.Order;
import com.tuempresa.gestion.gestion_ordenes_java.model.OrderItem;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDto toDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
            .map(OrderMapper::toItemDto)
            .toList();

        return new OrderDto(
            order.getId(),
            order.getCustomerName(),
            order.getOrderDate(),
            order.getStatus(),
            order.getNotes(),
            order.getTotalItems(),
            order.getTotalAmount(),
            order.getCreatedBy(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            items
        );
    }

    private static OrderItemDto toItemDto(OrderItem item) {
        return new OrderItemDto(
            item.getId(),
            item.getProduct() != null ? item.getProduct().getId() : null,
            item.getProductCode(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getLineTotal()
        );
    }
}
