package net.tospay.auth.ui.auth.pin;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import net.tospay.auth.ui.auth.pin.security.PFLiveData;
import net.tospay.auth.ui.auth.pin.security.PFResult;
import net.tospay.auth.ui.auth.pin.security.PFSecurityManager;
import net.tospay.auth.ui.auth.pin.security.callbacks.PFPinCodeHelperCallback;

public class PFPinCodeViewModel extends ViewModel {

    public LiveData<PFResult<String>> encodePin(Context context, String pin) {
        final PFLiveData<PFResult<String>> liveData = new PFLiveData<>();
        PFSecurityManager.getInstance().getPinCodeHelper().encodePin(
                context,
                pin,
                liveData::setData
        );
        return liveData;
    }

    public LiveData<PFResult<Boolean>> checkPin(Context context, String encodedPin, String pin) {
        final PFLiveData<PFResult<Boolean>> liveData = new PFLiveData<>();
        PFSecurityManager.getInstance().getPinCodeHelper().checkPin(
                context,
                encodedPin,
                pin,
                liveData::setData
        );
        return liveData;
    }

    public LiveData<PFResult<Boolean>> delete() {
        final PFLiveData<PFResult<Boolean>> liveData = new PFLiveData<>();
        PFSecurityManager.getInstance().getPinCodeHelper().delete(
                liveData::setData
        );
        return liveData;
    }

    public LiveData<PFResult<Boolean>> isPinCodeEncryptionKeyExist() {
        final PFLiveData<PFResult<Boolean>> liveData = new PFLiveData<>();
        PFSecurityManager.getInstance().getPinCodeHelper().isPinCodeEncryptionKeyExist(
                liveData::setData
        );
        return liveData;
    }

}
