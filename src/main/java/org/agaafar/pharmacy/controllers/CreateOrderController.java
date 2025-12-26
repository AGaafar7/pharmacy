package org.agaafar.pharmacy.controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.agaafar.pharmacy.database.dao.CustomerDAO;
import org.agaafar.pharmacy.database.dao.MedicineDAO;
import org.agaafar.pharmacy.database.dao.OrderDAO;
import org.agaafar.pharmacy.database.dao.OrderLineItemDAO;
import org.agaafar.pharmacy.database.models.Customer;
import org.agaafar.pharmacy.database.models.Medicine;
import org.agaafar.pharmacy.database.models.Order;
import org.agaafar.pharmacy.database.models.OrderLineItem;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CreateOrderController {

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private ComboBox<Medicine> medicineComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<OrderLineItem> itemsTable;

    @FXML
    private TableColumn<OrderLineItem, String> medicineNameColumn;

    @FXML
    private TableColumn<OrderLineItem, Integer> quantityColumn;

    @FXML
    private TableColumn<OrderLineItem, Double> priceColumn;

    @FXML
    private TableColumn<OrderLineItem, Double> subtotalColumn;

    @FXML
    private Label totalLabel;

    private Order currentOrder;

    private CustomerDAO customerDAO;
    private MedicineDAO medicineDAO;
    private OrderDAO orderDAO;
    private OrderLineItemDAO orderLineItemDAO;

    public void initialize() {
        customerDAO = new CustomerDAO();
        medicineDAO = new MedicineDAO();
        orderDAO = new OrderDAO();
        orderLineItemDAO = new OrderLineItemDAO();

        try {
            List<Customer> customers = customerDAO.getAll();
            customerComboBox.getItems().setAll(customers);

            List<Medicine> medicines = medicineDAO.getAll();
            medicineComboBox.getItems().setAll(medicines);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }

        ColumnSetup(medicineNameColumn, quantityColumn, priceColumn, subtotalColumn);

        currentOrder = new Order(0, LocalDate.now(), "Pending", null);
    }

    static void ColumnSetup(TableColumn<OrderLineItem, String> medicineNameColumn, TableColumn<OrderLineItem, Integer> quantityColumn, TableColumn<OrderLineItem, Double> priceColumn, TableColumn<OrderLineItem, Double> subtotalColumn) {
        medicineNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMedicine().getName()));
        quantityColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        priceColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPriceAtSale()).asObject());
        subtotalColumn.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getSubTotal()).asObject());
    }

    @FXML
    private void onAddItem() {
        Medicine selectedMedicine = medicineComboBox.getSelectionModel().getSelectedItem();
        if (selectedMedicine == null) {
            showAlert("Warning", "Please select a medicine.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid quantity.");
            return;
        }

        currentOrder.addItem(selectedMedicine, qty);
        itemsTable.getItems().setAll(currentOrder.getItems());
        updateTotal();
        quantityField.clear();
    }

    @FXML
    private void onCreateOrder() {
        Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Warning", "Please select a customer.");
            return;
        }

        if (currentOrder.getItems().isEmpty()) {
            showAlert("Warning", "No items in the order.");
            return;
        }

        currentOrder = new Order(0, LocalDate.now(), "Pending", selectedCustomer);

        try {
            int orderId = orderDAO.createOrder(currentOrder);

            for (OrderLineItem item : currentOrder.getItems()) {
                orderLineItemDAO.insert(orderId, item);
            }

            showAlert("Success", "Order created successfully!");

            currentOrder = new Order(0, LocalDate.now(), "Pending", null);
            itemsTable.getItems().clear();
            totalLabel.setText("0.0");
        } catch (SQLException e) {
            showAlert("Error", "Failed to create order: " + e.getMessage());
        }
    }

    private void updateTotal() {
        totalLabel.setText(String.valueOf(currentOrder.calculateTotal()));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
