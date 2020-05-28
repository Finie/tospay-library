package net.tospay.auth.ui.base;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import java.lang.ref.WeakReference;

public abstract class BaseViewModel<N> extends ViewModel {

    private final ObservableBoolean mIsLoading = new ObservableBoolean();
    private final ObservableBoolean mIsError = new ObservableBoolean(false);
    private final ObservableField<String> mErrorMessage = new ObservableField<>("");
    private final ObservableField<String> mBearerToken = new ObservableField<>("");

    private WeakReference<N> mNavigator;

    public N getNavigator() {
        return mNavigator.get();
    }

    public void setNavigator(N navigator) {
        this.mNavigator = new WeakReference<>(navigator);
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

    public ObservableBoolean getIsError() {
        return mIsError;
    }

    public void setIsError(boolean isError) {
        mIsError.set(isError);
    }

    public ObservableField<String> getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage.set(errorMessage);
    }

    public ObservableField<String> getBearerToken() {
        return mBearerToken;
    }

    public void setBearerToken(String bearerToken) {
        mBearerToken.set(bearerToken);
    }
}
