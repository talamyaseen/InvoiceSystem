package io.invoice_system.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import io.invoice_system.model.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {
	public List<Invoice> findByUserId(Integer Id);
 
}
