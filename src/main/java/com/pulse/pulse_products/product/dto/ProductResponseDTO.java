package com.pulse.pulse_products.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO (
    UUID id,

    String name,

    @Size(max = 1000)
    String description,

    @NotNull
    @DecimalMin("0.00")
    BigDecimal price,

    int amount
) {}
