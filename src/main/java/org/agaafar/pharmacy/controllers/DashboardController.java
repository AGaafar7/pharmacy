package org.agaafar.pharmacy.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML
    private Button inventoryBtn;

    @FXML
    private Button customerBtn;

    @FXML
    private Button createBtn;

    @FXML
    private Button historyBtn;

    @FXML
    private Button pharmacistBtn;

    private void openScreen(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onInventoryClicked() {
        openScreen("/org/agaafar/pharmacy/inventory.fxml", "Inventory");
    }

    @FXML
    private void onCustomerClicked() {
        openScreen("/org/agaafar/pharmacy/customer.fxml", "Customers");
    }

    @FXML
    private void onCreateOrderClicked() {
        openScreen("/org/agaafar/pharmacy/createorder.fxml", "Create Order");
    }

    @FXML
    private void onOrderHistoryClicked() {
        openScreen("/org/agaafar/pharmacy/orderhistory.fxml", "Order History");
    }

    @FXML
    private void onPharmacistClicked() {
        openScreen("/org/agaafar/pharmacy/pharmacist.fxml", "Pharmacists");
    }

}
