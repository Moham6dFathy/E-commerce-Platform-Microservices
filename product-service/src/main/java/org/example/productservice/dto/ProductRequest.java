package org.example.productservice.dto;

import java.math.BigDecimal;

public record ProductRequest(String name, String description, double price, int stock) {
}
