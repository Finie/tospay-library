package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Amount implements Parcelable {

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("currency")
    @Expose
    private String currency;

    public Amount() {
    }

    public Amount(String amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    protected Amount(Parcel in) {
        amount = in.readString();
        currency = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(currency);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Amount> CREATOR = new Creator<Amount>() {
        @Override
        public Amount createFromParcel(Parcel in) {
            return new Amount(in);
        }

        @Override
        public Amount[] newArray(int size) {
            return new Amount[size];
        }
    };

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Amount{" +
                "amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
