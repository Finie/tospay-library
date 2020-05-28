package net.tospay.auth.remote.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferResponse implements Parcelable {

    @SerializedName("transactionId")
    @Expose
    private String transactionId;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("html")
    @Expose
    private String html;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("reference")
    @Expose
    private String reference;

    public TransferResponse() {
    }

    protected TransferResponse(Parcel in) {
        transactionId = in.readString();
        status = in.readString();
        html = in.readString();
        id = in.readString();
        reference = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeString(status);
        dest.writeString(html);
        dest.writeString(id);
        dest.writeString(reference);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransferResponse> CREATOR = new Creator<TransferResponse>() {
        @Override
        public TransferResponse createFromParcel(Parcel in) {
            return new TransferResponse(in);
        }

        @Override
        public TransferResponse[] newArray(int size) {
            return new TransferResponse[size];
        }
    };

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
