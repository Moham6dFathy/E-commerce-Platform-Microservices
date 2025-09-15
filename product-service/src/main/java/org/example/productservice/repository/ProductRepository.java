package org.example.productservice.repository;

import org.example.productservice.dto.ProductRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.productservice.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {


}
