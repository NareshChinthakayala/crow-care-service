package com.crown.care.dto;

import com.crown.care.entity.Order;
import com.crown.care.entity.OrderItem;
import com.crown.care.entity.Product;
import com.crown.care.entity.User;

/**
 * Converts entities into response DTOs.
 */
public final class Mapper {

    private Mapper() {
    }

    public static UserResponse toUser(User u) {
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getRole(),
                u.getEmployeeCode(), u.getSalon().getId(), u.getSalon().getName());
    }

    public static EmployeeResponse toEmployee(User u) {
        return new EmployeeResponse(u.getId(), u.getEmployeeCode(), u.getFullName(), u.getEmail(),
                u.getPhone(), u.getSpecialty(), u.isActive(), u.getCreatedAt());
    }

    public static ProductResponse toProduct(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getCategory(), p.getDescription(), p.getPrice(),
                p.getStockQuantity(), p.getLowStockThreshold(), p.isLowStock(), p.getImageUrl(), p.getCreatedAt());
    }

    public static OrderItemResponse toOrderItem(OrderItem i) {
        Long productId = i.getProduct() != null ? i.getProduct().getId() : null;
        return new OrderItemResponse(i.getId(), productId, i.getProductName(), i.getQuantity(), i.getPrice());
    }

    public static OrderResponse toOrder(Order o) {
        User emp = o.getAssignedEmployee();
        return new OrderResponse(
                o.getId(),
                o.getCustomerName(),
                o.getCustomerPhone(),
                o.getOrderType(),
                o.getServiceName(),
                o.getServicePrice(),
                o.getAppointmentDate(),
                o.getAppointmentTime(),
                o.getNotes(),
                o.getStatus(),
                o.getTotalAmount(),
                emp != null ? emp.getId() : null,
                emp != null ? emp.getFullName() : null,
                o.getItems().stream().map(Mapper::toOrderItem).toList(),
                o.getCreatedAt(),
                o.getCompletedAt()
        );
    }
}
