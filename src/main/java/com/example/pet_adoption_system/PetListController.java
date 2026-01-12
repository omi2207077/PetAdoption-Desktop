package com.example.pet_adoption_system;

import java.util.ArrayList;
import java.util.List;


public class PetListController {

    private List<PetModel> petList;
    private final DBHelper db; // Your JDBC-based DBHelper
    private final PetUIInterface uiCallback;


    public interface PetUIInterface {
        void onListUpdated();
        void showConfirmDialog(String title, String message, Runnable onConfirm);
        void showError(String message);
        void showSuccess(String message);
    }

    public PetListController(List<PetModel> list, DBHelper db, PetUIInterface uiCallback) {
        this.petList = list;
        this.db = db;
        this.uiCallback = uiCallback;
    }


    public void deletePet(int position) {
        if (position < 0 || position >= petList.size()) return;

        PetModel petToDelete = petList.get(position);

        uiCallback.showConfirmDialog("Delete Post", "Are you sure you want to delete this?", () -> {
            try {

                boolean success = db.deletePetById(petToDelete.getId());

                if (success) {
                    petList.remove(position);
                    uiCallback.onListUpdated();
                    uiCallback.showSuccess("Pet Deleted");
                } else {
                    uiCallback.showError("Failed to delete pet from database.");
                }
            } catch (Exception e) {
                uiCallback.showError("Error deleting pet: " + e.getMessage());
            }
        });
    }


    public String getStatusText(int adoptedStatus) {
        return (adoptedStatus == 1) ? "Adopted" : "Available";
    }

    public String getStatusColor(int adoptedStatus) {

        return (adoptedStatus == 1) ? "#FF0000" : "#00FF00";
    }
}