package com.tuempresa.gestion.gestion_ordenes_java.service;

import com.tuempresa.gestion.gestion_ordenes_java.dto.OrderDto;
import com.tuempresa.gestion.gestion_ordenes_java.dto.OrderFilter;
import com.tuempresa.gestion.gestion_ordenes_java.dto.PaginationResponse;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.CreateOrderRequest;
import com.tuempresa.gestion.gestion_ordenes_java.dto.request.UpdateOrderStatusRequest;
import com.tuempresa.gestion.gestion_ordenes_java.exception.BadRequestException;
import com.tuempresa.gestion.gestion_ordenes_java.exception.ResourceNotFoundException;
import com.tuempresa.gestion.gestion_ordenes_java.exception.ValidationException;
import com.tuempresa.gestion.gestion_ordenes_java.mapper.OrderMapper;
import com.tuempresa.gestion.gestion_ordenes_java.model.Order;
import com.tuempresa.gestion.gestion_ordenes_java.model.OrderItem;
import com.tuempresa.gestion.gestion_ordenes_java.model.OrderStatus;
import com.tuempresa.gestion.gestion_ordenes_java.model.Product;
import com.tuempresa.gestion.gestion_ordenes_java.repository.OrderRepository;
import com.tuempresa.gestion.gestion_ordenes_java.repository.ProductRepository;
import com.tuempresa.gestion.gestion_ordenes_java.spec.OrderSpecifications;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private static final Map<String, String> SORTABLE_COLUMNS = Map.of(
        "customer_name", "customerName",
        "total_amount", "totalAmount",
        "id", "id",
        "order_date", "orderDate"
    );

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderTicketPdfService ticketPdfService;

    public OrderService(
        OrderRepository orderRepository,
        ProductRepository productRepository,
        OrderTicketPdfService ticketPdfService
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.ticketPdfService = ticketPdfService;
    }

    @Transactional(readOnly = true)
    public PaginationResponse<OrderDto> getOrders(OrderFilter filter) {
        Specification<Order> spec = Specification
            .where(OrderSpecifications.hasStartDate(filter.startDate()))
            .and(OrderSpecifications.hasEndDate(filter.endDate()))
            .and(OrderSpecifications.hasStatuses(filter.statuses()));

        int safePage = Math.max(filter.page() - 1, 0);
        int safePerPage = filter.perPage() <= 0 ? 10 : filter.perPage();

        String sortBy = mapSortColumn(filter.sortBy());
        Sort.Direction direction = "asc".equalsIgnoreCase(filter.sortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(safePage, safePerPage, Sort.by(direction, sortBy));

        Page<Order> result = orderRepository.findAll(spec, pageable);

        return new PaginationResponse<>(
            result.getContent().stream().map(OrderMapper::toDto).toList(),
            result.getNumber() + 1,
            Math.max(result.getTotalPages(), 1),
            safePerPage,
            result.getTotalElements()
        );
    }

    public OrderDto createOrder(CreateOrderRequest request) {
        Map<Long, Product> products = fetchProducts(request);

        Order order = new Order();
        order.setCustomerName(request.customerName().trim());
        order.setOrderDate(request.orderDate());
        order.setStatus(OrderStatus.NEW.value());
        String notes = request.notes();
        order.setNotes(notes == null || notes.isBlank() ? null : notes.trim());
        order.setCreatedBy("system");

        int totalItems = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {
            Product product = products.get(itemRequest.productId());
            if (product == null) {
                throw new ResourceNotFoundException("Producto no encontrado");
            }

            int quantity = itemRequest.quantity();
            BigDecimal unitPrice = product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductCode(product.getCode());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setLineTotal(lineTotal);

            order.addItem(orderItem);

            totalItems += quantity;
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalItems(totalItems);
        order.setTotalAmount(totalAmount);

        Order saved = orderRepository.save(order);
        return OrderMapper.toDto(saved);
    }

    public OrderDto updateStatus(Long id, UpdateOrderStatusRequest request) {
        String status = request.status().toLowerCase();
        if (!OrderStatus.isValid(status)) {
            throw new ValidationException(
                "Errores de validación",
                Map.of("status", "Estado inválido")
            );
        }
        Order order = orderRepository.findWithItemsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));
        order.setStatus(status);
        return OrderMapper.toDto(order);
    }

    public byte[] generateInvoice(Long id) {
        Order order = orderRepository.findWithItemsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (!OrderStatus.INVOICED.value().equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("La orden debe estar facturada para generar el ticket.");
        }

        return ticketPdfService.generate(order);
    }

    private Map<Long, Product> fetchProducts(CreateOrderRequest request) {
        List<Long> ids = request.items().stream()
            .map(i -> i.productId())
            .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(ids);
        Map<Long, Product> map = new HashMap<>();
        products.forEach(product -> map.put(product.getId(), product));

        if (map.size() != ids.stream().collect(Collectors.toSet()).size()) {
            throw new ResourceNotFoundException("Uno o más productos no existen");
        }

        return map;
    }

    private String mapSortColumn(String sortBy) {
        String safeSort = (sortBy == null || sortBy.isBlank()) ? "order_date" : sortBy.toLowerCase();
        return SORTABLE_COLUMNS.getOrDefault(safeSort, "orderDate");
    }
}
