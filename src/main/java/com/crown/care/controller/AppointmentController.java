package com.crown.care.controller;

import com.crown.care.dto.OrderResponse;
import com.crown.care.dto.StatusRequest;
import com.crown.care.entity.OrderStatus;
import com.crown.care.entity.User;
import com.crown.care.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Employee (stylist) screen: see own assigned appointments and update them. */
@RestController
@RequestMapping("/api/employee/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final OrderService orderService;

    /** Today's appointments by default. ?date=2026-11-17 for another day, ?all=true for everything. */
    @GetMapping
    public List<OrderResponse> myAppointments(@AuthenticationPrincipal User employee,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                              @RequestParam(defaultValue = "false") boolean all) {
        return orderService.myAppointments(employee, date, all);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@AuthenticationPrincipal User employee, @PathVariable Long id) {
        return orderService.myAppointment(employee, id);
    }

    /** Body: { "status": "IN_PROGRESS" } or { "status": "DONE" } */
    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@AuthenticationPrincipal User employee, @PathVariable Long id,
                                      @RequestBody StatusRequest request) {
        return orderService.updateMyAppointmentStatus(employee, id, request.status());
    }

    /** Shortcut for the "mark as done" button. */
    @PatchMapping("/{id}/done")
    public OrderResponse markDone(@AuthenticationPrincipal User employee, @PathVariable Long id) {
        return orderService.updateMyAppointmentStatus(employee, id, OrderStatus.DONE);
    }
}
