package org.agaafar.pharmacy.database.dao;

import org.agaafar.pharmacy.database.DBConnection;
import org.agaafar.pharmacy.database.models.Customer;
import org.agaafar.pharmacy.database.models.Order;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int createOrder(Order o) throws SQLException {

        String sql = """
            INSERT INTO orders(customer_id, date, status, total_value)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, o.getCustomer().getCustomerId());
            ps.setString(2, o.getDate().toString());
            ps.setString(3, o.getStatus());
            ps.setDouble(4, o.calculateTotal());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        return -1;
    }

    public int createOrderWithinTx(Order o, Connection conn) throws SQLException {

        String sql = """
            INSERT INTO orders(customer_id, date, status, total_value)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, o.getCustomer().getCustomerId());
            ps.setString(2, o.getDate().toString());
            ps.setString(3, o.getStatus());
            ps.setDouble(4, o.calculateTotal());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        throw new SQLException("Failed to create order");
    }

    public void updateStatusWithinTx(int orderId, String status, Connection conn) throws SQLException {

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public List<Order> getAll() throws SQLException {

        List<Order> list = new ArrayList<>();

        String sql = """
            SELECT o.*, c.*
            FROM orders o
            JOIN customer c ON o.customer_id = c.customer_id
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Customer c = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone")
                );

                Order order = new Order(
                        rs.getInt("order_id"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("status"),
                        c
                );

                list.add(order);
            }
        }

        return list;
    }

    public void updateStatus(int orderId, String status) throws SQLException {

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }
}
