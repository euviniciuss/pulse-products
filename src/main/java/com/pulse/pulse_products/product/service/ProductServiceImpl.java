package com.pulse.pulse_products.product.service;

import com.pulse.pulse_products.product.dto.ProductResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public ProductResponseDTO create(ProductResponseDTO product) {
        return product;
    }

}
