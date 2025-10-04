package com.acme.inventory;

import com.acme.inventory.domain.Inventory;
import com.acme.inventory.repository.InventoryRepository;
import com.acme.inventory.service.InventoryService;
import com.acme.inventory.web.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceUnitTest {

  InventoryRepository repo;
  InventoryService service;

  @BeforeEach
  void setup() {
    repo = mock(InventoryRepository.class);
    service = new InventoryService(repo);
  }

  @Test
  void setIfAbsent_creates_when_missing() {
    var id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.empty());
    ArgumentCaptor<Inventory> cap = ArgumentCaptor.forClass(Inventory.class);
    when(repo.save(cap.capture())).thenAnswer(inv -> inv.getArgument(0));

    var res = service.setIfAbsent(id, 5);
    assertThat(res.getProductId()).isEqualTo(id);
    assertThat(res.getQuantity()).isEqualTo(5);
  }

  @Test
  void get_throws_when_not_found() {
    var id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.get(id)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void decrement_ok_and_persists() {
    var id = UUID.randomUUID();
    var inv = new Inventory();
    inv.setProductId(id);
    inv.setQuantity(3);

    when(repo.findById(id)).thenReturn(Optional.of(inv));
    when(repo.save(any())).thenAnswer(a -> a.getArgument(0));

    var res = service.decrement(id, 2);
    assertThat(res.getQuantity()).isEqualTo(1);
  }

  @Test
  void decrement_fails_on_insufficient() {
    var id = UUID.randomUUID();
    var inv = new Inventory();
    inv.setProductId(id);
    inv.setQuantity(1);

    when(repo.findById(id)).thenReturn(Optional.of(inv));
    assertThatThrownBy(() -> service.decrement(id, 2))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Insufficient stock");
  }
}
