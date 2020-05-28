package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChargeInfo implements Parcelable {

    @SerializedName("source")
    @Expose
    private Store source;

    @SerializedName("destination")
    @Expose
    private Amount amount;

    @SerializedName("partnerInfo")
    @Expose
    private PartnerInfo partnerInfo;

    @SerializedName("railInfo")
    @Expose
    private RailInfo railInfo;

    public ChargeInfo() {
    }

    protected ChargeInfo(Parcel in) {
        source = in.readParcelable(Store.class.getClassLoader());
        amount = in.readParcelable(Amount.class.getClassLoader());
        partnerInfo = in.readParcelable(PartnerInfo.class.getClassLoader());
        railInfo = in.readParcelable(RailInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(source, flags);
        dest.writeParcelable(amount, flags);
        dest.writeParcelable(partnerInfo, flags);
        dest.writeParcelable(railInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChargeInfo> CREATOR = new Creator<ChargeInfo>() {
        @Override
        public ChargeInfo createFromParcel(Parcel in) {
            return new ChargeInfo(in);
        }

        @Override
        public ChargeInfo[] newArray(int size) {
            return new ChargeInfo[size];
        }
    };

    public Store getSource() {
        return source;
    }

    public void setSource(Store source) {
        this.source = source;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public PartnerInfo getPartnerInfo() {
        return partnerInfo;
    }

    public void setPartnerInfo(PartnerInfo partnerInfo) {
        this.partnerInfo = partnerInfo;
    }

    public RailInfo getRailInfo() {
        return railInfo;
    }

    public void setRailInfo(RailInfo railInfo) {
        this.railInfo = railInfo;
    }
}