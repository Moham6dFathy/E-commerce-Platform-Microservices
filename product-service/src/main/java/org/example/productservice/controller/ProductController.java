package org.example.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.ProductRequest;
import org.example.productservice.dto.ProductResponse;
import org.example.productservice.model.Product;
import org.example.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductResponse> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        return productService.getAllProducts(page,size);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable String id){
        return productService.getProductById(id);
    }

    @GetMapping(path = "/stock")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam int productId,@RequestParam int quantity){
        return productService.isInStock(productId, quantity);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct(@PathVariable String id, @RequestBody ProductRequest productRequest){
        return productService.updateProductById(id,productRequest);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable String id){
        productService.deleteProductById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllProducts(){
        productService.deleteAllProducts();
    }

}
