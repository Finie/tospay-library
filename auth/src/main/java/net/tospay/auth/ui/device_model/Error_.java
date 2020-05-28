package net.tospay.auth.ui.device_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error_ {
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("message")
    @Expose
    private String message;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
