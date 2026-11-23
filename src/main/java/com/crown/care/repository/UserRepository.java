package com.crown.care.repository;

import com.crown.care.entity.Role;
import com.crown.care.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrEmployeeCode(String email, String employeeCode);

    boolean existsByEmail(String email);

    List<User> findBySalonIdAndRoleOrderByFullNameAsc(Long salonId, Role role);

    Optional<User> findByIdAndSalonIdAndRole(Long id, Long salonId, Role role);
}
