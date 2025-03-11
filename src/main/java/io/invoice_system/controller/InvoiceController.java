package io.invoice_system.controller;

import io.invoice_system.dto.InvoiceDTO;
import io.invoice_system.model.Invoice;
import io.invoice_system.model.InvoiceHistory;
import io.invoice_system.model.UserEntity;
import io.invoice_system.service.CustomUserDetailsService;
import io.invoice_system.service.InvoiceService;
import io.invoice_system.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
	
	private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService ;
    
    @PreAuthorize("hasAnyRole('SUPPORT_USER','SUPERUSER')")
    @PostMapping("")
    public Invoice createInvoice(@RequestBody InvoiceDTO invoiceDTO, Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return invoiceService.createInvoice(invoiceDTO, user);
    }
    
    
    
    @GetMapping("")
    public ResponseEntity<List<InvoiceDTO>> getInvoices(Principal principal, 
                                                         @RequestParam(defaultValue = "0") int page, 
                                                         @RequestParam(defaultValue = "10") int size) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
       Page<InvoiceDTO> invoicePage = null ;
       if (customUserDetailsService.hasRole(user, "Superuser", "Auditor")) { 
        	 invoicePage = invoiceService.getAllInvoices(pageable).map(InvoiceDTO::new); 
        }
        else {
        invoicePage = invoiceService.getInvoicesByUser(user.getId(), pageable)
                .map(InvoiceDTO::new); 
        }
        if (invoicePage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(invoicePage.getContent());
    }

    
    @PreAuthorize("hasAnyRole('SUPPORT_USER','SUPERUSER')")
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<String> deleteInvoice(@PathVariable int invoiceId, Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
       invoiceService.deleteInvoice(invoiceId, user);
       logger.info("Invoice with ID {} deleted successfully by user with ID {}", invoiceId, user.getId());
       return ResponseEntity.ok("Invoice deleted successfully.");
    }
    
    
    @PreAuthorize("hasAnyRole('SUPPORT_USER','SUPERUSER')")
    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable int invoiceId, @RequestBody InvoiceDTO invoiceDTO, Principal principal)  {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Invoice updatedInvoice = invoiceService.updateInvoice(invoiceId, invoiceDTO, user);
        return ResponseEntity.ok(new InvoiceDTO(updatedInvoice));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable int invoiceId, Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId, user);
        return ResponseEntity.ok(invoiceDTO);
    }


}

