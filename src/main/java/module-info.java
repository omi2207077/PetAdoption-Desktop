
module com.example.pet_adoption_system {

        requires java.sql;


        requires javafx.controls;
        requires javafx.fxml;

        requires java.prefs;
        requires java.desktop;
        opens com.example.pet_adoption_system to javafx.fxml;
        exports com.example.pet_adoption_system;
        }