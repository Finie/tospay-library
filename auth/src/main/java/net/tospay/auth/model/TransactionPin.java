package net.tospay.auth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransactionPin implements Serializable {

    @SerializedName("pin_url")
    @Expose
    private String pinUrl;
    @SerializedName("pin_correlation_id")
    @Expose
    private String pinCorrelationId;
    @SerializedName("pin_set")
    @Expose
    private Boolean pinSet;
    @SerializedName("user_id")
    @Expose
    private String userId;

    public String getPinUrl() {
        return pinUrl;
    }

    public void setPinUrl(String pinUrl) {
        this.pinUrl = pinUrl;
    }

    public String getPinCorrelationId() {
        return pinCorrelationId;
    }

    public void setPinCorrelationId(String pinCorrelationId) {
        this.pinCorrelationId = pinCorrelationId;
    }

    public Boolean getPinSet() {
        return pinSet;
    }

    public void setPinSet(Boolean pinSet) {
        this.pinSet = pinSet;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
