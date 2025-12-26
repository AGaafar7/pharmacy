package org.agaafar.pharmacy.database.dao;

import org.agaafar.pharmacy.database.DBConnection;
import org.agaafar.pharmacy.database.models.Medicine;
import org.agaafar.pharmacy.database.models.StockItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockItemDAO {

    // Insert a stock item
    public void insert(StockItem s) throws SQLException {
        String sql = "INSERT INTO stock_item(batch_id, medicine_id, quantity, expiration_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getBatchId());
            ps.setInt(2, s.getMedicine().getId());
            ps.setInt(3, s.getQuantity());
            ps.setString(4, s.getExpirationDate().toString());

            ps.executeUpdate();
        }
    }

    // Insert within existing transaction
    public void insertWithinTx(StockItem s, Connection conn) throws SQLException {
        String sql = "INSERT INTO stock_item(batch_id, medicine_id, quantity, expiration_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getBatchId());
            ps.setInt(2, s.getMedicine().getId());
            ps.setInt(3, s.getQuantity());
            ps.setString(4, s.getExpirationDate().toString());

            ps.executeUpdate();
        }
    }

    // Update quantity by batchId
    public void updateQuantity(int batchId, int newQty) throws SQLException {
        String sql = "UPDATE stock_item SET quantity = ? WHERE batch_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newQty);
            ps.setInt(2, batchId);
            ps.executeUpdate();
        }
    }

    public void updateQuantityWithinTx(int batchId, int newQty, Connection conn) throws SQLException {
        String sql = "UPDATE stock_item SET quantity = ? WHERE batch_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, batchId);
            ps.executeUpdate();
        }
    }

    // Get all stock items
    public List<StockItem> getAllStock() throws SQLException {
        String sql = """
            SELECT s.batch_id, s.medicine_id AS s_medicine_id, s.quantity, s.expiration_date,
                   m.id AS medicine_id, m.name AS medicine_name, m.brand, m.price, m.prescription_required
            FROM stock_item s
            JOIN medicine m ON s.medicine_id = m.id
        """;

        List<StockItem> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Medicine med = new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("prescription_required") == 1
                );

                StockItem item = new StockItem(
                        rs.getInt("batch_id"),
                        rs.getInt("quantity"),
                        LocalDate.parse(rs.getString("expiration_date")),
                        med
                );

                list.add(item);
            }
        }

        return list;
    }

    // Get stock by medicine
    public List<StockItem> getStockByMedicine(int medicineId) throws SQLException {
        String sql = """
            SELECT si.batch_id, si.medicine_id AS si_medicine_id, si.quantity, si.expiration_date,
                   m.id AS medicine_id, m.name AS medicine_name, m.brand, m.price, m.prescription_required
            FROM stock_item si
            JOIN medicine m ON si.medicine_id = m.id
            WHERE si.medicine_id = ?
        """;

        List<StockItem> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicineId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Medicine med = new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("prescription_required") == 1
                );

                StockItem s = new StockItem(
                        rs.getInt("batch_id"),
                        rs.getInt("quantity"),
                        LocalDate.parse(rs.getString("expiration_date")),
                        med
                );

                list.add(s);
            }
        }

        return list;
    }

    public List<Medicine> getAllMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT id, name, brand, price, prescription_required FROM medicine";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Medicine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("prescription_required") == 1
                ));
            }
        }
        return list;
    }

    public Medicine getMedicineById(int id) throws SQLException {
        String sql = "SELECT id, name, brand, price, prescription_required FROM medicine WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Medicine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("prescription_required") == 1
                );
            }
        }
        return null;
    }

    public Medicine getMedicineByName(String name) throws SQLException {
        String sql = "SELECT id, name, brand, price, prescription_required FROM medicine WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Medicine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("prescription_required") == 1
                );
            }
        }
        return null;
    }
    public Medicine insertMedicine(String name, String brand, double price, boolean prescription) throws SQLException {
        String sql = "INSERT INTO medicine(name, brand, price, prescription_required) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, brand);
            ps.setDouble(3, price);
            ps.setInt(4, prescription ? 1 : 0);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Medicine(id, name, brand, price, prescription);
            }
        }
        return null;
    }

    public StockItem getStockByBatch(int batchId) throws SQLException {
        String sql = """
        SELECT s.batch_id, s.quantity, s.expiration_date,
               m.id AS medicine_id, m.name AS medicine_name, m.brand, m.price, m.prescription_required
        FROM stock_item s
        JOIN medicine m ON s.medicine_id = m.id
        WHERE s.batch_id = ?
    """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, batchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Medicine med = new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("prescription_required") == 1
                );
                return new StockItem(
                        rs.getInt("batch_id"),
                        rs.getInt("quantity"),
                        LocalDate.parse(rs.getString("expiration_date")),
                        med
                );
            }
        }
        return null;
    }

    // Get available batches (FIFO)
    public List<StockItem> getAvailableBatchesFIFO(int medicineId, Connection conn) throws SQLException {
        String sql = """
            SELECT si.batch_id, si.medicine_id AS si_medicine_id, si.quantity, si.expiration_date,
                   m.id AS medicine_id, m.name AS medicine_name, m.brand, m.price, m.prescription_required
            FROM stock_item si
            JOIN medicine m ON si.medicine_id = m.id
            WHERE si.medicine_id = ?
              AND date(si.expiration_date) >= date('now')
              AND si.quantity > 0
            ORDER BY si.expiration_date ASC
        """;

        List<StockItem> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, medicineId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Medicine med = new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("prescription_required") == 1
                );

                StockItem s = new StockItem(
                        rs.getInt("batch_id"),
                        rs.getInt("quantity"),
                        LocalDate.parse(rs.getString("expiration_date")),
                        med
                );

                list.add(s);
            }
        }

        return list;
    }

    // Get expired stock
    public List<StockItem> getExpiredStock() throws SQLException {
        String sql = """
            SELECT si.batch_id, si.medicine_id AS si_medicine_id, si.quantity, si.expiration_date,
                   m.id AS medicine_id, m.name AS medicine_name, m.brand, m.price, m.prescription_required
            FROM stock_item si
            JOIN medicine m ON si.medicine_id = m.id
            WHERE date(si.expiration_date) < date('now')
        """;

        List<StockItem> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Medicine med = new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("prescription_required") == 1
                );

                StockItem s = new StockItem(
                        rs.getInt("batch_id"),
                        rs.getInt("quantity"),
                        LocalDate.parse(rs.getString("expiration_date")),
                        med
                );

                list.add(s);
            }
        }

        return list;
    }
}
