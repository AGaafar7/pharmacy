package org.agaafar.pharmacy.controllers;

import org.agaafar.pharmacy.database.DBConnection;
import org.agaafar.pharmacy.database.dao.OrderDAO;
import org.agaafar.pharmacy.database.dao.OrderLineItemDAO;
import org.agaafar.pharmacy.database.dao.StockItemDAO;
import org.agaafar.pharmacy.database.models.Medicine;
import org.agaafar.pharmacy.database.models.Order;
import org.agaafar.pharmacy.database.models.OrderLineItem;
import org.agaafar.pharmacy.database.models.StockItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderLineItemDAO itemDAO = new OrderLineItemDAO();
    private final StockItemDAO stockDAO = new StockItemDAO();

    public void processOrder(Order order) throws SQLException {

        if (order.getCustomer() == null)
            throw new SQLException("Customer is required.");

        if (order.getItems().isEmpty())
            throw new SQLException("Order must have at least one item.");

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {


                int orderId = orderDAO.createOrderWithinTx(order, conn);


                for (OrderLineItem item : order.getItems()) {

                    Medicine med = item.getMedicine();
                    int requiredQty = item.getQuantity();


                    List<StockItem> batches =
                            stockDAO.getAvailableBatchesFIFO(med.getId(), conn);

                    int remaining = requiredQty;

                    for (StockItem batch : batches) {

                        if (remaining <= 0)
                            break;

                        int stockQty = batch.getQuantity();
                        int use = Math.min(stockQty, remaining);

                        int newQty = stockQty - use;


                        stockDAO.updateQuantityWithinTx(batch.getBatchId(), newQty, conn);

                        remaining -= use;
                    }

                    if (remaining > 0)
                        throw new SQLException("Insufficient stock for medicine: "
                                + med.getName());


                    itemDAO.insertWithinTx(orderId, item, conn);
                }

                conn.commit();

            } catch (Exception ex) {

                conn.rollback();
                throw new SQLException("Order failed: " + ex.getMessage(), ex);
            }
        }
    }
}
