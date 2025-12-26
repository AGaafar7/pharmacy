package org.agaafar.pharmacy.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.agaafar.pharmacy.database.dao.CustomerDAO;
import org.agaafar.pharmacy.database.models.Customer;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.*;

public class CustomerController {

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> idColumn;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> phoneColumn;

    private CustomerDAO customerDAO;

    public void initialize() {
        customerDAO = new CustomerDAO();

        idColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCustomerId()).asObject());
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        phoneColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhone()));

        loadCustomers();
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.getAll();
            customerTable.getItems().setAll(customers);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load customers: " + e.getMessage());
        }
    }

    @FXML
    private void onAddCustomer() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == addButton) {
                return new Customer(0, nameField.getText(), phoneField.getText());
            }
            return null;
        });

        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(c -> {
            try {
                customerDAO.insert(c);
                loadCustomers();
            } catch (SQLException e) {
                showAlert("Error", "Failed to add customer: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onUpdateCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a customer to update.");
            return;
        }

        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Update Customer");

        TextField nameField = new TextField(selected.getName());
        TextField phoneField = new TextField(selected.getPhone());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == updateButton) {
                selected.setName(nameField.getText());
                selected.setPhone(phoneField.getText());
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(c -> {
            try {
                customerDAO.update(c);
                loadCustomers();
            } catch (SQLException e) {
                showAlert("Error", "Failed to update customer: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onDeleteCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a customer to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Customer");
        confirm.setHeaderText("Are you sure you want to delete " + selected.getName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    customerDAO.delete(selected.getCustomerId());
                    loadCustomers();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete customer: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
