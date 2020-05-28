package net.tospay.auth.remote.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentValidationRequest {

    @SerializedName("token")
    @Expose
    private String token;

    public PaymentValidationRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
