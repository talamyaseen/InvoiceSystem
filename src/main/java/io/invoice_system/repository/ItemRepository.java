package io.invoice_system.repository;

import io.invoice_system.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer> {
 
}
