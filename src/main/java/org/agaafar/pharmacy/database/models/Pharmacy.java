package org.agaafar.pharmacy.database.models;

import java.util.ArrayList;
import java.util.List;

public class Pharmacy {

    private String name;
    private List<StockItem> inventory = new ArrayList<>();
    private List<Order> allOrders = new ArrayList<>();
    private List<Pharmacist> staff = new ArrayList<>();

    public void addStock(StockItem item) {
        inventory.add(item);
    }

    public StockItem findMedicine(String name) {
        return inventory.stream()
                .filter(i -> i.getMedicine().getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
