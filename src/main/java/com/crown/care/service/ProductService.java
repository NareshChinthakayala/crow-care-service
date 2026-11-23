package com.crown.care.service;

import com.crown.care.dto.Mapper;
import com.crown.care.dto.ProductRequest;
import com.crown.care.dto.ProductResponse;
import com.crown.care.entity.Product;
import com.crown.care.entity.User;
import com.crown.care.exception.ApiException;
import com.crown.care.repository.OrderItemRepository;
import com.crown.care.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public List<ProductResponse> getAll(User admin, boolean lowStockOnly) {
        Long salonId = admin.getSalon().getId();
        List<Product> products = lowStockOnly
                ? productRepository.findLowStock(salonId)
                : productRepository.findBySalonIdOrderByNameAsc(salonId);
        return products.stream().map(Mapper::toProduct).toList();
    }

    public ProductResponse getById(User admin, Long id) {
        return Mapper.toProduct(find(admin, id));
    }

    @Transactional
    public ProductResponse create(User admin, ProductRequest req) {
        Product product = new Product();
        product.setSalon(admin.getSalon());
        copy(req, product);
        return Mapper.toProduct(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(User admin, Long id, ProductRequest req) {
        Product product = find(admin, id);
        copy(req, product);
        return Mapper.toProduct(productRepository.save(product));
    }

    /** Quick stock change, e.g. { "quantity": 10 } adds 10, { "quantity": -2 } removes 2. */
    @Transactional
    public ProductResponse adjustStock(User admin, Long id, int quantity) {
        Product product = find(admin, id);
        product.setStockQuantity(Math.max(0, product.getStockQuantity() + quantity));
        return Mapper.toProduct(productRepository.save(product));
    }

    @Transactional
    public void delete(User admin, Long id) {
        Product product = find(admin, id);
        orderItemRepository.detachProduct(product.getId()); // old orders keep the product name
        productRepository.delete(product);
    }

    public Product find(User admin, Long id) {
        return productRepository.findByIdAndSalonId(id, admin.getSalon().getId())
                .orElseThrow(() -> ApiException.notFound("Product not found"));
    }

    private void copy(ProductRequest req, Product p) {
        p.setName(req.name());
        p.setCategory(req.category());
        p.setDescription(req.description());
        p.setImageUrl(req.imageUrl());
        if (req.price() != null) p.setPrice(req.price());
        if (req.stockQuantity() != null) p.setStockQuantity(req.stockQuantity());
        if (req.lowStockThreshold() != null) p.setLowStockThreshold(req.lowStockThreshold());
    }
}
