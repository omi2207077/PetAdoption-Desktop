package com.example.pet_adoption_system;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class SceneLoader {

    public static void load(ActionEvent event, String fxmlFile, String title) {
        try {

            Parent root = FXMLLoader.load(SceneLoader.class.getResource("/" + fxmlFile));


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load FXML file: " + fxmlFile);
        }
    }
}