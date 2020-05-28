package net.tospay.auth.ui.account.bank.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Branch {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("branchName")
    @Expose
    private String branchName;
    @SerializedName("branchCode")
    @Expose
    private String branchCode;
    @SerializedName("status")
    @Expose
    private String status;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
