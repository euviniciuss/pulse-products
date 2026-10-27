package com.pulse.pulse_products.product.controller;

import com.pulse.pulse_products.product.dto.ProductResponseDTO;
import com.pulse.pulse_products.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ProductResponseDTO create() {
        ProductResponseDTO response = new ProductResponseDTO(UUID.randomUUID(),
                "Teclado",
                "Teclado Industrial",
                new BigDecimal("199.90"),
                5);

        return productService.create(response);
    }
}
