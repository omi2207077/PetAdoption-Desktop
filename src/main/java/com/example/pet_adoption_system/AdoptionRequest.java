package com.example.pet_adoption_system;


public class AdoptionRequest {
    private int id;
    private int userId;
    private int petId;
    private String status;
    private String userName;
    private String petName;


    public AdoptionRequest(int id, int userId, int petId, String status, String userName, String petName) {
        this.id = id;
        this.userId = userId;
        this.petId = petId;
        this.status = status;
        this.userName = userName;
        this.petName = petName;
    }


    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getPetId() { return petId; }
    public String getStatus() { return status; }
    public String getUserName() { return userName; }
    public String getPetName() { return petName; }


    public void setStatus(String status) { this.status = status; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPetName(String petName) { this.petName = petName; }


    @Override
    public String toString() {
        return "AdoptionRequest{" +
                "id=" + id +
                ", user='" + userName + '\'' +
                ", pet='" + petName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}