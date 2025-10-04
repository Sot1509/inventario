package com.acme.products.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;


    @NotBlank
    @Column(unique = true, length = 64, nullable = false)
    private String sku;


    @NotBlank
    @Column(nullable = false)
    private String name;


    private String description;


    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;


    @PrePersist
    public void pre(){ if(id==null) id = UUID.randomUUID(); }


// getters/setters
    public UUID getId(){return id;}
    public void setId(UUID id){this.id=id;}
    public String getSku(){return sku;}
    public void setSku(String sku){this.sku=sku;}
    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}
    public BigDecimal getPrice(){return price;}
    public void setPrice(BigDecimal price){this.price=price;}
}