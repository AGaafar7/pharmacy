package org.agaafar.pharmacy.database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.agaafar.pharmacy.database.DBConnection;
import org.agaafar.pharmacy.database.models.Medicine;

public class MedicineDAO {

    public void insert(Medicine m) throws SQLException {
        String sql = "INSERT INTO medicine(name, brand, price, prescription_required) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getBrand());
            ps.setDouble(3, m.getPrice());
            ps.setBoolean(4, m.isPrescriptionRequired());
            ps.executeUpdate();
        }
    }

    public List<Medicine> getAll() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicine";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Medicine m = new Medicine(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getBoolean("prescription_required")
                );
                list.add(m);
            }
        }
        return list;
    }
}
