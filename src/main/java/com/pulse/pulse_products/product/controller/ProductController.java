package com.pulse.pulse_products.product.controller;

import com.pulse.pulse_products.product.dto.ProductRequestDTO;
import com.pulse.pulse_products.product.dto.ProductResponseDTO;
import com.pulse.pulse_products.product.entity.Product;
import com.pulse.pulse_products.product.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO request
    ) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setAmount(request.amount());

        Product createdProduct = productService.create(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(createdProduct));
    }

    private ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.from(product);
    }
}
