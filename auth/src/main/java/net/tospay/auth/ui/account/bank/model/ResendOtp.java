package net.tospay.auth.ui.account.bank.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResendOtp {
    @SerializedName("bank_abbrv")
    @Expose
    private String bankAbbrv;
    @SerializedName("bank_account_token")
    @Expose
    private String bankAccountToken;

    public String getBankAbbrv() {
        return bankAbbrv;
    }

    public void setBankAbbrv(String bankAbbrv) {
        this.bankAbbrv = bankAbbrv;
    }

    public String getBankAccountToken() {
        return bankAccountToken;
    }

    public void setBankAccountToken(String bankAccountToken) {
        this.bankAccountToken = bankAccountToken;
    }
}
