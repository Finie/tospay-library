package net.tospay.auth.remote.exception;

import androidx.annotation.Nullable;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Nullable
    @Override
    public String getMessage() {
        return "No network available, please check your WiFi or Data connection";
    }
}
