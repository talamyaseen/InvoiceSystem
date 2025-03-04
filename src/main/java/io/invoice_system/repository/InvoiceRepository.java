package io.invoice_system.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import io.invoice_system.model.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> ,PagingAndSortingRepository<Invoice, Integer>{
	//public List<Invoice> findByUserId(Integer Id);
	 Page<Invoice> findByUserId(int userId, Pageable pageable);
 
}
