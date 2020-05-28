package net.tospay.auth.ui.auth.reset;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.ui.base.BaseViewModel;

public class ResetPasswordViewModel extends BaseViewModel<ResetPasswordNavigator> implements View.OnClickListener {

    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    public MutableLiveData<String> otp = new MutableLiveData<>();

    private final UserRepository userRepository;

    private LiveData<Resource<Result>> resetResourceLiveData;
    private LiveData<Resource<Result>> resendResourceLiveData;

    public ResetPasswordViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<String> getEmail() {
        return mEmail;
    }

    public MutableLiveData<String> getOtp() {
        return otp;
    }

    public LiveData<Resource<Result>> getResetResourceLiveData() {
        return resetResourceLiveData;
    }

    public LiveData<Resource<Result>> getResendResourceLiveData() {
        return resendResourceLiveData;
    }

    public void resend() {
        resendResourceLiveData = userRepository.forgotPassword(mEmail.getValue());
    }

    public void reset(String password) {
        String otp = getOtp().getValue();
        resetResourceLiveData = userRepository.resetPassword(mEmail.getValue(), otp, password);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_reset_password) {
            getNavigator().onResetPasswordClick(view);

        } else if (id == R.id.btn_resend) {
            getNavigator().onResendClick(view);
        }
    }
}
