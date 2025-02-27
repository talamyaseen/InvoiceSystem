package io.invoice_system.controller;

import io.invoice_system.model.Item;
import io.invoice_system.repository.GeneralRepository;
import io.invoice_system.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;
    


    // Endpoint to get all items
    @GetMapping("/items")
    @CrossOrigin(origins = "http://localhost:3000") 
    public List<Object> getAllItems() {
        return itemService.getAllItems();
    }
}
