package com.soft.morales.mysmartwardrobe.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Look {

    // Look attributes variables
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("garment_id")
    @Expose
    private List<Integer> garment_id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("date")
    @Expose
    private String date;

    // Constructors

    public Look() {

    }

    public Look(String id, List<Integer> garment_id, String username, String date) {
        this.id = id;
        this.garment_id = garment_id;
        this.username = username;
        this.date = date;
    }

    public Look(List<Integer> garment_id, String username, String date) {
        this.garment_id = garment_id;
        this.username = username;
        this.date = date;
    }

    // Accessors

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Integer> getGarmentsIds() {
        return garment_id;
    }

    public void setGarment_id(List<Integer> garment_id) {
        this.garment_id = garment_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
