package org.agaafar.pharmacy.database.dao;

import org.agaafar.pharmacy.database.DBConnection;
import org.agaafar.pharmacy.database.models.Pharmacist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PharmacistDAO {

    public void insert(Pharmacist p) throws SQLException {
        String sql = "INSERT INTO pharmacist(name,phone) VALUES (?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getPhone());

            ps.executeUpdate();
        }
    }

    public void delete(int pharmacistId) throws SQLException {

        String sql = "DELETE FROM pharmacist WHERE pharmacist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pharmacistId);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Deleting pharmacist failed, no rows affected.");
            }
        }
    }


    public void update(Pharmacist p) throws SQLException {

        String sql = "UPDATE pharmacist SET name = ?, phone = ? WHERE pharmacist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getPhone());
            ps.setInt(3, p.getPharmacistId());

            int rows = ps.executeUpdate();  // returns number of affected rows

            if (rows == 0) {
                throw new SQLException("Updating pharmacist failed, no rows affected.");
            }
        }
    }


    public List<Pharmacist> getAll() throws SQLException {
        List<Pharmacist> list = new ArrayList<>();

        String sql = "SELECT * FROM pharmacist";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Pharmacist(
                        rs.getInt("pharmacist_id"),
                        rs.getString("name"),
                        rs.getString("phone")
                ));
            }
        }

        return list;
    }
}
