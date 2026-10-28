package com.pulse.pulse_products.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public record ProductRequestDTO(
        @NotBlank
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer amount
) {}