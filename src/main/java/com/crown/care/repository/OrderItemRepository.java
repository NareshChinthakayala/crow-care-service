package com.crown.care.repository;

import com.crown.care.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Modifying
    @Query("update OrderItem i set i.product = null where i.product.id = :productId")
    void detachProduct(@Param("productId") Long productId);
}
