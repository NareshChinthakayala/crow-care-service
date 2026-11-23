package com.crown.care.controller;

import com.crown.care.dto.MessageResponse;
import com.crown.care.dto.ProductRequest;
import com.crown.care.dto.ProductResponse;
import com.crown.care.entity.User;
import com.crown.care.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** GET /api/admin/products?lowStock=true returns only low-stock products. */
    @GetMapping
    public List<ProductResponse> getAll(@AuthenticationPrincipal User admin,
                                        @RequestParam(defaultValue = "false") boolean lowStock) {
        return productService.getAll(admin, lowStock);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@AuthenticationPrincipal User admin, @PathVariable Long id) {
        return productService.getById(admin, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@AuthenticationPrincipal User admin, @RequestBody ProductRequest request) {
        return productService.create(admin, request);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@AuthenticationPrincipal User admin, @PathVariable Long id,
                                  @RequestBody ProductRequest request) {
        return productService.update(admin, id, request);
    }

    /** Body: { "quantity": 10 } to add stock, negative to reduce. */
    @PatchMapping("/{id}/stock")
    public ProductResponse adjustStock(@AuthenticationPrincipal User admin, @PathVariable Long id,
                                       @RequestBody Map<String, Integer> body) {
        return productService.adjustStock(admin, id, body.getOrDefault("quantity", 0));
    }

    @DeleteMapping("/{id}")
    public MessageResponse delete(@AuthenticationPrincipal User admin, @PathVariable Long id) {
        productService.delete(admin, id);
        return new MessageResponse("Product deleted");
    }
}
