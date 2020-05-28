package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.interfaces.AccountType;

public class Account implements Parcelable, AccountType {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("network")
    @Expose
    private String network;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("trunc")
    @Expose
    private String trunc;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("note")
    @Expose
    private String note;

    @SerializedName("alias")
    @Expose
    private String alias;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("verified")
    @Expose
    private boolean verified;

    private String name;
    private int accountType;
    private double withdrawalAmount = 0.00;
    private boolean collapsed = false;
    private boolean checked = false;

    public Account() {
    }

    protected Account(Parcel in) {
        id = in.readString();
        network = in.readString();
        avatar = in.readString();
        trunc = in.readString();
        state = in.readString();
        note = in.readString();
        alias = in.readString();
        currency = in.readString();
        verified = in.readByte() != 0;
        accountType = in.readInt();
        withdrawalAmount = in.readDouble();
        collapsed = in.readByte() != 0;
        checked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(network);
        dest.writeString(avatar);
        dest.writeString(trunc);
        dest.writeString(state);
        dest.writeString(note);
        dest.writeString(alias);
        dest.writeString(currency);
        dest.writeByte((byte) (verified ? 1 : 0));
        dest.writeInt(accountType);
        dest.writeDouble(withdrawalAmount);
        dest.writeByte((byte) (collapsed ? 1 : 0));
        dest.writeByte((byte) (checked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTrunc() {
        return trunc;
    }

    public void setTrunc(String trunc) {
        this.trunc = trunc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public double getWithdrawalAmount() {
        return withdrawalAmount;
    }

    public void setWithdrawalAmount(double withdrawalAmount) {
        this.withdrawalAmount = withdrawalAmount;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int getType() {
        return accountType;
    }

    public String getName() {
        switch (accountType) {
            case 2:
                return "Mobile";
            case 3:
                return "Bank";

            case 4:
                return "Card";

            default:
                return "Wallet";
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", network='" + network + '\'' +
                ", avatar='" + avatar + '\'' +
                ", trunc='" + trunc + '\'' +
                ", state='" + state + '\'' +
                ", note='" + note + '\'' +
                ", alias='" + alias + '\'' +
                ", currency='" + currency + '\'' +
                ", verified=" + verified +
                ", accountType=" + accountType +
                ", withdrawalAmount=" + withdrawalAmount +
                ", collapsed=" + collapsed +
                ", checked=" + checked +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) {
            return false;
        }

        Account account = (Account) obj;
        return this.id.equals(account.id);
    }
}
