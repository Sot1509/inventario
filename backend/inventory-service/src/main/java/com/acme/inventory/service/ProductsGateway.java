package com.acme.inventory.service;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Component
public class ProductsGateway {
    private final WebClient client;
    public ProductsGateway(WebClient productsClient){ this.client = productsClient; }


    @CircuitBreaker(name = "products", fallbackMethod = "existsFallback")
    @Retry(name = "products")
    @TimeLimiter(name = "products")
    public CompletableFuture<Boolean> exists(UUID productId){
        
        return client.get().uri("/products/{id}", productId)
        .accept(MediaType.valueOf("application/vnd.api+json"))
        .retrieve()
        .bodyToMono(Map.class)
        .map(m -> true)
        .toFuture();
    }


    private CompletableFuture<Boolean> existsFallback(UUID productId, Throwable ex){
     return CompletableFuture.completedFuture(false);
    }
}