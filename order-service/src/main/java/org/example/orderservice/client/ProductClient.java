package org.example.orderservice.client;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.dto.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "product", url = "http://localhost:8080")
public interface ProductClient {
    Logger log = LoggerFactory.getLogger(ProductClient.class);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/product/{id}")
    @CircuitBreaker(name = "product", fallbackMethod = "fallbackMethod")
    @Retry(name = "product")
    ProductResponse getProductById(@PathVariable("id") int id);

    default boolean fallbackMethod(String id, Throwable throwable) {
        log.info("Cannot get product for id {}, failure reason: {}", id, throwable.getMessage());
        return false;
    }
}
