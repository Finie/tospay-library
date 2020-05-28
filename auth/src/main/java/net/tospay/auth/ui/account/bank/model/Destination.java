package net.tospay.auth.ui.account.bank.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Destination implements Serializable {

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("amount")
    @Expose
    private Double amount;

    public String getCurrency() {
        return currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
