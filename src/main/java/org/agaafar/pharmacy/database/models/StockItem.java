package org.agaafar.pharmacy.database.models;

import java.time.LocalDate;

public class StockItem {

    private int batchId;
    private int quantity;
    private LocalDate expirationDate;
    private Medicine medicine;

    public StockItem(int batchId, int quantity, LocalDate expirationDate, Medicine medicine) {
        this.batchId = batchId;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.medicine = medicine;
    }

    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }

    public boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }

    public void updateQuantity(int qty) {
        this.quantity += qty;
    }
}
