package com.crown.care.controller;

import com.crown.care.dto.EmployeeRequest;
import com.crown.care.dto.EmployeeResponse;
import com.crown.care.dto.MessageResponse;
import com.crown.care.entity.User;
import com.crown.care.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin manages the styling team. */
@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeResponse> getAll(@AuthenticationPrincipal User admin) {
        return employeeService.getAll(admin);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@AuthenticationPrincipal User admin, @PathVariable Long id) {
        return employeeService.getById(admin, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@AuthenticationPrincipal User admin, @RequestBody EmployeeRequest request) {
        return employeeService.create(admin, request);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@AuthenticationPrincipal User admin, @PathVariable Long id,
                                   @RequestBody EmployeeRequest request) {
        return employeeService.update(admin, id, request);
    }

    @DeleteMapping("/{id}")
    public MessageResponse delete(@AuthenticationPrincipal User admin, @PathVariable Long id) {
        employeeService.delete(admin, id);
        return new MessageResponse("Employee deleted");
    }
}
