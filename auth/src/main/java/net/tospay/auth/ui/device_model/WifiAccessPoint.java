package net.tospay.auth.ui.device_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WifiAccessPoint {

    @SerializedName("macAddress")
    @Expose
    private String macAddress;
    @SerializedName("signalStrength")
    @Expose
    private String signalStrength;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getSignalToNoiseRatio() {
        return signalToNoiseRatio;
    }

    public void setSignalToNoiseRatio(String signalToNoiseRatio) {
        this.signalToNoiseRatio = signalToNoiseRatio;
    }

    @SerializedName("signalToNoiseRatio")
    @Expose
    private String signalToNoiseRatio;
    @SerializedName("cellId")
    @Expose
    private Integer cellId;
    @SerializedName("locationAreaCode")
    @Expose
    private Integer locationAreaCode;
    @SerializedName("mobileCountryCode")
    @Expose
    private Integer mobileCountryCode;
    @SerializedName("mobileNetworkCode")
    @Expose
    private Integer mobileNetworkCode;

    public Integer getCellId() {
        return cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    public Integer getLocationAreaCode() {
        return locationAreaCode;
    }

    public void setLocationAreaCode(Integer locationAreaCode) {
        this.locationAreaCode = locationAreaCode;
    }

    public Integer getMobileCountryCode() {
        return mobileCountryCode;
    }

    public void setMobileCountryCode(Integer mobileCountryCode) {
        this.mobileCountryCode = mobileCountryCode;
    }

    public Integer getMobileNetworkCode() {
        return mobileNetworkCode;
    }

    public void setMobileNetworkCode(Integer mobileNetworkCode) {
        this.mobileNetworkCode = mobileNetworkCode;
    }


}
