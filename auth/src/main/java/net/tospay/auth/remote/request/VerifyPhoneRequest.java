package net.tospay.auth.remote.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyPhoneRequest {

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("verification_code")
    @Expose
    private String verificationCode;

    public VerifyPhoneRequest(String phone, String verificationCode) {
        this.phone = phone;
        this.verificationCode = verificationCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
