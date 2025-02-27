package io.invoice_system.service;

import io.invoice_system.model.Item;
import io.invoice_system.repository.GeneralRepository;
import io.invoice_system.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    GeneralRepository generalRepository;


    @Autowired
    private ItemRepository itemRepository;

    // Method to fetch all items
    public List<Object> getAllItems() {
        return generalRepository.executeSql("SELECT * FROM items");
    }
}
