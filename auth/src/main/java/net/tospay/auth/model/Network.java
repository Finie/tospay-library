package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Network implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("operator")
    @Expose
    private String operator;

    @SerializedName("mnc")
    @Expose
    private String mnc;

    @SerializedName("mcc")
    @Expose
    private String mcc;

    @SerializedName("brand")
    @Expose
    private String brand;

    @SerializedName("type")
    @Expose
    private String type;

    public Network() {

    }

    protected Network(Parcel in) {
        id = in.readString();
        avatar = in.readString();
        operator = in.readString();
        mnc = in.readString();
        mcc = in.readString();
        brand = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(avatar);
        dest.writeString(operator);
        dest.writeString(mnc);
        dest.writeString(mcc);
        dest.writeString(brand);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Network> CREATOR = new Creator<Network>() {
        @Override
        public Network createFromParcel(Parcel in) {
            return new Network(in);
        }

        @Override
        public Network[] newArray(int size) {
            return new Network[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
