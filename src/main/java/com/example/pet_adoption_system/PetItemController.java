package com.example.pet_adoption_system;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class PetItemController {

    @FXML private Label tvPetName;
    @FXML private Label tvPetDes;
    @FXML private Label tvPetStatus;
    @FXML private Button btnDelete;

    private PetModel pet;


    public void setData(PetModel pet) {
        this.pet = pet;
        tvPetName.setText(pet.getName());
        tvPetDes.setText(pet.getDesc());

        if (pet.getAdopted() == 1) {
            tvPetStatus.setText("Adopted");
            tvPetStatus.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;"); // Red
        } else {
            tvPetStatus.setText("Available");
            tvPetStatus.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;"); // Green
        }
    }
}