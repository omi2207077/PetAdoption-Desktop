package com.example.pet_adoption_system;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    private final DBHelper dbHelper;
    private final AdminContract view;


    public interface AdminContract {
        void refreshPetList(List<PetModel> pets);
        void onMessage(String message);
        void onLogout();
        void updateBadge(int count);
    }

    public AdminController(AdminContract view, DBHelper dbHelper) {
        this.view = view;
        this.dbHelper = dbHelper;
    }


    public void loadPets() {
        ArrayList<PetModel> list = dbHelper.getAllPets();
        if (list == null) list = new ArrayList<>();
        view.refreshPetList(list);
    }


    public void addPet(String name, String desc, File imageFile, File targetDir) {
        if (name == null || name.isEmpty()) {
            view.onMessage("Pet name required");
            return;
        }

        String savedPath = "";
        if (imageFile != null && imageFile.exists()) {
            savedPath = saveImage(imageFile, targetDir);
        }

        dbHelper.addPet(name, savedPath, desc);
        loadPets();
        view.onMessage("Pet added successfully");
    }


    public void handleRequest(int requestId, int petId, boolean approved) {
        if (approved) {
            dbHelper.approveRequest(requestId);
            view.onMessage("Request Approved");
        } else {
            dbHelper.rejectRequest(requestId);
            view.onMessage("Request Rejected");
        }


        view.updateBadge(dbHelper.getPendingRequestCount());
        loadPets();
    }


    private String saveImage(File sourceFile, File destDir) {
        if (!destDir.exists()) destDir.mkdirs();

        File destFile = new File(destDir, "pet_" + System.currentTimeMillis() + ".jpg");

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destFile)) {

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void logout() {

        view.onLogout();
    }
}