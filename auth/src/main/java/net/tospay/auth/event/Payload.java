package net.tospay.auth.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload implements Parcelable {

    @SerializedName("topic")
    @Expose
    private String topic;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("reason")
    @Expose
    private String reason;

    @SerializedName("code")
    @Expose
    private String code;

    public Payload() {
    }

    public Payload(String topic, String status, String message) {
        this.topic = topic;
        this.status = status;
        this.message = message;
    }

    protected Payload(Parcel in) {
        topic = in.readString();
        status = in.readString();
        message = in.readString();
        reason = in.readString();
        code = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(topic);
        dest.writeString(status);
        dest.writeString(message);
        dest.writeString(reason);
        dest.writeString(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Payload> CREATOR = new Creator<Payload>() {
        @Override
        public Payload createFromParcel(Parcel in) {
            return new Payload(in);
        }

        @Override
        public Payload[] newArray(int size) {
            return new Payload[size];
        }
    };

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "topic='" + topic + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", reason='" + reason + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
