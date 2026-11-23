package com.crown.care.controller;

import com.crown.care.dto.*;
import com.crown.care.entity.OrderStatus;
import com.crown.care.entity.User;
import com.crown.care.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Offline orders (walk-in / phone): create, assign to employees, track progress. */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /** Optional filters: ?date=2026-11-17&status=PENDING&employeeId=2 */
    @GetMapping
    public List<OrderResponse> getAll(@AuthenticationPrincipal User admin,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      @RequestParam(required = false) OrderStatus status,
                                      @RequestParam(required = false) Long employeeId) {
        return orderService.getAll(admin, date, status, employeeId);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@AuthenticationPrincipal User admin, @PathVariable Long id) {
        return orderService.getById(admin, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@AuthenticationPrincipal User admin, @RequestBody OrderRequest request) {
        return orderService.create(admin, request);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@AuthenticationPrincipal User admin, @PathVariable Long id,
                                @RequestBody OrderRequest request) {
        return orderService.update(admin, id, request);
    }

    /** Body: { "employeeId": 2 } (null to unassign) */
    @PatchMapping("/{id}/assign")
    public OrderResponse assign(@AuthenticationPrincipal User admin, @PathVariable Long id,
                                @RequestBody AssignRequest request) {
        return orderService.assign(admin, id, request.employeeId());
    }

    /** Body: { "status": "IN_PROGRESS" }  (PENDING, IN_PROGRESS, DONE, CANCELLED) */
    @PatchMapping("/{id}/status")
    public OrderResponse changeStatus(@AuthenticationPrincipal User admin, @PathVariable Long id,
                                      @RequestBody StatusRequest request) {
        return orderService.changeStatus(admin, id, request.status());
    }

    @DeleteMapping("/{id}")
    public MessageResponse delete(@AuthenticationPrincipal User admin, @PathVariable Long id) {
        orderService.delete(admin, id);
        return new MessageResponse("Order deleted");
    }
}
