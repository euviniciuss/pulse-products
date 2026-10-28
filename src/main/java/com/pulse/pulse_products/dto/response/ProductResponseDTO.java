package com.pulse.pulse_products.dto.response;

import com.pulse.pulse_products.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponseDTO (
    UUID id,

    String name,

    @Size(max = 1000)
    String description,

    @NotNull
    @DecimalMin("0.00")
    BigDecimal price,

    int amount,

    Instant createdAt,
    Instant updatedAt
) {
    public static ProductResponseDTO from(Product product) {
        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getAmount(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
