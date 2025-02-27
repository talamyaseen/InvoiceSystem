package io.invoice_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_history")
public class InvoiceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int invoiceId; 

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String changedByUserId; 

    @Column(nullable = false)
    private LocalDateTime changedAt;


    private Boolean isDeleted = false;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getInvoiceId() {
		return invoiceId;
	}


	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getChangedByUserId() {
		return changedByUserId;
	}


	public void setChangedByUserId(String changedByUserId) {
		this.changedByUserId = changedByUserId;
	}


	public LocalDateTime getChangedAt() {
		return changedAt;
	}


	public void setChangedAt(LocalDateTime changedAt) {
		this.changedAt = changedAt;
	}


	public Boolean getIsDeleted() {
		return isDeleted;
	}


	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
    
    
    
}
