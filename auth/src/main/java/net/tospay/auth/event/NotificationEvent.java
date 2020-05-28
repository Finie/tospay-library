package net.tospay.auth.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationEvent implements Parcelable {

    public static final String TOPUP = "TOPUP";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String PAYMENT = "PAYMENT";

    @SerializedName("data")
    @Expose
    private Payload payload;

    @SerializedName("notification")
    @Expose
    private Notification notification;

    public NotificationEvent() {
    }

    public NotificationEvent(Payload payload) {
        this.payload = payload;
    }

    public NotificationEvent(Notification notification) {
        this.notification = notification;
    }

    public NotificationEvent(Payload payload, Notification notification) {
        this.payload = payload;
        this.notification = notification;
    }

    protected NotificationEvent(Parcel in) {
        payload = in.readParcelable(Payload.class.getClassLoader());
        notification = in.readParcelable(Notification.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(payload, flags);
        dest.writeParcelable(notification, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationEvent> CREATOR = new Creator<NotificationEvent>() {
        @Override
        public NotificationEvent createFromParcel(Parcel in) {
            return new NotificationEvent(in);
        }

        @Override
        public NotificationEvent[] newArray(int size) {
            return new NotificationEvent[size];
        }
    };

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "payload=" + payload +
                ", notification=" + notification +
                '}';
    }
}
