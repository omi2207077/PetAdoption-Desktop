package com.example.pet_adoption_system;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class UserPanelController {

    private final DBHelper dbHelper;
    private final UserUIInterface view;
    private final Preferences prefs;


    public interface UserUIInterface {
        void refreshPetList(List<PetModel> pets);
        void updateWelcomeMessage(String name);
        void showNoData(boolean visible);
        void onLogout();
    }

    public UserPanelController(UserUIInterface view, DBHelper dbHelper) {
        this.view = view;
        this.dbHelper = dbHelper;

        this.prefs = Preferences.userNodeForPackage(UserPanelController.class);
    }


    public boolean checkUserSession() {
        String role = prefs.get("role", "");
        if (!"user".equals(role)) {
            return false;
        }

        String name = prefs.get("name", "User");
        view.updateWelcomeMessage(name);
        return true;
    }


    public void loadAvailablePets() {

        ArrayList<PetModel> allPets = dbHelper.getAllPets();
        ArrayList<PetModel> availablePets = new ArrayList<>();

        for (PetModel pet : allPets) {
            if (pet.getAdopted() == 0) {
                availablePets.add(pet);
            }
        }

        if (availablePets.isEmpty()) {
            view.showNoData(true);
        } else {
            view.showNoData(false);
            view.refreshPetList(availablePets);
        }
    }

    public int getLoggedInUserId() {
        return prefs.getInt("user_id", -1);
    }

    public void logout() {
        try {
            prefs.clear();
            view.onLogout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}