package com.example.pet_adoption_system;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

import java.util.List;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML private TextField etUser;
    @FXML private TextField etPass;
    @FXML private ToggleButton switchRole;
    @FXML private ProgressIndicator progressBarLogin;
    @FXML private ImageView showHidePass;

    private DBHelper db;
    private Preferences prefs;


    @FXML
    public void initialize() {


        prefs = Preferences.userRoot().node("session");
        prefs.remove("role");

        db = new DBHelper();

        String role = prefs.get("role", "");
        if (!role.isEmpty()) {
            javafx.application.Platform.runLater(() -> {
                if (role.equals("admin")) {

                    setupAdminPanel(etUser);
                } else if (role.equals("user")) {
                    SceneLoader.loadFromNode(etUser, "UserPanel.fxml", "User Dashboard");
                }
            });
        }

        // Role Switch Listener
        switchRole.selectedProperty().addListener((obs, oldVal, isAdmin) -> {
            if (isAdmin) {
                switchRole.setText("Admin Login");
                etUser.setPromptText("Admin Username");
                etUser.clear();
            } else {
                switchRole.setText("User Login");
                etUser.setPromptText("Phone Number");
                etUser.clear();
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

            if (user.equals("Omi") && pass.equals("2207077")) {
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

        Node node = (Node) event.getSource();
        FXMLLoader loader = SceneLoader.loadFromNode(node, "AdminPanel.fxml", "Admin Dashboard");
        if (loader != null) {

            AdminController controller = loader.getController();


            controller.setView(new AdminController.AdminContract() {
                @Override
                public void onLogout() {

                    prefs.remove("role");
                    SceneLoader.loadFromNode(node, "Login.fxml", "Login");
                }

                @Override
                public void onMessage(String message) {
                    showAlert("Admin", message);
                }

                @Override
                public void refreshPetList(List<PetModel> pets) {
                }

                @Override
                public void updateBadge(int count) {
                }
            });
        }
    }
    private void setupAdminPanel(Node node) {

        FXMLLoader loader = SceneLoader.loadFromNode(node, "AdminPanel.fxml", "Admin Dashboard");

        if (loader != null) {
            AdminController controller = loader.getController();

            // 2. Set the View Contract
            controller.setView(new AdminController.AdminContract() {
                @Override
                public void onLogout() {
                    prefs.remove("role");
                    SceneLoader.loadFromNode(node, "Login.fxml", "Login");
                }

                @Override
                public void onMessage(String message) {
                    showAlert("Admin System", message);
                }

                @Override
                public void refreshPetList(List<PetModel> pets) {

                }

                @Override
                public void updateBadge(int count) {

                }
            });


            controller.loadPets();
        }
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