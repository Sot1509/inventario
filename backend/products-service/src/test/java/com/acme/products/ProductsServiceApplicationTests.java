package com.acme.products;

import com.acme.products.domain.Product;
import com.acme.products.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ProductIntegrationTest {

  @Container
  static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
      .withDatabaseName("shop")
      .withUsername("shop")
      .withPassword("shop");

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", mysql::getJdbcUrl);
    r.add("spring.datasource.username", mysql::getUsername);
    r.add("spring.datasource.password", mysql::getPassword);
    r.add("spring.jpa.hibernate.ddl-auto", () -> "update");
  }

  @Autowired
  ProductRepository repo;

  @BeforeEach
  void clean() { repo.deleteAll(); }

  @Test
  void create_and_list_and_find() {
    var p = new Product();
    p.setSku("SKU-TC-001");
    p.setName("Test Keyboard");
    p.setDescription("TC");
    p.setPrice(new BigDecimal("99.99"));
    var saved = repo.save(p);

    var all = repo.findAll();
    assertThat(all).hasSize(1);

    var one = repo.findById(saved.getId());
    assertThat(one).isPresent();
    assertThat(one.get().getSku()).isEqualTo("SKU-TC-001");
  }
}
