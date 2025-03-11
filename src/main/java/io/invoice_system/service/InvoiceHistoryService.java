package io.invoice_system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.invoice_system.dto.InvoiceItemDTO;
import io.invoice_system.model.Invoice;
import io.invoice_system.model.InvoiceHistory;
import io.invoice_system.model.InvoiceItem;
import io.invoice_system.model.UserEntity;
import io.invoice_system.repository.InvoiceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceHistoryService {

    @Autowired
    private InvoiceHistoryRepository invoiceHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;
    public void createInvoiceHistory(int invoiceId, String action, double totalAmount, UserEntity user, List<InvoiceItem> invoiceItems) {
  
        ObjectNode descriptionNode = objectMapper.createObjectNode();
        descriptionNode.put("action", action);
        descriptionNode.put("totalAmount", totalAmount);

        ArrayNode itemsArrayNode = objectMapper.createArrayNode();
        for (InvoiceItem itemDTO : invoiceItems) {
            ObjectNode itemNode = objectMapper.createObjectNode();
            itemNode.put("itemName", itemDTO.getItem().getName());
            itemNode.put("quantity", itemDTO.getQuantity());
            itemsArrayNode.add(itemNode);
        }
        descriptionNode.set("items", itemsArrayNode);

        InvoiceHistory invoiceHistory = new InvoiceHistory();
        invoiceHistory.setInvoiceId(invoiceId);
        invoiceHistory.setDescription(descriptionNode); 
        invoiceHistory.setChangedByUserId(String.valueOf(user.getId()));
        invoiceHistory.setChangedAt(LocalDateTime.now());
        invoiceHistory.setIsDeleted(false);

        invoiceHistoryRepository.save(invoiceHistory);
    }

    public void updateInvoiceHistory(int invoiceId, String action, double totalAmount, UserEntity user, List<InvoiceItemDTO> items) {
        InvoiceHistory existingHistory = invoiceHistoryRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice history not found"));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode existingDescriptionNode = existingHistory.getDescription();
        ArrayNode historyArray = objectMapper.createArrayNode();

        if (existingDescriptionNode != null) {
            if (existingDescriptionNode.isArray()) {
                historyArray = (ArrayNode) existingDescriptionNode;  
            } else {
              
                historyArray.add(existingDescriptionNode);
            }
        }


        ObjectNode newEntry = objectMapper.createObjectNode();
        newEntry.put("action", action);
        newEntry.put("totalAmount", totalAmount);

        ArrayNode itemsArrayNode = objectMapper.createArrayNode();
        for (InvoiceItemDTO itemDTO : items) {
            ObjectNode itemNode = objectMapper.createObjectNode();
            itemNode.put("itemName", itemDTO.getItemName());
            itemNode.put("quantity", itemDTO.getQuantity());
            itemsArrayNode.add(itemNode);
        }
        newEntry.set("items", itemsArrayNode);

        historyArray.add(newEntry);
    
        existingHistory.setDescription(historyArray);
        existingHistory.setChangedByUserId(String.valueOf(user.getId()));
        existingHistory.setChangedAt(LocalDateTime.now());
        existingHistory.setIsDeleted(false);

        invoiceHistoryRepository.save(existingHistory);
    }


    public  Page<InvoiceHistory> getAllInvoicesHistory( Pageable pageable) {
       
        return   invoiceHistoryRepository.findAll(pageable);
    }
 


}
