package com.example.pet_adoption_system;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    private DBHelper dbHelper;
    private AdminContract view;
    private File selectedImageFile;

    public interface AdminContract {
        void refreshPetList(List<PetModel> pets);
        void onMessage(String message);
        void onLogout();
        void updateBadge(int count);
    }

    public AdminController() {
        this.dbHelper = new DBHelper();
    }

    public void setView(AdminContract view) {
        this.view = view;

        updateBadge();
    }

    private void updateBadge() {
        if (view != null) {
            view.updateBadge(dbHelper.getPendingRequestCount());
        }
    }

    @FXML
    private void handleViewRequests(ActionEvent event) {
        ArrayList<AdoptionRequest> requests = dbHelper.getPendingRequests();

        if (requests == null || requests.isEmpty()) {
            if (view != null) view.onMessage("No pending requests");
            return;
        }


        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Pending Adoption Requests");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        for (AdoptionRequest req : requests) {
            VBox itemBox = new VBox(5);
            itemBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 1 0; -fx-padding: 10;");

            Label lbl = new Label(req.getUserName() + " wants to adopt " + req.getPetName());

            HBox btnBox = new HBox(10);
            Button btnApprove = new Button("Approve");
            btnApprove.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            Button btnReject = new Button("Reject");
            btnReject.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

            btnApprove.setOnAction(e -> {
                processRequest(req.getId(), true);
                layout.getChildren().remove(itemBox);
            });

            btnReject.setOnAction(e -> {
                processRequest(req.getId(), false);
                layout.getChildren().remove(itemBox);
            });

            btnBox.getChildren().addAll(btnApprove, btnReject);
            itemBox.getChildren().addAll(lbl, btnBox);
            layout.getChildren().add(itemBox);
        }

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        stage.setScene(new Scene(scroll, 400, 500));
        stage.show();
    }
    @FXML
    private VBox petListContainer;


    public void refreshPetList(List<PetModel> pets) {

        petListContainer.getChildren().clear();

        for (PetModel pet : pets) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("petadmin.fxml"));
                Node card = loader.load();


                PetItemController controller = loader.getController();
                controller.setData(pet);


                petListContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void loadPets() {
        ArrayList<PetModel> list = dbHelper.getAllPets();
        if (list == null) list = new ArrayList<>();
        if (view != null) {
            view.refreshPetList(list);
        }
    }

    public void processRequest(int requestId, boolean approved) {
        if (approved) {
            dbHelper.approveRequest(requestId);
            if (view != null) view.onMessage("Request Approved");
        } else {
            dbHelper.rejectRequest(requestId);
            if (view != null) view.onMessage("Request Rejected");
        }

        if (view != null) {
            view.updateBadge(dbHelper.getPendingRequestCount());
            loadPets();
        }
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        if (view != null) {
            view.onLogout();
        }
    }

    @FXML
    private void handleAddPet(javafx.event.ActionEvent event) {

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add New Pet");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TextField etName = new TextField();
        etName.setPromptText("Pet Name");

        TextArea etDesc = new TextArea();
        etDesc.setPromptText("Description");
        etDesc.setPrefRowCount(3);

        Button btnImg = new Button("Select Image");
        Label lblImg = new Label("No image selected");

        btnImg.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            selectedImageFile = chooser.showOpenDialog(stage);
            if (selectedImageFile != null) {
                lblImg.setText(selectedImageFile.getName());
            }
        });

        Button btnAdd = new Button("Add Pet");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        btnAdd.setOnAction(e -> {
            String name = etName.getText().trim();
            String desc = etDesc.getText().trim();

            if (name.isEmpty()) {
                view.onMessage("Pet name required");
                return;
            }

            String savedPath = "";
            if (selectedImageFile != null) {

                File destDir = new File("pet_images");
                savedPath = saveImage(selectedImageFile, destDir);
            }

            dbHelper.addPet(name, savedPath, desc);
            loadPets();
            stage.close();
            if (view != null) view.onMessage("Pet added successfully");
        });

        layout.getChildren().addAll(new Label("Enter Pet Details"), etName, etDesc, btnImg, lblImg, btnAdd);
        stage.setScene(new Scene(layout, 350, 400));
        stage.show();
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
}