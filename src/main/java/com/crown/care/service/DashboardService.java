package com.crown.care.service;

import com.crown.care.dto.DashboardResponse;
import com.crown.care.dto.EmployeeProgressResponse;
import com.crown.care.dto.Mapper;
import com.crown.care.entity.Order;
import com.crown.care.entity.OrderStatus;
import com.crown.care.entity.Role;
import com.crown.care.entity.User;
import com.crown.care.repository.OrderRepository;
import com.crown.care.repository.ProductRepository;
import com.crown.care.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /** Everything the admin dashboard needs in one call: today's orders, employee progress, low stock. */
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(User admin) {
        Long salonId = admin.getSalon().getId();
        LocalDate today = LocalDate.now();

        List<Order> todayOrders = orderRepository.findBySalonIdAndAppointmentDateOrderByAppointmentTimeAsc(salonId, today);

        List<EmployeeProgressResponse> progress = userRepository
                .findBySalonIdAndRoleOrderByFullNameAsc(salonId, Role.EMPLOYEE).stream()
                .filter(User::isActive)
                .map(emp -> {
                    List<Order> mine = todayOrders.stream()
                            .filter(o -> o.getAssignedEmployee() != null
                                    && o.getAssignedEmployee().getId().equals(emp.getId()))
                            .toList();
                    return new EmployeeProgressResponse(
                            emp.getId(),
                            emp.getEmployeeCode(),
                            emp.getFullName(),
                            mine.size(),
                            count(mine, OrderStatus.PENDING),
                            count(mine, OrderStatus.IN_PROGRESS),
                            count(mine, OrderStatus.DONE));
                })
                .toList();

        return new DashboardResponse(
                today,
                todayOrders.size(),
                count(todayOrders, OrderStatus.PENDING),
                count(todayOrders, OrderStatus.IN_PROGRESS),
                count(todayOrders, OrderStatus.DONE),
                todayOrders.stream().map(Mapper::toOrder).toList(),
                progress,
                productRepository.findLowStock(salonId).stream().map(Mapper::toProduct).toList());
    }

    private long count(List<Order> orders, OrderStatus status) {
        return orders.stream().filter(o -> o.getStatus() == status).count();
    }
}
