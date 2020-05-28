package net.tospay.auth.ui.auth.pin.security.callbacks;


import net.tospay.auth.ui.auth.pin.security.PFResult;

public interface PFPinCodeHelperCallback<T> {
    void onResult(PFResult<T> result);
}
