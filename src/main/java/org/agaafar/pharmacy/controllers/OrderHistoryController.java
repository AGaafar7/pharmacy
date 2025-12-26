package org.agaafar.pharmacy.controllers;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.agaafar.pharmacy.database.dao.OrderDAO;
import org.agaafar.pharmacy.database.dao.OrderLineItemDAO;
import org.agaafar.pharmacy.database.models.Order;
import org.agaafar.pharmacy.database.models.OrderLineItem;

import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderHistoryController {

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, String> dateColumn;

    @FXML
    private TableColumn<Order, String> customerColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, Double> totalColumn;

    @FXML
    private TableView<OrderLineItem> itemsTable;

    @FXML
    private TableColumn<OrderLineItem, String> medicineColumn;

    @FXML
    private TableColumn<OrderLineItem, Integer> qtyColumn;

    @FXML
    private TableColumn<OrderLineItem, Double> priceColumn;

    @FXML
    private TableColumn<OrderLineItem, Double> subtotalColumn;

    @FXML
    private Label totalLabel;

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderLineItemDAO lineItemDAO = new OrderLineItemDAO();

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {


        orderIdColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getOrderId()).asObject());

        dateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDate().format(fmt)));

        customerColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCustomer().getName()));

        statusColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStatus()));

        totalColumn.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().calculateTotal()).asObject());


        ordersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> loadOrderItems(newSel)
        );


        CreateOrderController.ColumnSetup(medicineColumn, qtyColumn, priceColumn, subtotalColumn);

        loadOrders();
    }

    @FXML
    private void onRefresh() {
        loadOrders();
        itemsTable.getItems().clear();
        totalLabel.setText("");
    }

    private void loadOrders() {
        try {
            List<Order> orders = orderDAO.getAll();
            ordersTable.getItems().setAll(orders);
        } catch (SQLException e) {
            showError("Failed to load orders: " + e.getMessage());
        }
    }

    private void loadOrderItems(Order order) {
        if (order == null) return;

        try {
            List<OrderLineItem> items = lineItemDAO.getItemsByOrder(order.getOrderId());
            itemsTable.getItems().setAll(items);
            totalLabel.setText(String.valueOf(order.calculateTotal()));

        } catch (SQLException e) {
            showError("Failed to load order items: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    @FXML
    private void onExport() {
        List<Order> orders = ordersTable.getItems();
        if (orders.isEmpty()) {
            showError("No orders to export.");
            return;
        }

        // You can use FileChooser to let the user pick a save location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Orders CSV");
        fileChooser.setInitialFileName("orders.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(ordersTable.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("Order ID,Date,Customer,Status,Total,Medicine,Quantity,Price,Subtotal");

                for (Order order : orders) {
                    List<OrderLineItem> items = lineItemDAO.getItemsByOrder(order.getOrderId());
                    if (items.isEmpty()) {
                        writer.printf("%d,%s,%s,%s,%.2f,,,,\n",
                                order.getOrderId(),
                                order.getDate().format(fmt),
                                order.getCustomer().getName(),
                                order.getStatus(),
                                order.calculateTotal());
                    } else {
                        for (OrderLineItem item : items) {
                            writer.printf("%d,%s,%s,%s,%.2f,%s,%d,%.2f,%.2f\n",
                                    order.getOrderId(),
                                    order.getDate().format(fmt),
                                    order.getCustomer().getName(),
                                    order.getStatus(),
                                    order.calculateTotal(),
                                    item.getMedicine().getName(),
                                    item.getQuantity(),
                                    item.getPriceAtSale(),
                                    item.getSubTotal());
                        }
                    }
                }
                showInfo("Export successful: " + file.getAbsolutePath());
            } catch (Exception e) {
                showError("Failed to export: " + e.getMessage());
            }
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
