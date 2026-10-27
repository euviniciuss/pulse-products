package com.pulse.pulse_products.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO (
    UUID id,
    String name,
    String description,
    BigDecimal price,
    int amount
) {}
