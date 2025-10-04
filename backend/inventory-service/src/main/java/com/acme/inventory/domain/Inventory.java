package com.acme.inventory.domain;


import jakarta.persistence.*;
import java.util.UUID;


@Entity
@Table(name = "inventory")
public class Inventory {
@Id
@Column(name = "product_id", columnDefinition = "BINARY(16)")
private UUID productId;


@Column(nullable = false)
private Integer quantity;


public UUID getProductId(){return productId;}
public void setProductId(UUID id){this.productId=id;}
public Integer getQuantity(){return quantity;}
public void setQuantity(Integer q){this.quantity=q;}
}