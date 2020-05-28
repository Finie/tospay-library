package net.tospay.auth.remote.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;

public class MobileRequest {

    @SerializedName("country")
    @Expose
    private Country country;

    @SerializedName("network")
    @Expose
    private Network network;

    @SerializedName("number")
    @Expose
    private String number;

    @SerializedName("alias")
    @Expose
    private String alias;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
