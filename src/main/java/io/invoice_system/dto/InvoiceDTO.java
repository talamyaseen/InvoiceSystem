package io.invoice_system.dto;

import io.invoice_system.model.Invoice;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceDTO {
    private int id;
    private String status;
    private double totalAmount;
    private List<InvoiceItemDTO> items;
   private LocalDateTime createdTime; 

    public InvoiceDTO() {}

    public InvoiceDTO(Invoice invoice) {
        this.id = invoice.getId();
        this.status = invoice.getStatus();
        this.totalAmount = invoice.getTotalAmount();
        this.items = invoice.getInvoiceItems()
                .stream()
                .map(InvoiceItemDTO::new)
                .collect(Collectors.toList());
        this.createdTime = LocalDateTime.now(); ; 
    }

   
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<InvoiceItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemDTO> items) {
        this.items = items;
    }

   public LocalDateTime getCreatedTime() {
        return  LocalDateTime.now();
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
