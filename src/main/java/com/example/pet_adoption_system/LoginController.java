package com.example.pet_adoption_system;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

import java.util.prefs.Preferences;

public class LoginController {

    @FXML private TextField etUser;
    @FXML private TextField etPass;
    @FXML private CheckBox switchRole;
    @FXML private ProgressIndicator progressBarLogin;
    @FXML private ImageView showHidePass;

    private DBHelper db;
    private Preferences prefs;

    @FXML
    public void initialize(ActionEvent event) {
        db = new DBHelper();
        prefs = Preferences.userRoot().node("session");


        String role = prefs.get("role", "");
        if (role.equals("admin")) {
            goToAdminPanel(event);
        } else if (role.equals("user")) {
            goToUserPanel(event);
        }

        // Switch change
        switchRole.selectedProperty().addListener((obs, oldVal, isAdmin) -> {
            if (isAdmin) {
                switchRole.setText("Admin Login");
                etUser.setPromptText("Admin Username");

            } else {
                switchRole.setText("User Login");
                etUser.setPromptText("Phone Number");

            }
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = etUser.getText().trim();
        String pass = etPass.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {

            showAlert("Validation Error", "Please fill in all fields!");
            return;
        }

        progressBarLogin.setVisible(true);

        if (switchRole.isSelected()) {

            if (db.adminLogin(user, pass)) {
                prefs.put("role", "admin");
                goToAdminPanel(event);
            } else {

                showAlert("Access Denied", "Invalid Admin credentials.");
            }
        } else {

            try {
                ResultSet c = db.loginUser(user, pass);

                if (c.next()) {

                    int userId = c.getInt(1);
                    String userName = c.getString(2);

                    prefs.put("role", "user");
                    prefs.putInt("user_id", userId);
                    prefs.put("name", userName);

                    goToUserPanel(event);
                } else {
                    showAlert("Error", "Invalid username or password");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Could not connect to the database.");
            }
        }

        progressBarLogin.setVisible(false);
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        goToSignup(event);
    }

    private void goToAdminPanel(ActionEvent event) {

        com.example.pet_adoption_system.SceneLoader.load(event, "AdminPanel.fxml", "Admin Dashboard");
    }

    private void goToUserPanel(ActionEvent event) {

        com.example.pet_adoption_system.SceneLoader.load(event, "UserPanel.fxml", "Pet Adoption - Dashboard");
    }

    private void goToSignup(ActionEvent event) {

        com.example.pet_adoption_system.SceneLoader.load(event, "UserSignup.fxml", "Create New Account");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}