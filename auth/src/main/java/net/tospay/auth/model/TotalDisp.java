package net.tospay.auth.model;

import java.io.Serializable;

public class TotalDisp implements Serializable {
    private String currency,amount;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
