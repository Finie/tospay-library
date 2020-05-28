package net.tospay.auth.remote.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Bank;
import net.tospay.auth.model.Country;

import java.io.Serializable;

public class BankRequest implements Serializable {

    @SerializedName("country")
    @Expose
    private Country country;

    @SerializedName("bank")
    @Expose
    private Bank bank;

    @SerializedName("account_number")
    @Expose
    private String accountNumber;

    @SerializedName("phone")
    @Expose
    private String phone;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
