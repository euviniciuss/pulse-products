package com.pulse.pulse_products.service;

import com.pulse.pulse_products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    Product create(Product product);

    Page<Product> findAll(Pageable pageable);

    Product findById(UUID id);

    Product update(UUID id, Product product);

    void delete(UUID id);
}
