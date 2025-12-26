package org.agaafar.pharmacy.database.dao;

import org.agaafar.pharmacy.database.DBConnection;
import org.agaafar.pharmacy.database.models.Medicine;
import org.agaafar.pharmacy.database.models.OrderLineItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderLineItemDAO {

    public void insert(int orderId, OrderLineItem item) throws SQLException {

        String sql = """
            INSERT INTO order_line_item(order_id, medicine_id, quantity, price_at_sale, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, item.getMedicine().getId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPriceAtSale());
            ps.setDouble(5, item.getSubTotal());

            ps.executeUpdate();
        }
    }


    public void insertWithinTx(int orderId, OrderLineItem item, Connection conn) throws SQLException {

        String sql = """
            INSERT INTO order_line_item(order_id, medicine_id, quantity, price_at_sale, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, item.getMedicine().getId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPriceAtSale());
            ps.setDouble(5, item.getSubTotal());

            ps.executeUpdate();
        }
    }


    public List<OrderLineItem> getItemsByOrder(int orderId) throws SQLException {

        List<OrderLineItem> list = new ArrayList<>();

        String sql = """
            SELECT li.*, m.*
            FROM order_line_item li
            JOIN medicine m ON li.medicine_id = m.id
            WHERE li.order_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Medicine m = new Medicine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getBoolean("prescription_required")
                );

                OrderLineItem item = new OrderLineItem(
                        rs.getInt("id"),
                        m,
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_sale")
                );

                list.add(item);
            }
        }

        return list;
    }
}
