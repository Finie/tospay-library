package net.tospay.auth.model.transfer;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RailInfo implements Parcelable {

    @SerializedName("account")
    @Expose
    private Account account;

    @SerializedName("amount")
    @Expose
    private Amount amount;

    public RailInfo() {
    }

    protected RailInfo(Parcel in) {
        account = in.readParcelable(Account.class.getClassLoader());
        amount = in.readParcelable(Amount.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(account, flags);
        dest.writeParcelable(amount, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RailInfo> CREATOR = new Creator<RailInfo>() {
        @Override
        public RailInfo createFromParcel(Parcel in) {
            return new RailInfo(in);
        }

        @Override
        public RailInfo[] newArray(int size) {
            return new RailInfo[size];
        }
    };

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "RailInfo{" +
                "account=" + account +
                ", amount=" + amount +
                '}';
    }
}
