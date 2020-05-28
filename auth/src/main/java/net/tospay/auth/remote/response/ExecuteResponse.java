package net.tospay.auth.remote.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExecuteResponse implements Serializable {
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("pin_url")
    @Expose
    private String pinUrl;

    @SerializedName("html")
    @Expose
    private String html;

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("pin_correlation_id")
    @Expose
    private String pinCorrelationId;
    @SerializedName("pin_set")
    @Expose
    private Boolean pinSet;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPinUrl() {
        return pinUrl;
    }

    public void setPinUrl(String pinUrl) {
        this.pinUrl = pinUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
