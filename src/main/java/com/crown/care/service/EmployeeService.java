package com.crown.care.service;

import com.crown.care.dto.EmployeeRequest;
import com.crown.care.dto.EmployeeResponse;
import com.crown.care.dto.Mapper;
import com.crown.care.entity.Role;
import com.crown.care.entity.User;
import com.crown.care.exception.ApiException;
import com.crown.care.repository.OrderRepository;
import com.crown.care.repository.RefreshTokenRepository;
import com.crown.care.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final String DEFAULT_PASSWORD = "crown123";

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public List<EmployeeResponse> getAll(User admin) {
        return userRepository.findBySalonIdAndRoleOrderByFullNameAsc(admin.getSalon().getId(), Role.EMPLOYEE)
                .stream().map(Mapper::toEmployee).toList();
    }

    public EmployeeResponse getById(User admin, Long id) {
        return Mapper.toEmployee(find(admin, id));
    }

    @Transactional
    public EmployeeResponse create(User admin, EmployeeRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw ApiException.badRequest("Email is already in use");
        }
        User emp = new User();
        emp.setFullName(req.fullName());
        emp.setEmail(req.email());
        emp.setPhone(req.phone());
        emp.setSpecialty(req.specialty());
        emp.setActive(req.active() == null || req.active());
        String password = (req.password() == null || req.password().isBlank()) ? DEFAULT_PASSWORD : req.password();
        emp.setPassword(passwordEncoder.encode(password));
        emp.setRole(Role.EMPLOYEE);
        emp.setSalon(admin.getSalon());
        userRepository.save(emp);

        // Employee ID shown on the login screen, e.g. EMP001
        emp.setEmployeeCode(String.format("EMP%03d", emp.getId()));
        return Mapper.toEmployee(emp);
    }

    @Transactional
    public EmployeeResponse update(User admin, Long id, EmployeeRequest req) {
        User emp = find(admin, id);
        emp.setFullName(req.fullName());
        emp.setEmail(req.email());
        emp.setPhone(req.phone());
        emp.setSpecialty(req.specialty());
        if (req.active() != null) {
            emp.setActive(req.active());
        }
        if (req.password() != null && !req.password().isBlank()) {
            emp.setPassword(passwordEncoder.encode(req.password()));
        }
        return Mapper.toEmployee(userRepository.save(emp));
    }

    @Transactional
    public void delete(User admin, Long id) {
        User emp = find(admin, id);
        orderRepository.unassignEmployee(emp.getId());
        refreshTokenRepository.deleteByUserId(emp.getId());
        userRepository.delete(emp);
    }

    public User find(User admin, Long id) {
        return userRepository.findByIdAndSalonIdAndRole(id, admin.getSalon().getId(), Role.EMPLOYEE)
                .orElseThrow(() -> ApiException.notFound("Employee not found"));
    }
}
