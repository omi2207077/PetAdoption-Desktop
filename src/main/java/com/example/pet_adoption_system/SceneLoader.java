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
        Node node = (Node) event.getSource();
        loadFromNode(node, fxmlFile, title);
    }


    public static FXMLLoader loadFromNode(Node node, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource("/com/example/pet_adoption_system/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) node.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            return loader;
        } catch (IOException e) {
            System.out.println("Could not load FXML: " + fxmlFile);
            e.printStackTrace();
            return null;
        }
    }
}