package com.soft.morales.mysmartwardrobe.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Garment {

    // Garment attributes variables
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("season")
    @Expose
    private String season;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("brand")
    @Expose
    private String brand;

    // Constructors
    public Garment(Garment garment) {
        this.id = garment.id;
        this.name = garment.name;
        this.photo = garment.photo;
        this.category = garment.category;
        this.season = garment.season;
        this.price = garment.price;
        this.color = garment.color;
        this.size = garment.size;
        this.brand = garment.brand;
    }

    public Garment(String name, String photo, String category, String brand) {
        this.name = name;
        this.photo = photo;
        this.category = category;
        this.brand = brand;
    }

    // Accessors

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

}