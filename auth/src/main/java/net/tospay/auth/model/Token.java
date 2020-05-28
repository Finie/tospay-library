package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token implements Parcelable {

    @SerializedName("expired_at")
    @Expose
    private Long expiredAt;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    public Token() {

    }

    protected Token(Parcel in) {
        if (in.readByte() == 0) {
            expiredAt = null;
        } else {
            expiredAt = in.readLong();
        }
        token = in.readString();
        refreshToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (expiredAt == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(expiredAt);
        }
        dest.writeString(token);
        dest.writeString(refreshToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "Token{" +
                "expiredAt=" + expiredAt +
                ", token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
