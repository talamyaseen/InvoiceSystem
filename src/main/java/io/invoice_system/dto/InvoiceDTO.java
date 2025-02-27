package io.invoice_system.dto;

import io.invoice_system.model.Invoice;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceDTO {
    private int id;
    private String status;
    private double totalAmount;
    private List<InvoiceItemDTO> items;
    public InvoiceDTO() {}

    public InvoiceDTO(Invoice invoice) {
    	this.id=invoice.getId();
        this.status = invoice.getStatus();
        this.totalAmount = invoice.getTotalAmount();
        this.items = invoice.getInvoiceItems()
                .stream()
                .map(InvoiceItemDTO::new) // Convert each InvoiceItem to DTO
                .collect(Collectors.toList());
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

}
