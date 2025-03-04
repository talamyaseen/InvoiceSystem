package io.invoice_system.service;

import io.invoice_system.repository.GeneralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    GeneralRepository generalRepository;

    // Method to fetch all items
    public List<Object> getAllItems() {
        return generalRepository.executeSql("SELECT * FROM items");
    }
}
