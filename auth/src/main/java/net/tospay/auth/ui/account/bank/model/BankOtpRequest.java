package net.tospay.auth.ui.account.bank.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BankOtpRequest implements Serializable {
    @SerializedName("bank_account_token")
    @Expose
    private String bankAccountToken;
    @SerializedName("bank_abbrv")
    @Expose
    private String bankCode;
    @SerializedName("token")
    @Expose
    private String token;

    public String getBankAccountToken() {
        return bankAccountToken;
    }

    public void setBankAccountToken(String bankAccountToken) {
        this.bankAccountToken = bankAccountToken;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
