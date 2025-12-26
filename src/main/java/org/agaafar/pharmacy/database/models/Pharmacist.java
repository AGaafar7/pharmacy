package org.agaafar.pharmacy.database.models;

public class Pharmacist {

    private int pharmacistId;
    private String name;
    private String phone;

    public Pharmacist(int pharmacistId, String name, String phone) {
        this.pharmacistId = pharmacistId;
        this.name = name;
        this.phone = phone;
    }

    public int getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(int pharmacistId) { this.pharmacistId = pharmacistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean verifyPrescription(Medicine m) {
        return m.isPrescriptionRequired();
    }
}
