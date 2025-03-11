package io.invoice_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.invoice_system.model.Invoice;
import io.invoice_system.repository.InvoiceRepository;

import java.util.List;

@Service
public class ReportService {
    @Autowired
    GeneralService generalService;

    public List<Object> getAllInvoices(String startDate, String endDate, double totalAmount) {
        String query = "SELECT users.id, users.name, users.city, users.street, IFNULL(SUM(invoices.total_amount), 0) AS total " +
                       "FROM users " +
                       "LEFT JOIN invoices ON users.id = invoices.user_id " +
                       "WHERE (" +
                           "(invoices.created_time BETWEEN '" + startDate + " 00:00:00' AND '" + endDate + " 23:59:59' AND " + totalAmount + " != 0) " + 
                           "OR " + totalAmount + " = 0) " +
                       "GROUP BY users.id " +
                       "HAVING  total >= " + totalAmount + " ;";

        return generalService.executeSql(query);
    }
}


