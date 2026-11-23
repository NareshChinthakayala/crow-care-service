package com.crown.care.repository;

import com.crown.care.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findBySalonIdOrderByAppointmentDateDescAppointmentTimeDesc(Long salonId);

    List<Order> findBySalonIdAndAppointmentDateOrderByAppointmentTimeAsc(Long salonId, LocalDate date);

    Optional<Order> findByIdAndSalonId(Long id, Long salonId);

    List<Order> findByAssignedEmployeeIdAndAppointmentDateOrderByAppointmentTimeAsc(Long employeeId, LocalDate date);

    List<Order> findByAssignedEmployeeIdOrderByAppointmentDateDescAppointmentTimeAsc(Long employeeId);

    Optional<Order> findByIdAndAssignedEmployeeId(Long id, Long employeeId);

    @Modifying
    @Query("update Order o set o.assignedEmployee = null where o.assignedEmployee.id = :employeeId")
    void unassignEmployee(@Param("employeeId") Long employeeId);
}
