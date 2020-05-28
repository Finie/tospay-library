package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceInfo implements Parcelable {

    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("ip")
    @Expose
    private List<String> ip = null;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
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

    @SerializedName("locationAccuracy")
    @Expose
    private String location_accuracy;


    public DeviceInfo() {
    }

    public DeviceInfo(Parcel in) {
        imei = in.readString();
        ip = in.createStringArrayList();
        latitude = in.readString();
        longitude = in.readString();
        networkCountryIso = in.readString();
        osVersion = in.readString();
        phone = in.readString();
        phoneSerial = in.readString();
        simCardSerial = in.readString();
        userAgent = in.readString();
        location_accuracy = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imei);
        dest.writeStringList(ip);
        dest.writeString(longitude);
        dest.writeString(longitude);
        dest.writeString(networkCountryIso);
        dest.writeString(osVersion);
        dest.writeString(phone);
        dest.writeString(phoneSerial);
        dest.writeString(simCardSerial);
        dest.writeString(userAgent);
        dest.writeString(location_accuracy);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel in) {
            return new DeviceInfo(in);
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getLocation_accuracy() {
        return location_accuracy;
    }

    public void setLocation_accuracy(String location_accuracy) {
        this.location_accuracy = location_accuracy;
    }
}
