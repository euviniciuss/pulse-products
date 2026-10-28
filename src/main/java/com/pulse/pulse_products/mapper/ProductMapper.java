package com.pulse.pulse_products.mapper;

import com.pulse.pulse_products.dto.request.ProductRequestDTO;
import com.pulse.pulse_products.dto.response.ProductResponseDTO;
import com.pulse.pulse_products.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(ProductRequestDTO request) {
        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setAmount(request.amount());

        return product;
    }

    public ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.from(product);
    }
}
