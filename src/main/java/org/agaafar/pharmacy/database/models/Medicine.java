package org.agaafar.pharmacy.database.models;

public class Medicine {

    private int id;
    private String name;
    private String brand;
    private double price;
    private boolean prescriptionRequired;

    public Medicine(int id, String name, String brand, double price, boolean prescriptionRequired) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.prescriptionRequired = prescriptionRequired;
    }

    public Medicine() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean prescriptionRequired) { this.prescriptionRequired = prescriptionRequired; }

    public String getDetails() {
        return name + " (" + brand + ")";
    }
    @Override
    public String toString() {
        return name;
    }

}
