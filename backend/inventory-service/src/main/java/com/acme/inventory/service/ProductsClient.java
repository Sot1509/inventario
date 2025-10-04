package com.acme.inventory.service;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.Map;
import java.util.UUID;


@Component
public class ProductsClient {
    private final WebClient client;
    public ProductsClient(WebClient productsClient){ this.client = productsClient; }


    public Mono<Boolean> exists(UUID productId){
        return client.get().uri("/products/{id}", productId)
        .accept(MediaType.valueOf("application/vnd.api+json"))
        .retrieve()
        .bodyToMono(Map.class)
        .map(m -> true)
        .onErrorResume(ex -> Mono.just(false));
    }
}