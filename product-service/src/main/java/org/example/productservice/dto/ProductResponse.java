package org.example.productservice.dto;

public record ProductResponse(int id , String name, String description , double price , int stock) {
}
