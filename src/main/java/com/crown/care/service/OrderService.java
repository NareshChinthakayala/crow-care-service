package com.crown.care.service;

import com.crown.care.dto.*;
import com.crown.care.entity.*;
import com.crown.care.exception.ApiException;
import com.crown.care.repository.OrderRepository;
import com.crown.care.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmployeeService employeeService;

    // ---------------- Admin ----------------

    /** All filters are optional. */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll(User admin, LocalDate date, OrderStatus status, Long employeeId) {
        Long salonId = admin.getSalon().getId();
        List<Order> orders = (date != null)
                ? orderRepository.findBySalonIdAndAppointmentDateOrderByAppointmentTimeAsc(salonId, date)
                : orderRepository.findBySalonIdOrderByAppointmentDateDescAppointmentTimeDesc(salonId);

        return orders.stream()
                .filter(o -> status == null || o.getStatus() == status)
                .filter(o -> employeeId == null
                        || (o.getAssignedEmployee() != null && o.getAssignedEmployee().getId().equals(employeeId)))
                .map(Mapper::toOrder)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(User admin, Long id) {
        return Mapper.toOrder(find(admin, id));
    }

    @Transactional
    public OrderResponse create(User admin, OrderRequest req) {
        Order order = new Order();
        order.setSalon(admin.getSalon());
        apply(admin, order, req);
        return Mapper.toOrder(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse update(User admin, Long id, OrderRequest req) {
        Order order = find(admin, id);
        apply(admin, order, req);
        return Mapper.toOrder(orderRepository.save(order));
    }

    @Transactional
    public void delete(User admin, Long id) {
        Order order = find(admin, id);
        restoreStock(order);
        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponse assign(User admin, Long id, Long employeeId) {
        Order order = find(admin, id);
        order.setAssignedEmployee(employeeId == null ? null : employeeService.find(admin, employeeId));
        return Mapper.toOrder(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse changeStatus(User admin, Long id, OrderStatus status) {
        Order order = find(admin, id);
        setStatus(order, status);
        return Mapper.toOrder(orderRepository.save(order));
    }

    // ---------------- Employee (stylist) ----------------

    /** date == null means "today"; all == true returns every appointment ever assigned. */
    @Transactional(readOnly = true)
    public List<OrderResponse> myAppointments(User employee, LocalDate date, boolean all) {
        List<Order> orders = all
                ? orderRepository.findByAssignedEmployeeIdOrderByAppointmentDateDescAppointmentTimeAsc(employee.getId())
                : orderRepository.findByAssignedEmployeeIdAndAppointmentDateOrderByAppointmentTimeAsc(
                        employee.getId(), date != null ? date : LocalDate.now());
        return orders.stream().map(Mapper::toOrder).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse myAppointment(User employee, Long id) {
        return Mapper.toOrder(findForEmployee(employee, id));
    }

    @Transactional
    public OrderResponse updateMyAppointmentStatus(User employee, Long id, OrderStatus status) {
        Order order = findForEmployee(employee, id);
        setStatus(order, status);
        return Mapper.toOrder(orderRepository.save(order));
    }

    // ---------------- helpers ----------------

    private Order find(User admin, Long id) {
        return orderRepository.findByIdAndSalonId(id, admin.getSalon().getId())
                .orElseThrow(() -> ApiException.notFound("Order not found"));
    }

    private Order findForEmployee(User employee, Long id) {
        return orderRepository.findByIdAndAssignedEmployeeId(id, employee.getId())
                .orElseThrow(() -> ApiException.notFound("Appointment not found"));
    }

    private void setStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        order.setCompletedAt(status == OrderStatus.DONE ? LocalDateTime.now() : null);
    }

    /** Copies request data onto the order, replaces product items and recalculates the total. */
    private void apply(User admin, Order order, OrderRequest req) {
        order.setCustomerName(req.customerName());
        order.setCustomerPhone(req.customerPhone());
        order.setOrderType(req.orderType() != null ? req.orderType() : OrderType.WALK_IN);
        order.setServiceName(req.serviceName());
        order.setServicePrice(req.servicePrice() != null ? req.servicePrice() : BigDecimal.ZERO);
        order.setAppointmentDate(req.appointmentDate() != null ? req.appointmentDate() : LocalDate.now());
        order.setAppointmentTime(req.appointmentTime());
        order.setNotes(req.notes());
        order.setAssignedEmployee(req.employeeId() != null ? employeeService.find(admin, req.employeeId()) : null);

        // replace items: give back stock of old items, then take stock for new items
        restoreStock(order);
        order.getItems().clear();

        BigDecimal total = order.getServicePrice();
        if (req.items() != null) {
            for (OrderItemRequest itemReq : req.items()) {
                Product product = productRepository.findByIdAndSalonId(itemReq.productId(), admin.getSalon().getId())
                        .orElseThrow(() -> ApiException.notFound("Product not found: " + itemReq.productId()));
                product.setStockQuantity(Math.max(0, product.getStockQuantity() - itemReq.quantity()));

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(product);
                item.setProductName(product.getName());
                item.setQuantity(itemReq.quantity());
                item.setPrice(product.getPrice());
                order.getItems().add(item);

                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())));
            }
        }
        order.setTotalAmount(total);
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product p = item.getProduct();
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
            }
        }
    }
}
