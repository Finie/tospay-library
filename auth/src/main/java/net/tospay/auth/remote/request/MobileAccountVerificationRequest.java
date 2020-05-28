package net.tospay.auth.remote.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MobileAccountVerificationRequest {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("verification_code")
    @Expose
    private String verificationCode;

    public MobileAccountVerificationRequest(String id, String verificationCode) {
        this.id = id;
        this.verificationCode = verificationCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
