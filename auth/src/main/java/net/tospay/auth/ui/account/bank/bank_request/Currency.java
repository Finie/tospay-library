package net.tospay.auth.ui.account.bank.bank_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Currency implements Serializable {
    @SerializedName("iso")
    @Expose
    private String iso;
    @SerializedName("name")
    @Expose
    private String name;

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
