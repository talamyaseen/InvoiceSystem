
package io.invoice_system.service;

import io.invoice_system.dto.InvoiceDTO;
import io.invoice_system.dto.InvoiceItemDTO;
import io.invoice_system.model.Invoice;
import io.invoice_system.model.InvoiceHistory;
import io.invoice_system.model.InvoiceItem;
import io.invoice_system.model.Item;
import io.invoice_system.model.UserEntity;
import io.invoice_system.repository.InvoiceHistoryRepository;
import io.invoice_system.repository.InvoiceRepository;
import io.invoice_system.repository.ItemRepository;
import io.invoice_system.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    CustomUserDetailsService customUserDetailsService ;
    
    @Autowired
    private InvoiceHistoryRepository invoiceHistoryRepository;
    @Autowired
	private InvoiceHistoryService invoiceHistoryService;
    
    public Invoice createInvoice(InvoiceDTO invoiceDTO, UserEntity user) {
        logger.info("Creating invoice for user: {}", user.getId());

        Invoice invoice = new Invoice();
        invoice.setTotalAmount(invoiceDTO.getTotalAmount());
        invoice.setStatus(invoiceDTO.getStatus());
        invoice.setUser(user);
        invoice.setCreatedTime(invoiceDTO.getCreatedTime());
        invoiceRepository.save(invoice);

        List<InvoiceItem> invoiceItems = invoiceDTO.getItems().stream()
                .map(itemDTO -> {
                    Item item = itemRepository.findById(itemDTO.getItemId())
                            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

                    InvoiceItem invoiceItem = new InvoiceItem();
                    invoiceItem.setInvoice(invoice);
                    invoiceItem.setItem(item);
                    invoiceItem.setQuantity(itemDTO.getQuantity());
                     
                    return invoiceItem;
                })
                .collect(Collectors.toList());

        invoice.setInvoiceItems(invoiceItems);
        invoiceRepository.save(invoice);

        
         invoiceHistoryService.createInvoiceHistory(invoice.getId(), "Invoice created", invoiceDTO.getTotalAmount(), user, invoiceItems );

        logger.info("Invoice created successfully with ID: {}", invoice.getId());
        return invoice;
    }


    public Page<Invoice> getInvoicesByUser(int userId, Pageable pageable) {

        return invoiceRepository.findByUserId(userId, pageable);
    }
    
    
    
    public void deleteInvoice(int invoiceId, UserEntity user) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        if (!customUserDetailsService.hasRole(user, "Superuser")) 
            if (invoice.getUser().getId() != user.getId()) {
            	   throw new AccessDeniedException("Not authorized to delete this invoice");
            }
        InvoiceHistory invoiceHistory = invoiceHistoryRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice history not found"));
        invoiceHistory.setIsDeleted(true);
        invoiceHistoryRepository.save(invoiceHistory);
        invoiceRepository.delete(invoice);
     
    }
    
    
    public Invoice updateInvoice(int invoiceId, InvoiceDTO invoiceDTO, UserEntity user)  {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        logger.info("Updating Invoice with ID: {}", invoiceId);

        if (!customUserDetailsService.hasRole(user, "Superuser")) {
            if (invoice.getUser().getId() != user.getId()) {
                throw new AccessDeniedException("Not authorized to update this invoice");
            }
        }

        double updatedTotalAmount = 0;
        List<InvoiceItem> updatedItems = new ArrayList<>();

        for (InvoiceItemDTO itemDTO : invoiceDTO.getItems()) {
            InvoiceItem invoiceItem = invoice.getInvoiceItems().stream()
                    .filter(item -> item.getItem().getId() == itemDTO.getItemId())
                    .findFirst()
                    .orElse(null);

            if (invoiceItem != null) {
                invoiceItem.setQuantity(itemDTO.getQuantity());
                updatedItems.add(invoiceItem);
                updatedTotalAmount += invoiceItem.getItem().getPrice() * itemDTO.getQuantity();
            }
        }

        invoice.setTotalAmount(updatedTotalAmount);

        List<InvoiceItem> itemsToRemove = invoice.getInvoiceItems().stream()
                .filter(invoiceItem -> updatedItems.stream()
                        .noneMatch(updatedItem -> updatedItem.getItem().getId() == invoiceItem.getItem().getId()))
                .collect(Collectors.toList());
        invoice.getInvoiceItems().removeAll(itemsToRemove);
        invoiceRepository.save(invoice);

      
      invoiceHistoryService.updateInvoiceHistory(invoiceId, "Invoice updated", updatedTotalAmount, user, invoiceDTO.getItems());

        logger.info("Invoice ID: {} updated successfully", invoiceId);
        return invoice;
    }

    
    public InvoiceDTO getInvoiceById(int invoiceId, UserEntity user) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        if (!customUserDetailsService.hasRole(user, "Superuser", "Auditor")) 
        if (invoice.getUser().getId() != user.getId()) {
        	   throw new AccessDeniedException("Not authorized to access this invoice");
        }

        return new InvoiceDTO(invoice);
    }


    public  Page<Invoice> getAllInvoices( Pageable pageable) {
       
        return invoiceRepository.findAll(pageable);
    }
 




}
