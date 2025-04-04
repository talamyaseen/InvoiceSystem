package io.invoice_system.repository;

import io.invoice_system.model.Invoice;
import io.invoice_system.model.InvoiceHistory;
import io.invoice_system.model.InvoiceItem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvoiceHistoryRepository extends CrudRepository<InvoiceHistory, Integer>,PagingAndSortingRepository<InvoiceHistory, Integer> {
	  Optional<InvoiceHistory> findByInvoiceId(Integer invoiceId);
   
}
