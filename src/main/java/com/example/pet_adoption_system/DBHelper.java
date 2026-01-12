package com.example.pet_adoption_system;

import java.sql.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {

    private static final String DB_URL = "jdbc:sqlite:pet_adoption.db";

    public DBHelper() {
        initDatabase();
    }

    // Connect to the SQLite Database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initDatabase() {
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, password TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS admin(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS pets(id INTEGER PRIMARY KEY AUTOINCREMENT, pet_name TEXT, pet_image TEXT, pet_desc TEXT, is_adopted INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS adoption_requests(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, pet_id INTEGER, status TEXT)");


            String checkAdmin = "SELECT count(*) FROM admin WHERE username='Omi'";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO admin(username,password) VALUES('Omi','omi1436!!')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void rejectRequest(int requestId) {
        String sql = "UPDATE adoptions SET status = 'Rejected' WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error rejecting request: " + e.getMessage());
        }
    }
    public boolean deletePetById(int id) {

        String sql = "DELETE FROM pets WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setInt(1, id);


            int affectedRows = pstmt.executeUpdate();


            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            return false;
        }
    }
    public boolean isPhoneExists(String phone) {

        String sql = "SELECT count(*) FROM users WHERE phone = ?";


        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking phone existence: " + e.getMessage());
        }
        return false;
    }
    public boolean registerUser(String name, String phone, String password) {

        String sql = "INSERT INTO users (name, phone, password, role) VALUES (?, ?, ?, 'user')";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, password);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
    public boolean addAdoptionRequest(int userId, int petId) {

        String sql = "INSERT INTO adoptions (user_id, pet_id, status) VALUES (?, ?, 'Pending')";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, petId);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error adding adoption request: " + e.getMessage());
            return false;
        }
    }
    public ResultSet loginUser(String phone, String password) throws SQLException {

        String sql = "SELECT * FROM users WHERE phone = ? AND password = ?";

        Connection conn = this.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);


        pstmt.setString(1, phone);
        pstmt.setString(2, password);


        return pstmt.executeQuery();
    }


    public boolean adminLogin(String user, String pass) {
        String sql = "SELECT * FROM admin WHERE username=? AND password=?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }


    public void addPet(String name, String image, String desc) {
        String sql = "INSERT INTO pets(pet_name, pet_image, pet_desc, is_adopted) VALUES(?,?,?,0)";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, image);
            pstmt.setString(3, desc);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PetModel> getAllPets() {
        ArrayList<PetModel> list = new ArrayList<>();
        String sql = "SELECT * FROM pets";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PetModel(
                        rs.getInt("id"),
                        rs.getString("pet_name"),
                        rs.getString("pet_image"),
                        rs.getString("pet_desc"),
                        rs.getInt("is_adopted")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public ArrayList<AdoptionRequest> getPendingRequests() {
        ArrayList<AdoptionRequest> list = new ArrayList<>();
        String sql = "SELECT ar.id, ar.user_id, ar.pet_id, ar.status, u.name, p.pet_name " +
                "FROM adoption_requests ar " +
                "JOIN users u ON ar.user_id = u.id " +
                "JOIN pets p ON ar.pet_id = p.id " +
                "WHERE ar.status='Pending'";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AdoptionRequest(
                        rs.getInt(1), rs.getInt(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5), rs.getString(6)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void approveRequest(int requestId) {
        String updateReq = "UPDATE adoption_requests SET status='Approved' WHERE id=?";
        String findPet = "SELECT pet_id FROM adoption_requests WHERE id=?";
        String updatePet = "UPDATE pets SET is_adopted=1 WHERE id=?";

        try (Connection conn = this.connect()) {
            conn.setAutoCommit(false); // Transaction start
            try {

                PreparedStatement p1 = conn.prepareStatement(updateReq);
                p1.setInt(1, requestId);
                p1.executeUpdate();


                PreparedStatement p2 = conn.prepareStatement(findPet);
                p2.setInt(1, requestId);
                ResultSet rs = p2.executeQuery();
                if (rs.next()) {
                    int petId = rs.getInt(1);

                    PreparedStatement p3 = conn.prepareStatement(updatePet);
                    p3.setInt(1, petId);
                    p3.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPendingRequestCount() {
        String sql = "SELECT COUNT(*) FROM adoption_requests WHERE status='Pending'";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}