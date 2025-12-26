package org.agaafar.pharmacy.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.agaafar.pharmacy.database.dao.PharmacistDAO;
import org.agaafar.pharmacy.database.models.Pharmacist;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PharmacistController {

    @FXML
    private TableView<Pharmacist> pharmacistTable;

    @FXML
    private TableColumn<Pharmacist, Integer> idColumn;

    @FXML
    private TableColumn<Pharmacist, String> nameColumn;

    @FXML
    private TableColumn<Pharmacist, String> phoneColumn;


    private PharmacistDAO pharmacistDAO;

    @FXML
    public void initialize() {

        pharmacistDAO = new PharmacistDAO();

        idColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getPharmacistId()).asObject());

        nameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getName()));
        phoneColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getPhone()));

        loadPharmacists();
    }

    @FXML
    private void onRefresh() {
        loadPharmacists();
    }

    private void loadPharmacists() {
        try {
            List<Pharmacist> list = pharmacistDAO.getAll();
            pharmacistTable.getItems().setAll(list);
        } catch (SQLException e) {
            showError("Failed to load pharmacists: " + e.getMessage());
        }
    }

    @FXML
    private void onAddPharmacist() {

        Dialog<Pharmacist> dialog = new Dialog<>();
        dialog.setTitle("Add Pharmacist");

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

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                return new Pharmacist(0, nameField.getText(), phoneField.getText());
            }
            return null;
        });

        Optional<Pharmacist> result = dialog.showAndWait();

        result.ifPresent(p -> {
            try {
                pharmacistDAO.insert(p);
                loadPharmacists();
            } catch (SQLException e) {
                showError("Failed to add pharmacist: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onUpdatePharmacist() {

        Pharmacist selected = pharmacistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a pharmacist first.");
            return;
        }

        Dialog<Pharmacist> dialog = new Dialog<>();
        dialog.setTitle("Update Pharmacist");

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

        ButtonType updateBtn = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == updateBtn) {
                selected.setName(nameField.getText());
                selected.setPhone(phoneField.getText());

                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            try {
                pharmacistDAO.update(p);
                loadPharmacists();
            } catch (SQLException e) {
                showError("Failed to update pharmacist: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onDeletePharmacist() {

        Pharmacist selected = pharmacistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a pharmacist first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Pharmacist");
        confirm.setHeaderText("Delete " + selected.getName() + "?");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    pharmacistDAO.delete(selected.getPharmacistId());
                    loadPharmacists();
                } catch (SQLException e) {
                    showError("Failed to delete pharmacist: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Warning");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}

