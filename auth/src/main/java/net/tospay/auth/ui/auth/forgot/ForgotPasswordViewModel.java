package net.tospay.auth.ui.auth.forgot;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.ui.base.BaseViewModel;

public class ForgotPasswordViewModel extends BaseViewModel<ForgotPasswordNavigator> implements View.OnClickListener {

    private MutableLiveData<String> mEmail;
    private final UserRepository userRepository;
    private LiveData<Resource<Result>> responseLiveData;

    public ForgotPasswordViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.mEmail = new MutableLiveData<>();
    }

    public MutableLiveData<String> getEmail() {
        return mEmail;
    }

    public LiveData<Resource<Result>> getResponseLiveData() {
        return responseLiveData;
    }

    public void forgotPassword() {
        responseLiveData = userRepository.forgotPassword(mEmail.getValue());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_forgot_password) {
            getNavigator().onForgotPasswordClick(view);

        } else if (id == R.id.btn_back_to_login) {
            getNavigator().onLoginClick(view);
        }
    }
}
