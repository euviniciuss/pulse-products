package com.pulse.pulse_products.product.controller;

import com.pulse.pulse_products.product.dto.ProductRequestDTO;
import com.pulse.pulse_products.product.dto.ProductResponseDTO;
import com.pulse.pulse_products.product.entity.Product;
import com.pulse.pulse_products.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        Product product = toEntity(request);
        Product createdProduct = productService.create(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(createdProduct));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findAll(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<Product> products = productService.findAll(pageable);
        return ResponseEntity.ok(products.map(this::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable UUID id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(toResponse(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequestDTO request
    ) {
        Product productToUpdate = toEntity(request);
        Product updatedProduct = productService.update(id, productToUpdate);
        return ResponseEntity.ok(toResponse(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Product toEntity(ProductRequestDTO request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setAmount(request.amount());
        return product;
    }

    private ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.from(product);
    }
}
