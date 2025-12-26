package org.agaafar.pharmacy.database.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private int orderId;
    private LocalDate date;
    private String status;
    private Customer customer;
    private List<OrderLineItem> items = new ArrayList<>();

    public Order(int orderId, LocalDate date, String status, Customer customer) {
        this.orderId = orderId;
        this.date = date;
        this.status = status;
        this.customer = customer;
    }

    public int getOrderId() { return orderId; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public Customer getCustomer() { return customer; }

    public List<OrderLineItem> getItems() { return items; }

    public void addItem(Medicine med, int qty) {
        items.add(new OrderLineItem(0, med, qty, med.getPrice()));
    }

    public double calculateTotal() {
        return items.stream().mapToDouble(OrderLineItem::getSubTotal).sum();
    }
}
