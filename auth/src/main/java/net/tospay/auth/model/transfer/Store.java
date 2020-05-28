package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Store implements Parcelable {

    @SerializedName("account")
    @Expose
    private Account account;

    @SerializedName("charge")
    @Expose
    private Amount charge;

    @SerializedName("order")
    @Expose
    private Amount order;

    @SerializedName("total")
    @Expose
    private Amount total;

    @SerializedName("amount")
    @Expose
    private Amount amount;

    public Store() {
    }

    protected Store(Parcel in) {
        account = in.readParcelable(Account.class.getClassLoader());
        charge = in.readParcelable(Amount.class.getClassLoader());
        order = in.readParcelable(Amount.class.getClassLoader());
        total = in.readParcelable(Amount.class.getClassLoader());
        amount = in.readParcelable(Amount.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(account, flags);
        dest.writeParcelable(charge, flags);
        dest.writeParcelable(order, flags);
        dest.writeParcelable(total, flags);
        dest.writeParcelable(amount, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Amount getCharge() {
        return charge;
    }

    public void setCharge(Amount charge) {
        this.charge = charge;
    }

    public Amount getOrder() {
        return order;
    }

    public void setOrder(Amount order) {
        this.order = order;
    }

    public Amount getTotal() {
        return total;
    }

    public void setTotal(Amount total) {
        this.total = total;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Store)) {
            return false;
        }

        Store source = (Store) obj;
        return this.account.equals(source.account);
    }
}