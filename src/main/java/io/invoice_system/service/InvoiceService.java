
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private InvoiceHistoryRepository invoiceHistoryRepository;
    public Invoice createInvoice(InvoiceDTO invoiceDTO, int userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create a new invoice
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(invoiceDTO.getTotalAmount());
        invoice.setStatus(invoiceDTO.getStatus());
        invoice.setUser(user);

        // Save the invoice first, to get the generated ID
        invoiceRepository.save(invoice);

        // Handle the list of items
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
        invoiceRepository.save(invoice); // Save invoice with items

        // Format the description string
        StringBuilder description = new StringBuilder("Invoice created: ");
        for (InvoiceItemDTO itemDTO : invoiceDTO.getItems()) {
            Item item = itemRepository.findById(itemDTO.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found"));
            description.append(item.getName()).append(" (")
            .append(itemDTO.getQuantity()).append("), ");
    
        }
        description.append("Total: ").append(invoiceDTO.getTotalAmount());

        // Record invoice creation in the history
        InvoiceHistory invoiceHistory = new InvoiceHistory();
        invoiceHistory.setInvoiceId(invoice.getId());
        invoiceHistory.setDescription(description.toString());
        invoiceHistory.setChangedByUserId(String.valueOf(userId));
        invoiceHistory.setChangedAt(LocalDateTime.now());
        invoiceHistory.setIsDeleted(false);
        invoiceHistoryRepository.save(invoiceHistory);

        return invoice;
    }


    public List<Invoice> getInvoicesByUser(int userId) {
        return invoiceRepository.findByUserId(userId);
    }

    public boolean deleteInvoice(int invoiceId, int userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        if (invoice.getUser().getId()!=userId) {
            return false; // User is not authorized to delete this invoice
        }

        // Mark invoice history as deleted
        InvoiceHistory invoiceHistory = invoiceHistoryRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice history not found"));
        invoiceHistory.setIsDeleted(true);
        invoiceHistoryRepository.save(invoiceHistory);

        // Delete the invoice
        invoiceRepository.delete(invoice);
        return true;
    }
    
    public Invoice updateInvoice(int invoiceId, InvoiceDTO invoiceDTO, int userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        if (invoice.getUser().getId() != userId) {
            return null; 
        }

        // Recalculate total amount based on updated items
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
                // Add the item's total to the updated total amount
                updatedTotalAmount += invoiceItem.getItem().getPrice() * itemDTO.getQuantity();
            }
        }

        // Update total amount with the recalculated value
        invoice.setTotalAmount(updatedTotalAmount);

        // Handle items to be removed (same as before)
        List<InvoiceItem> itemsToRemove = invoice.getInvoiceItems().stream()
                .filter(invoiceItem -> updatedItems.stream()
                        .noneMatch(updatedItem -> updatedItem.getItem().getId() == invoiceItem.getItem().getId()))
                .collect(Collectors.toList());
        invoice.getInvoiceItems().removeAll(itemsToRemove);
        invoiceRepository.save(invoice);

        // Append new info to the invoice history description
        InvoiceHistory existingHistory = invoiceHistoryRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice history not found"));

        StringBuilder description = new StringBuilder(existingHistory.getDescription() + " | Invoice updated: ");
        for (InvoiceItemDTO itemDTO : invoiceDTO.getItems()) {
            Item item = itemRepository.findById(itemDTO.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found"));
            description.append(item.getName()).append(" (")
                    .append(itemDTO.getQuantity()).append("), ");
        }
        description.append("Total: ").append(updatedTotalAmount); // Use the recalculated total

        // Update invoice history
        existingHistory.setDescription(description.toString());
        existingHistory.setChangedByUserId(String.valueOf(userId));
        existingHistory.setChangedAt(LocalDateTime.now());
        existingHistory.setIsDeleted(false);
        invoiceHistoryRepository.save(existingHistory);

        return invoice;
    }


}
