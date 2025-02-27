package io.invoice_system.repository;

import io.invoice_system.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceItemRepository extends CrudRepository<InvoiceItem, Integer> {
    // You can add custom queries if needed. For example, finding by invoice ID.
}
