package net.tospay.auth.ui.device_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationRes {
    private String status;
    @SerializedName("error")
    @Expose
    private Errors error;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("accuracy")
    @Expose
    private Double accuracy;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Errors getError() {
        return error;
    }

    public void setError(Errors error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
