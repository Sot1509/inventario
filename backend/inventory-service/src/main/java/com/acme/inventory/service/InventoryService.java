package com.acme.inventory.service;


import com.acme.inventory.domain.Inventory;
import com.acme.inventory.repository.InventoryRepository;
import com.acme.inventory.web.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;


@Service
public class InventoryService {
    private final InventoryRepository repo;
    public InventoryService(InventoryRepository repo){ this.repo = repo; }


    public Inventory get(UUID productId){
        return repo.findById(productId).orElseThrow(() -> new NotFoundException("Inventory for product "+productId+" not found"));
    }


    @Transactional
    public Inventory setIfAbsent(UUID productId, int initial){
        return repo.findById(productId).orElseGet(() -> {
            Inventory inv = new Inventory();
            inv.setProductId(productId);
            inv.setQuantity(initial);
            return repo.save(inv);
        });
    }


    @Transactional
    public Inventory decrement(UUID productId, int by){
        if(by <= 0) throw new IllegalArgumentException("'by' must be > 0");
        Inventory inv = get(productId);
        int next = inv.getQuantity() - by;
        if(next < 0) throw new IllegalArgumentException("Insufficient stock");
        inv.setQuantity(next);
        return repo.save(inv);
    }
}