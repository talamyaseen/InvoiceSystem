package io.invoice_system.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.invoice_system.model.InvoiceHistory;
import java.time.LocalDateTime;

public class InvoiceHistoryDTO {
    private int id;
    private int invoiceId;
    private JsonNode description;
    private String changedByUserId;
    private LocalDateTime changedAt;
    private Boolean isDeleted;


    public InvoiceHistoryDTO(InvoiceHistory invoiceHistory) {
        this.id = invoiceHistory.getId();
        this.invoiceId = invoiceHistory.getInvoiceId();
        this.changedByUserId = invoiceHistory.getChangedByUserId();
        this.changedAt = invoiceHistory.getChangedAt();
        this.isDeleted = invoiceHistory.getIsDeleted();
        
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.description = objectMapper.readTree(invoiceHistory.getDescription().toString());
        } catch (Exception e) {
            e.printStackTrace();
            this.description = null;
        }
    }


    public int getId() { return id; }
    public int getInvoiceId() { return invoiceId; }
    public JsonNode getDescription() { return description; }
    public String getChangedByUserId() { return changedByUserId; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public Boolean getIsDeleted() { return isDeleted; }
}
