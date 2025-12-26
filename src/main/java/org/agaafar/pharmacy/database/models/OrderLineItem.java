package org.agaafar.pharmacy.database.models;

public class OrderLineItem {

    private int id;
    private Medicine medicine;
    private int quantity;
    private double priceAtSale;

    public OrderLineItem(int id, Medicine medicine, int quantity, double priceAtSale) {
        this.id = id;
        this.medicine = medicine;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
    }

    public int getId() { return id; }
    public Medicine getMedicine() { return medicine; }
    public int getQuantity() { return quantity; }
    public double getPriceAtSale() { return priceAtSale; }

    public double getSubTotal() {
        return priceAtSale * quantity;
    }
}
