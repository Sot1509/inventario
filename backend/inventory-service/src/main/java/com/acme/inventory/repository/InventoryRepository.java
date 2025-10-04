package com.acme.inventory.repository;


import com.acme.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface InventoryRepository extends JpaRepository<Inventory, UUID> {}  