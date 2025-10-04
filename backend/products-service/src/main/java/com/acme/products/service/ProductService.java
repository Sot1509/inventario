package com.acme.products.service;



import com.acme.products.domain.Product;
import com.acme.products.repository.ProductRepository;
import com.acme.products.web.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.UUID;


@Service
public class ProductService {
    private final ProductRepository repo;
    public ProductService(ProductRepository repo){ this.repo = repo; }


    public Page<Product> list(Pageable pageable){ return repo.findAll(pageable); }
    public Product get(UUID id){ return repo.findById(id).orElseThrow(() -> new NotFoundException("Product "+id+" not found")); }
    public Product create(Product p){ return repo.save(p); }
    public Product update(UUID id, Product incoming){
        Product cur = get(id);
        cur.setSku(incoming.getSku());
        cur.setName(incoming.getName());
        cur.setDescription(incoming.getDescription());
        cur.setPrice(incoming.getPrice());
        return repo.save(cur);
    }
    public void delete(UUID id){ repo.delete(get(id)); }
}