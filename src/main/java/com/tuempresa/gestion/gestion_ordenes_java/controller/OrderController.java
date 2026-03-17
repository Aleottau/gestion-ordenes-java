package com.tuempresa.gestion.gestion_ordenes_java.controller;

import com.tuempresa.gestion.gestion_ordenes_java.dto.OrderDto;
import com.tuempresa.gestion.gestion_ordenes_java.dto.OrderFilter;
import com.tuempresa.gestion.gestion_ordenes_java.dto.PaginationResponse;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.CreateOrderRequest;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.UpdateOrderStatusRequest;
import com.tuempresa.gestion.gestion_ordenes_java.service.OrderService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public PaginationResponse<OrderDto> list(
        @RequestParam(value = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "sort_by", defaultValue = "order_date") String sortBy,
        @RequestParam(value = "sort_dir", defaultValue = "desc") String sortDir,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "per_page", defaultValue = "10") int perPage
    ) {
        List<String> statuses = parseStatuses(status);
        OrderFilter filter = new OrderFilter(
            startDate,
            endDate,
            statuses,
            sortBy,
            sortDir,
            page,
            perPage
        );
        return orderService.getOrders(filter);
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = orderService.createOrder(request);
        return ResponseEntity.status(201).body(order);
    }

    @PutMapping("/{id}")
    public Map<String, OrderDto> updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        OrderDto order = orderService.updateStatus(id, request);
        return Map.of("order", order);
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        byte[] pdf = orderService.generateInvoice(id);
        String filename = "orden_" + String.format("%04d", id) + ".pdf";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    private List<String> parseStatuses(String status) {
        if (status == null || status.isBlank()) {
            return Collections.emptyList();
        }
        return Stream.of(status.split(","))
            .map(String::trim)
            .map(String::toLowerCase)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }
}
