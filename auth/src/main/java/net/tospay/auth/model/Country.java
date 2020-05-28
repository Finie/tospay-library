package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("iso")
    @Expose
    private String iso;

    @SerializedName("nicename")
    @Expose
    private String nicename;

    @SerializedName("iso3")
    @Expose
    private String iso3;

    @SerializedName("numcode")
    @Expose
    private String numcode;

    @SerializedName("phonecode")
    @Expose
    private String phonecode;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("name")
    @Expose
    private String name;

    public Country() {
    }

    protected Country(Parcel in) {
        id = in.readString();
        iso = in.readString();
        nicename = in.readString();
        iso3 = in.readString();
        numcode = in.readString();
        phonecode = in.readString();
        currency = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(iso);
        dest.writeString(nicename);
        dest.writeString(iso3);
        dest.writeString(numcode);
        dest.writeString(phonecode);
        dest.writeString(currency);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getNicename() {
        return nicename;
    }

    public void setNicename(String nicename) {
        this.nicename = nicename;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getNumcode() {
        return numcode;
    }

    public void setNumcode(String numcode) {
        this.numcode = numcode;
    }

    public String getPhonecode() {
        return phonecode;
    }

    public void setPhonecode(String phonecode) {
        this.phonecode = phonecode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}