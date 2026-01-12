package com.example.pet_adoption_system;


public class PetModel {


    private int id;
    private int adopted;
    private String name;
    private String image;
    private String desc;

    public PetModel(int id, String name, String image, String desc, int adopted) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.adopted = adopted;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public int getAdopted() {
        return adopted;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setAdopted(int adopted) {
        this.adopted = adopted;
    }


    public boolean isAvailable() {
        return this.adopted == 0;
    }

    @Override
    public String toString() {
        return "PetModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + (adopted == 1 ? "Adopted" : "Available") +
                '}';
    }
}