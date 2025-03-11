package io.invoice_system.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.invoice_system.dto.InvoiceHistoryDTO;
import io.invoice_system.model.UserEntity;
import io.invoice_system.repository.UserRepository;
import io.invoice_system.service.InvoiceHistoryService;

@RestController
@RequestMapping("/invoices_history")
public class InvoiceHistoryController {
      
	@Autowired
	 private UserRepository userRepository;
	@Autowired
	private  InvoiceHistoryService invoiceHistoryService;

	@PreAuthorize("hasAnyRole('SUPPORT_USER','SUPERUSER')")
	 @GetMapping("")
	    public ResponseEntity<List<InvoiceHistoryDTO>> getInvoices(Principal principal, 
	                                                         @RequestParam(defaultValue = "0") int page, 
	                                                         @RequestParam(defaultValue = "10") int size) {
	        String username = principal.getName();
	        UserEntity user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new IllegalArgumentException("User not found"));

	       Pageable pageable = PageRequest.of(page, size);
	       Page<InvoiceHistoryDTO> invoicePage = null ;
	      
	        	 invoicePage = invoiceHistoryService.getAllInvoicesHistory(pageable).map(InvoiceHistoryDTO::new); 

	        return ResponseEntity.ok(invoicePage.getContent());
	   
}
}