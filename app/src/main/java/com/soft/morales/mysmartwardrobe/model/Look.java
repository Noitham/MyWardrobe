package com.soft.morales.mysmartwardrobe.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Look {

    // Look attributes variables
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("torso_id")
    @Expose
    private Integer torso_id;
    @SerializedName("piernas_id")
    @Expose
    private Integer piernas_id;
    @SerializedName("pies_id")
    @Expose
    private Integer pies_id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("date")
    @Expose
    private String date;

    // Constructors

    public Look() {

    }

    public Look(String id, Integer torso_id, Integer piernas_id, Integer pies_id, String username, String date) {
        this.id = id;
        this.torso_id = torso_id;
        this.piernas_id = piernas_id;
        this.pies_id = pies_id;
        this.username = username;
        this.date = date;
    }

    public Look(Integer torso_id, Integer piernas_id, Integer pies_id, String username, String date) {
        this.torso_id = torso_id;
        this.piernas_id = piernas_id;
        this.pies_id = pies_id;
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

    public Integer getTorso_id() {
        return torso_id;
    }

    public void setTorso_id(Integer torso_id) {
        this.torso_id = torso_id;
    }

    public Integer getPiernas_id() {
        return piernas_id;
    }

    public void setPiernas_id(Integer piernas_id) {
        this.piernas_id = piernas_id;
    }

    public Integer getPies_id() {
        return pies_id;
    }

    public void setPies_id(Integer pies_id) {
        this.pies_id = pies_id;
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
