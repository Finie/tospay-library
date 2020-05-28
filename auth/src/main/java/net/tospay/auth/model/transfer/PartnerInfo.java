package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PartnerInfo implements Parcelable {

    @SerializedName("account")
    @Expose
    private Account account;

    @SerializedName("amount")
    @Expose
    private Amount amount;

    public PartnerInfo() {
    }

    protected PartnerInfo(Parcel in) {
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

    public static final Creator<PartnerInfo> CREATOR = new Creator<PartnerInfo>() {
        @Override
        public PartnerInfo createFromParcel(Parcel in) {
            return new PartnerInfo(in);
        }

        @Override
        public PartnerInfo[] newArray(int size) {
            return new PartnerInfo[size];
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
        return "PartnerInfo{" +
                "account=" + account +
                ", amount=" + amount +
                '}';
    }
}
