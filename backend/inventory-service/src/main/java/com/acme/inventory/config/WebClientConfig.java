package com.acme.inventory.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {
    @Bean
    WebClient productsClient(@Value("${app.products-base-url}") String baseUrl,
    @Value("${app.api-key}") String apiKey){
        return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("X-API-KEY", apiKey)
        .exchangeStrategies(ExchangeStrategies.builder().codecs(c -> c.defaultCodecs().maxInMemorySize(2*1024*1024)).build())
        .build();
    }
}