package com.crown.care.repository;

import com.crown.care.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySalonIdOrderByNameAsc(Long salonId);

    Optional<Product> findByIdAndSalonId(Long id, Long salonId);

    @Query("select p from Product p where p.salon.id = :salonId and p.stockQuantity <= p.lowStockThreshold order by p.stockQuantity asc")
    List<Product> findLowStock(@Param("salonId") Long salonId);
}
