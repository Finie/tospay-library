package net.tospay.auth.ui.account.bank.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FxRequest implements Serializable {
    @SerializedName("origin")
    @Expose
    private Origin origin;
    @SerializedName("destination")
    @Expose
    private Destination destination;

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

}
