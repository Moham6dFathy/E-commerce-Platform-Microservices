package org.example.apigateway.route;

import org.apache.catalina.Server;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute(){
        return route("auth_service")
                .route(RequestPredicates.path("/api/v1/auth/**"), HandlerFunctions.http("http://localhost:8083"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("authServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authServiceSwaggerRoute(){
        return route("auth_service_swagger")
                .route(RequestPredicates.path("/aggregate/auth-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8083"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("authServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api/v1/v3/api-docs"))
                .build();
    }



    @Bean
    public RouterFunction<ServerResponse> productServiceRoute(){
        return route("product_service")
                .route(RequestPredicates.path("/api/v1/product/**"), HandlerFunctions.http("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute(){
        return route("product_service_swagger")
                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api/v1/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute(){
        return route("order_service")
                .route(RequestPredicates.path("/api/v1/order/**"), HandlerFunctions.http("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute(){
        return route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api/v1/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartServiceRoute(){
        return route("cart_service")
                .route(RequestPredicates.path("/api/v1/cart/**"), HandlerFunctions.http("http://localhost:8084"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("cartServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartServiceSwaggerRoute(){
        return route("cart_service_swagger")
                .route(RequestPredicates.path("/aggregate/cart-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8084"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("cartServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/v1/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceRoute(){
        return route("payment_service")
                .route(RequestPredicates.path("/api/v1/payment/**"), HandlerFunctions.http("http://localhost:8085"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("paymentServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceSwaggerRoute(){
        return route("payment_service_swagger")
                .route(RequestPredicates.path("/aggregate/payment-service/v3/api-docs"), HandlerFunctions.http("http://localhost:8085"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("paymentServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/v1/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service Unavailable, please try again later"))
                .build();
    }
}
