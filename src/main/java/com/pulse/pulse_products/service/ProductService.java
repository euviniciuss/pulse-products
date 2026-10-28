package com.pulse.pulse_products.service;

import com.pulse.pulse_products.dto.request.ProductRequestDTO;
import com.pulse.pulse_products.dto.response.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO request);

    Page<ProductResponseDTO> findAll(Pageable pageable);

    ProductResponseDTO findById(UUID id);

    ProductResponseDTO update(UUID id, ProductRequestDTO request);

    void delete(UUID id);
}
