package net.tospay.auth.ui.account.bank.bank_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Bank implements Serializable {
    @SerializedName("account_alias")
    @Expose
    private String accountAlias;
    @SerializedName("account_name")
    @Expose
    private String accountName;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("bank_code")
    @Expose
    private String bankCode;
    @SerializedName("bank_abbrv")
    @Expose
    private String bankAbbrv;
    @SerializedName("branch_token")
    @Expose
    private String branchToken;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("currency")
    @Expose
    private Currency currency;
    @SerializedName("customer_number")
    @Expose
    private String customerNumber;
    @SerializedName("customer_phone")
    @Expose
    private String customerPhone;
    @SerializedName("signatories")
    @Expose
    private String signatories;

    public String getAccountAlias() {
        return accountAlias;
    }

    public void setAccountAlias(String accountAlias) {
        this.accountAlias = accountAlias;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAbbrv() {
        return bankAbbrv;
    }

    public void setBankAbbrv(String bankAbbrv) {
        this.bankAbbrv = bankAbbrv;
    }

    public String getBranchToken() {
        return branchToken;
    }

    public void setBranchToken(String branchToken) {
        this.branchToken = branchToken;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getSignatories() {
        return signatories;
    }

    public void setSignatories(String signatories) {
        this.signatories = signatories;
    }

}
