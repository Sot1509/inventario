package com.acme.products.web;

import com.acme.products.domain.Product;
import com.acme.products.jsonapi.JsonApi;
import com.acme.products.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;


import java.util.*;

@RestController
@RequestMapping(value = "/products", produces = "application/vnd.api+json")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public JsonApi.ListResponse<Map<String, Object>> list(
            @RequestParam(name = "page[number]", defaultValue = "1") int number,
            @RequestParam(name = "page[size]", defaultValue = "10") int size) {

        var page = service.list(PageRequest.of(Math.max(number - 1, 0), size));

        // Tipamos explícitamente la lista de recursos:
        List<JsonApi.Resource<Map<String, Object>>> resources = page.getContent().stream()
                .map(p -> new JsonApi.Resource<>(
                        "products",
                        p.getId().toString(),
                        Map.<String, Object>of(
                                "sku", p.getSku(),
                                "name", p.getName(),
                                "description", Optional.ofNullable(p.getDescription()).orElse(""),
                                "price", p.getPrice()
                        ),
                        Map.<String, String>of("self", "/products/" + p.getId())
                ))
                .toList();

        Map<String, String> links = new HashMap<>();
        links.put("self", "/products?page[number]=" + number + "&page[size]=" + size);
        if (page.hasNext()) {
            links.put("next", "/products?page[number]=" + (number + 1) + "&page[size]=" + size);
        }

        Map<String, Object> meta = Map.of(
                "page",
                new JsonApi.PageMeta(number, size, page.getTotalElements(), page.getTotalPages())
        );

        // Especificamos el tipo genérico al construir:
        return new JsonApi.ListResponse<Map<String, Object>>(resources, links, meta);
    }

    @GetMapping("/{id}")
    public JsonApi.OneResponse<Map<String, Object>> get(@PathVariable UUID id) {
        var p = service.get(id);

        var res = new JsonApi.Resource<>(
                "products",
                p.getId().toString(),
                Map.<String, Object>of(
                        "sku", p.getSku(),
                        "name", p.getName(),
                        "description", Optional.ofNullable(p.getDescription()).orElse(""),
                        "price", p.getPrice()
                ),
                Map.<String, String>of("self", "/products/" + p.getId())
        );

        return new JsonApi.OneResponse<Map<String, Object>>(res);
    }

        @PostMapping(consumes = { "application/vnd.api+json", MediaType.APPLICATION_JSON_VALUE })
        public JsonApi.OneResponse<Map<String,Object>> create(@RequestBody Map<String,Object> payload) { 
        // payload JSON:API: { data: { type, attributes: { ... } } }
        var data = (Map<String, Object>) payload.get("data");
        var attrs = (Map<String, Object>) data.get("attributes");

        var p = new Product();
        p.setSku((String) attrs.get("sku"));
        p.setName((String) attrs.get("name"));
        p.setDescription((String) attrs.getOrDefault("description", ""));
        p.setPrice(new java.math.BigDecimal(attrs.get("price").toString()));

        var saved = service.create(p);

        var res = new JsonApi.Resource<>(
                "products",
                saved.getId().toString(),
                Map.<String, Object>of(
                        "sku", saved.getSku(),
                        "name", saved.getName(),
                        "description", Optional.ofNullable(saved.getDescription()).orElse(""),
                        "price", saved.getPrice()
                ),
                Map.<String, String>of("self", "/products/" + saved.getId())
        );

        return new JsonApi.OneResponse<Map<String, Object>>(res);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/vnd.api+json", MediaType.APPLICATION_JSON_VALUE })
    public JsonApi.OneResponse<Map<String, Object>> update(@PathVariable UUID id,
                                                           @RequestBody Map<String, Object> payload) {
        var data = (Map<String, Object>) payload.get("data");
        var attrs = (Map<String, Object>) data.get("attributes");

        var incoming = new Product();
        incoming.setSku((String) attrs.get("sku"));
        incoming.setName((String) attrs.get("name"));
        incoming.setDescription((String) attrs.getOrDefault("description", ""));
        incoming.setPrice(new java.math.BigDecimal(attrs.get("price").toString()));

        var saved = service.update(id, incoming);
        return get(saved.getId());
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        service.delete(id);
        return Map.of("meta", Map.of("deleted", id.toString()));
    }
}
