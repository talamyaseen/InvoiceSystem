package io.invoice_system.dto;

import io.invoice_system.model.InvoiceItem;

public class InvoiceItemDTO {
    private int itemId;
    private String itemName;
    private int quantity;
    public InvoiceItemDTO() {
    	
    }
    public InvoiceItemDTO(InvoiceItem invoiceItem) {

        this.itemId = invoiceItem.getId();
    	this.itemName = invoiceItem.getItem().getName();
        this.quantity = invoiceItem.getQuantity();
    }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public int getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
