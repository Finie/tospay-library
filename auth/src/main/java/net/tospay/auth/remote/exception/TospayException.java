package net.tospay.auth.remote.exception;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TospayException extends IOException {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("error")
    @Expose
    private List<Error> error = null;

    public TospayException(String message) {
        super(message);
    }

    public TospayException(Throwable throwable) {
        super(throwable);
    }

    public TospayException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Error> getError() {
        return error;
    }

    public void setError(List<Error> error) {
        this.error = error;
    }

    /**
     * Converts the error message into a single string
     *
     * @return string
     */
    public String getErrorMessage() {
        if (error != null) {
            if (!error.isEmpty()) {
                if (error.size() == 1) {
                    return error.get(0).getDescription();
                } else {
                    return TextUtils.join("\n", error);
                }
            }

            return description;
        }

        return description;
    }

    public class Error {

        @SerializedName("code")
        @Expose
        private String code;

        @SerializedName("description")
        @Expose
        private String description;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @NonNull
        @Override
        public String toString() {
            return description;
        }
    }
}
