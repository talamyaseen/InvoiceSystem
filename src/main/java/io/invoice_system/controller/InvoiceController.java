package io.invoice_system.controller;

import io.invoice_system.dto.InvoiceDTO;
import io.invoice_system.model.Invoice;
import io.invoice_system.model.InvoiceHistory;
import io.invoice_system.model.UserEntity;
import io.invoice_system.service.InvoiceService;
import io.invoice_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public Invoice createInvoice(@RequestBody InvoiceDTO invoiceDTO, Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return invoiceService.createInvoice(invoiceDTO, user.getId());
    }

    @GetMapping("")
    public ResponseEntity<List<InvoiceDTO>> getInvoices(Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<InvoiceDTO> invoices = invoiceService.getInvoicesByUser(user.getId())
                .stream()
                .map(InvoiceDTO::new) 
                .collect(Collectors.toList());

        if (invoices.isEmpty()) {
            return ResponseEntity.noContent().build(); 
        }

        return ResponseEntity.ok(invoices);
    }
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<String> deleteInvoice(@PathVariable int invoiceId, Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean deleted = invoiceService.deleteInvoice(invoiceId, user.getId());

        if (deleted) {
            return ResponseEntity.ok("Invoice deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this invoice.");
        }
    }
    
    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable int invoiceId, @RequestBody InvoiceDTO invoiceDTO, Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Invoice updatedInvoice = invoiceService.updateInvoice(invoiceId, invoiceDTO, user.getId());

        
        if (updatedInvoice == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); 
        }

        return ResponseEntity.ok(new InvoiceDTO(updatedInvoice));
    }


}

