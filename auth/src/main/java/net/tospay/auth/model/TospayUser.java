package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TospayUser implements Parcelable {

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("type_id")
    @Expose
    private String typeId;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("email_verified")
    @Expose
    private boolean emailVerified;

    @SerializedName("expired_at")
    @Expose
    private Long expiredAt;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("country_name")
    @Expose
    private String countryName;

    @SerializedName("language")
    @Expose
    private String language;

    @SerializedName("timezone")
    @Expose
    private String timezone;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("phone_verified")
    @Expose
    private boolean phoneVerified;

    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    @SerializedName("country")
    @Expose
    private Country country;

    @SerializedName("address")
    @Expose
    private Address address;

    @SerializedName("country_emoji")
    @Expose
    private String countryEmoji;

    @SerializedName("static_qr")
    @Expose
    private String staticQr;

    @SerializedName("pin_set")
    @Expose
    private String pin_set;

    public TospayUser() {
    }

    protected TospayUser(Parcel in) {
        userId = in.readString();
        typeId = in.readString();
        email = in.readString();
        emailVerified = in.readByte() != 0;
        if (in.readByte() == 0) {
            expiredAt = null;
        } else {
            expiredAt = in.readLong();
        }
        token = in.readString();
        refreshToken = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        name = in.readString();
        countryCode = in.readString();
        countryName = in.readString();
        language = in.readString();
        timezone = in.readString();
        phone = in.readString();
        currency = in.readString();
        phoneVerified = in.readByte() != 0;
        profilePic = in.readString();
        countryEmoji = in.readString();
        staticQr = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(typeId);
        dest.writeString(email);
        dest.writeByte((byte) (emailVerified ? 1 : 0));
        if (expiredAt == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(expiredAt);
        }
        dest.writeString(token);
        dest.writeString(refreshToken);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(name);
        dest.writeString(countryCode);
        dest.writeString(countryName);
        dest.writeString(language);
        dest.writeString(timezone);
        dest.writeString(phone);
        dest.writeString(currency);
        dest.writeByte((byte) (phoneVerified ? 1 : 0));
        dest.writeString(profilePic);
        dest.writeString(countryEmoji);
        dest.writeString(staticQr);
        dest.writeString(pin_set);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TospayUser> CREATOR = new Creator<TospayUser>() {
        @Override
        public TospayUser createFromParcel(Parcel in) {
            return new TospayUser(in);
        }

        @Override
        public TospayUser[] newArray(int size) {
            return new TospayUser[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCountryEmoji() {
        return countryEmoji;
    }

    public void setCountryEmoji(String countryEmoji) {
        this.countryEmoji = countryEmoji;
    }

    public String getStaticQr() {
        return staticQr;
    }

    public void setStaticQr(String staticQr) {
        this.staticQr = staticQr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (firstname == null && lastname == null) {
            return name;
        }

        return firstname + " " + lastname;
    }

    public String getPin_set() {
        return pin_set;
    }

    public void setPin_set(String pin_set) {
        this.pin_set = pin_set;
    }

    @Override
    public String toString() {
        return "TospayUser{" +
                "userId='" + userId + '\'' +
                ", typeId='" + typeId + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", expiredAt=" + expiredAt +
                ", token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", name='" + name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", language='" + language + '\'' +
                ", timezone='" + timezone + '\'' +
                ", phone='" + phone + '\'' +
                ", currency='" + currency + '\'' +
                ", phoneVerified=" + phoneVerified +
                ", profilePic='" + profilePic + '\'' +
                ", country=" + country +
                ", address=" + address +
                ", countryEmoji='" + countryEmoji + '\'' +
                ", staticQr='" + staticQr + '\'' +
                ", pin_set='" + pin_set + '\'' +
                '}';
    }
}
