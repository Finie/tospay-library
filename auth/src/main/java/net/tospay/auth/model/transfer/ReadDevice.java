package net.tospay.auth.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReadDevice implements Serializable {

    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("ip")
    @Expose
    private List<String> ip = null;
    @SerializedName("networkCountryIso")
    @Expose
    private String networkCountryIso;
    @SerializedName("osVersion")
    @Expose
    private String osVersion;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("phoneSerial")
    @Expose
    private String phoneSerial;
    @SerializedName("simCardSerial")
    @Expose
    private String simCardSerial;
    @SerializedName("userAgent")
    @Expose
    private String userAgent;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<String> getIp() {
        return ip;
    }

    public void setIp(List<String> ip) {
        this.ip = ip;
    }

    public String getNetworkCountryIso() {
        return networkCountryIso;
    }

    public void setNetworkCountryIso(String networkCountryIso) {
        this.networkCountryIso = networkCountryIso;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneSerial() {
        return phoneSerial;
    }

    public void setPhoneSerial(String phoneSerial) {
        this.phoneSerial = phoneSerial;
    }

    public String getSimCardSerial() {
        return simCardSerial;
    }

    public void setSimCardSerial(String simCardSerial) {
        this.simCardSerial = simCardSerial;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
