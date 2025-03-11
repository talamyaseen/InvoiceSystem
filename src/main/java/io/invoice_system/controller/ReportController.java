package io.invoice_system.controller;

import io.invoice_system.service.ReportService;
import io.invoice_system.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

	 @PreAuthorize("hasAnyRole('SUPPORT_USER','AUDITOR')")
	 @GetMapping("/invoices")
	 public List<Object> getAllInvoices(
	     @RequestParam String startDate,
	     @RequestParam String endDate,
	     @RequestParam double totalAmount
	 ) {
	     return reportService.getAllInvoices(startDate, endDate, totalAmount);
	 }

}

