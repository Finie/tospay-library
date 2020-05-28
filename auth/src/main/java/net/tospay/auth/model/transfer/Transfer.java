package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.TospayUser;

import java.util.List;

public class Transfer implements Parcelable {

    public static final String TOPUP = "TOPUP";
    public static final String WITHDRAW = "WITHDRAW";
    public static final String TRANSFER = "TRANSFER";
    public static final String PAYMENT = "PAYMENT";

    @SerializedName("delivery")
    @Expose
    private List<Store> delivery = null;

    @SerializedName("source")
    @Expose
    private List<Store> source = null;

    @SerializedName("orderInfo")
    @Expose
    private OrderInfo orderInfo;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("chargeInfo")
    @Expose
    private ChargeInfo chargeInfo;

    @SerializedName("merchant")
    @Expose
    private TospayUser merchant;

    @SerializedName("deviceInfo")
    @Expose
    private DeviceInfo deviceInfo;

    public Transfer() {
    }

    protected Transfer(Parcel in) {
        delivery = in.createTypedArrayList(Store.CREATOR);
        source = in.createTypedArrayList(Store.CREATOR);
        orderInfo = in.readParcelable(OrderInfo.class.getClassLoader());
        type = in.readString();
        chargeInfo = in.readParcelable(ChargeInfo.class.getClassLoader());
        merchant = in.readParcelable(TospayUser.class.getClassLoader());
        deviceInfo = in.readParcelable(DeviceInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(delivery);
        dest.writeTypedList(source);
        dest.writeParcelable(orderInfo, flags);
        dest.writeString(type);
        dest.writeParcelable(chargeInfo, flags);
        dest.writeParcelable(merchant, flags);
        dest.writeParcelable(deviceInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Transfer> CREATOR = new Creator<Transfer>() {
        @Override
        public Transfer createFromParcel(Parcel in) {
            return new Transfer(in);
        }

        @Override
        public Transfer[] newArray(int size) {
            return new Transfer[size];
        }
    };

    public List<Store> getDelivery() {
        return delivery;
    }

    public void setDelivery(List<Store> delivery) {
        this.delivery = delivery;
    }

    public List<Store> getSource() {
        return source;
    }

    public void setSource(List<Store> source) {
        this.source = source;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChargeInfo getChargeInfo() {
        return chargeInfo;
    }

    public void setChargeInfo(ChargeInfo chargeInfo) {
        this.chargeInfo = chargeInfo;
    }

    public TospayUser getMerchant() {
        return merchant;
    }

    public void setMerchant(TospayUser merchant) {
        this.merchant = merchant;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
