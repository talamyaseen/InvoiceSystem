package io.invoice_system.controller;

import io.invoice_system.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;
    
    @GetMapping("/items")
    public List<Object> getAllItems() {
    	List<Object> items = itemService.getAllItems();
    	 if (items == null || items.isEmpty()) {
             throw new NullPointerException("No items found in the system.");
         }
         return items;
    }
}
