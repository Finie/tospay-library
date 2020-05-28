package net.tospay.auth.ui.account.bank.bank_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LinkBankReq implements Serializable {
    @SerializedName("bank")
    @Expose
    private Bank bank;

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

}
