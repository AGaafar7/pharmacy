package org.agaafar.pharmacy.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.agaafar.pharmacy.database.dao.StockItemDAO;
import org.agaafar.pharmacy.database.models.Medicine;
import org.agaafar.pharmacy.database.models.StockItem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class InventoryController {

    @FXML
    private TableView<StockItem> stockTable;



    @FXML
    private TableColumn<StockItem, String> medicineNameColumn;

    @FXML
    private TableColumn<StockItem, String> batchIdColumn;

    @FXML
    private TableColumn<StockItem, String> quantityColumn;

    @FXML
    private TableColumn<StockItem, String> expirationColumn;


    private StockItemDAO stockItemDAO;

    public void initialize() {
        stockItemDAO = new StockItemDAO();

        batchIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getBatchId()))
        );

        quantityColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getQuantity()))
        );

        expirationColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getExpirationDate().toString())
        );

        medicineNameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getMedicine() != null
                                ? cell.getValue().getMedicine().getName()
                                : "Unknown"
                )
        );

        loadStockItems();
    }

    private void loadStockItems() {
        try {

            List<StockItem> items = stockItemDAO.getAllStock();
            stockTable.getItems().setAll(items);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load stock items: " + e.getMessage());
        }
    }


    @FXML
    private void onAddStockClicked() {
        Dialog<StockItem> dialog = new Dialog<>();
        dialog.setTitle("Add New Stock");
        dialog.setHeaderText("Enter stock details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField medicineNameField = new TextField();
        medicineNameField.setPromptText("Medicine Name");

        TextField brandField = new TextField();
        brandField.setPromptText("Brand (if new)");

        TextField priceField = new TextField();
        priceField.setPromptText("Price (if new)");

        CheckBox prescriptionRequiredBox = new CheckBox("Prescription Required");

        TextField batchField = new TextField();
        batchField.setPromptText("Batch ID");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        DatePicker expirationPicker = new DatePicker();
        expirationPicker.setPromptText("Expiration Date");

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Medicine Name:"), 0, 0);
        grid.add(medicineNameField, 1, 0);

        grid.add(new Label("Brand:"), 0, 1);
        grid.add(brandField, 1, 1);

        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);

        grid.add(prescriptionRequiredBox, 1, 3);

        grid.add(new Label("Batch ID:"), 0, 4);
        grid.add(batchField, 1, 4);

        grid.add(new Label("Quantity:"), 0, 5);
        grid.add(quantityField, 1, 5);

        grid.add(new Label("Expiration Date:"), 0, 6);
        grid.add(expirationPicker, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String medName = medicineNameField.getText().trim();
                    String brand = brandField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    boolean prescription = prescriptionRequiredBox.isSelected();
                    int batchId = Integer.parseInt(batchField.getText().trim());
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    LocalDate expiration = expirationPicker.getValue();

                    // Check if medicine exists
                    Medicine med = stockItemDAO.getMedicineByName(medName);

                    if (med == null) {
                        // Create new medicine
                        med = stockItemDAO.insertMedicine(medName, brand, price, prescription);
                    }

                    // Check if batch already exists for this medicine
                    StockItem existingStock = stockItemDAO.getStockByBatch(batchId);
                    if (existingStock != null) {
                        // Batch exists, update quantity
                        stockItemDAO.updateQuantity(batchId, existingStock.getQuantity() + quantity);
                        return null; // No need to add new StockItem object
                    } else {
                        // Create new stock item
                        return new StockItem(batchId, quantity, expiration, med);
                    }

                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid number input.");
                } catch (SQLException e) {
                    showAlert("Error", "Database error: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(stockItem -> {
            if (stockItem != null) {
                try {
                    stockItemDAO.insert(stockItem);
                    loadStockItems();
                    showAlert("Success", "Stock added successfully!");
                } catch (SQLException e) {
                    showAlert("Error", "Failed to add stock: " + e.getMessage());
                }
            } else {
                // Already updated quantity if batch existed
                loadStockItems();
            }
        });
    }


    @FXML
    private void onUpdateQuantityClicked() {
        StockItem selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getQuantity()));
            dialog.setTitle("Update Quantity");
            dialog.setHeaderText("Update quantity for batch " + selected.getBatchId());
            dialog.setContentText("New quantity:");

            dialog.showAndWait().ifPresent(input -> {
                try {
                    int newQty = Integer.parseInt(input);
                    stockItemDAO.updateQuantity(selected.getBatchId(), newQty);
                    loadStockItems();
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid number.");
                } catch (SQLException e) {
                    showAlert("Error", "Failed to update quantity: " + e.getMessage());
                }
            });
        } else {
            showAlert("Warning", "Select a stock item first.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
