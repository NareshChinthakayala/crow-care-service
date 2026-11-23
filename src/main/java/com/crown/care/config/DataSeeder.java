package com.crown.care.config;

import com.crown.care.entity.*;
import com.crown.care.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Inserts demo data the first time the app starts (only when the users table is empty).
 * Admin:     admin@crowncare.com / admin123
 * Employees: EMP002, EMP003, EMP004 (or their emails) / emp123
 */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SalonRepository salonRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        Salon salon = new Salon();
        salon.setName("CrownCare African Hair Studio");
        salon.setAddress("12 Allen Avenue, Ikeja, Lagos");
        salon.setPhone("+234 800 000 0000");
        salon.setEmail("admin@crowncare.com");
        salonRepository.save(salon);

        User admin = new User();
        admin.setFullName("Naomi Adebayo");
        admin.setEmail("admin@crowncare.com");
        admin.setPhone("+234 800 000 0000");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setSalon(salon);
        userRepository.save(admin);

        User amara = employee(salon, "Amara Okafor", "amara@crowncare.com", "Box braids");
        User zainab = employee(salon, "Zainab Bello", "zainab@crowncare.com", "Locs & twists");
        User kemi = employee(salon, "Kemi Adeyemi", "kemi@crowncare.com", "Wigs & weaves");

        product(salon, "Shea Butter Leave-in Conditioner", "Conditioner", "4500", 24, 5);
        product(salon, "Jamaican Black Castor Oil", "Oil", "3800", 3, 5);
        product(salon, "Edge Control Gel", "Styling", "2500", 15, 5);
        product(salon, "Braiding Hair (Pre-stretched)", "Extensions", "1800", 4, 10);
        product(salon, "Loc Twisting Gel", "Styling", "3200", 12, 5);

        LocalDate today = LocalDate.now();
        order(salon, "Chioma Eze", "+234 801 111 1111", OrderType.WALK_IN, "Knotless Box Braids", "25000",
                today, LocalTime.of(9, 0), amara, OrderStatus.DONE);
        order(salon, "Fatima Musa", "+234 802 222 2222", OrderType.PHONE, "Starter Locs", "18000",
                today, LocalTime.of(11, 30), zainab, OrderStatus.IN_PROGRESS);
        order(salon, "Ngozi Obi", "+234 803 333 3333", OrderType.WALK_IN, "Wig Install", "15000",
                today, LocalTime.of(14, 0), kemi, OrderStatus.PENDING);
        order(salon, "Aisha Lawal", "+234 804 444 4444", OrderType.PHONE, "Cornrows", "8000",
                today, LocalTime.of(16, 0), null, OrderStatus.PENDING);
    }

    private User employee(Salon salon, String name, String email, String specialty) {
        User u = new User();
        u.setFullName(name);
        u.setEmail(email);
        u.setPhone("+234 800 123 4567");
        u.setSpecialty(specialty);
        u.setPassword(passwordEncoder.encode("emp123"));
        u.setRole(Role.EMPLOYEE);
        u.setSalon(salon);
        userRepository.save(u);
        u.setEmployeeCode(String.format("EMP%03d", u.getId()));
        return userRepository.save(u);
    }

    private void product(Salon salon, String name, String category, String price, int stock, int threshold) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setDescription("Recommended for African hairstyles");
        p.setPrice(new BigDecimal(price));
        p.setStockQuantity(stock);
        p.setLowStockThreshold(threshold);
        p.setSalon(salon);
        productRepository.save(p);
    }

    private void order(Salon salon, String customer, String phone, OrderType type, String service, String price,
                       LocalDate date, LocalTime time, User employee, OrderStatus status) {
        Order o = new Order();
        o.setCustomerName(customer);
        o.setCustomerPhone(phone);
        o.setOrderType(type);
        o.setServiceName(service);
        o.setServicePrice(new BigDecimal(price));
        o.setTotalAmount(new BigDecimal(price));
        o.setAppointmentDate(date);
        o.setAppointmentTime(time);
        o.setAssignedEmployee(employee);
        o.setStatus(status);
        o.setSalon(salon);
        orderRepository.save(o);
    }
}
