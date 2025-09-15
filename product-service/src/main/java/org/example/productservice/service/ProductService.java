package org.example.productservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.controller.ProductController;
import org.example.productservice.dto.ProductRequest;
import org.example.productservice.dto.ProductResponse;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.model.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .stock(productRequest.stock())
                .build();

        productRepository.save(product);

        log.info("Product {} has been created", product);

        return new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock());
    }

    public Page<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findAll(pageable)
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock()
                ));
    }

    public ProductResponse getProductById(String id) {
        return productRepository.findById(Integer.parseInt(id))
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock()
                ))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public ProductResponse updateProductById(String id, ProductRequest productRequest) {
        Product product = productRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setStock(productRequest.stock());

        Product updatedProduct = productRepository.save(product);

        return new ProductResponse(updatedProduct.getId()
                ,updatedProduct.getName()
                ,updatedProduct.getDescription()
                ,updatedProduct.getPrice()
                ,updatedProduct.getStock());
    }

    public void deleteProductById(String id) {
        productRepository.deleteById(Integer.parseInt(id));
    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }


    public boolean isInStock(int productId , int quantity){
        log.info("In stock check");

        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        return product.getStock() >= quantity;
    }
}
