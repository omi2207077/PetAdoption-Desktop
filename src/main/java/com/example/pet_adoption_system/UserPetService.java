package com.example.pet_adoption_system;

import java.util.List;


public class UserPetService {

    private final DBHelper db;
    private final UserActionInterface ui;


    public interface UserActionInterface {
        void showAdoptionDialog(PetModel pet, int userId);
        void showMessage(String message);
        void refreshList();
    }

    public UserPetService(DBHelper db, UserActionInterface ui) {
        this.db = db;
        this.ui = ui;
    }


    public void handleAdoptionClick(PetModel pet, int userId) {
        if (pet.getAdopted() == 1) {
            ui.showMessage("This pet is already adopted.");
            return;
        }

        if (userId == -1) {
            ui.showMessage("Session expired. Please login again.");
            return;
        }


        ui.showAdoptionDialog(pet, userId);
    }


    public void submitApplication(int userId, int petId, String reason) {
        try {

            db.addAdoptionRequest(userId, petId);
            ui.showMessage("Application submitted successfully!");
            ui.refreshList();
        } catch (Exception e) {
            ui.showMessage("Error: " + e.getMessage());
        }
    }
}