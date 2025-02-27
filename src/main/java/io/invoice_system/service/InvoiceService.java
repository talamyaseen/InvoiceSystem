
package io.invoice_system.service;

import io.invoice_system.dto.InvoiceDTO;
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
                    // Get the item from the database using itemId from the DTO
                    Item item = itemRepository.findById(itemDTO.getItemId())
                            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

                    // Create an InvoiceItem entity
                    InvoiceItem invoiceItem = new InvoiceItem();
                    invoiceItem.setInvoice(invoice);  // Associate with the current invoice
                    invoiceItem.setItem(item);        // Set the item
                    invoiceItem.setQuantity(itemDTO.getQuantity());  // Set the quantity

                    return invoiceItem;
                })
                .collect(Collectors.toList());

        // Set the invoice items and save them to the database
        invoice.setInvoiceItems(invoiceItems);
        invoiceRepository.save(invoice); // Save invoice with items

        // Record invoice creation in the history
        InvoiceHistory invoiceHistory = new InvoiceHistory();
        invoiceHistory.setInvoiceId(invoice.getId());
        invoiceHistory.setDescription("Invoice created");
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

}
