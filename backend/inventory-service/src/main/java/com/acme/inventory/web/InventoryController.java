package com.acme.inventory.web;

import com.acme.inventory.domain.Inventory;
import com.acme.inventory.jsonapi.JsonApi;
import com.acme.inventory.service.InventoryService;
import com.acme.inventory.service.ProductsGateway;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/inventories", produces = "application/vnd.api+json")
public class InventoryController {
    private final InventoryService service;
    private final ProductsGateway products;

    public InventoryController(InventoryService service, ProductsGateway products){
        this.service = service;
        this.products = products;
    }

    @GetMapping("/{productId}")
    public JsonApi.OneResponse<Map<String, Object>> get(@PathVariable UUID productId){
        Inventory inv = service.get(productId);

        // TIPAMOS explícitamente el Resource y los Map.of
        JsonApi.Resource<Map<String, Object>> res =
                new JsonApi.Resource<>(
                        "inventories",
                        productId.toString(),
                        Map.<String, Object>of("quantity", inv.getQuantity()),
                        Map.<String, String>of("self", "/inventories/" + productId)
                );

        // TIPAMOS el constructor del OneResponse
        return new JsonApi.OneResponse<Map<String, Object>>(res);
    }

    @PostMapping(value = "/{productId}/decrement", produces = "application/vnd.api+json")
    public JsonApi.OneResponse<Map<String, Object>> decrement(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "1") int by) {

        // valida que el producto exista en products-service
        boolean ok = products.exists(productId).join();
        if (!ok) throw new NotFoundException("Product " + productId + " not found in products-service");

        Inventory inv = service.setIfAbsent(productId, 0);
        inv = service.decrement(productId, by);

        JsonApi.Resource<Map<String, Object>> res =
                new JsonApi.Resource<>(
                        "inventories",
                        productId.toString(),
                        Map.<String, Object>of("quantity", inv.getQuantity()),
                        Map.<String, String>of("self", "/inventories/" + productId)
                );

        return new JsonApi.OneResponse<Map<String, Object>>(res);
    }
}
