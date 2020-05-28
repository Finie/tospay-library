package net.tospay.auth.ui.auth.login;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.model.TransactionPin;
import net.tospay.auth.remote.request.LoginRequest;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.ui.base.BaseViewModel;

public class LoginViewModel extends BaseViewModel<LoginNavigator> implements View.OnClickListener {

    private MutableLiveData<String> mEmail;
    private MutableLiveData<String> mPassword;
    private UserRepository userRepository;
    private LiveData<Resource<TospayUser>> responseLiveData;
    private LiveData<Resource<TransactionPin>> pinsetupUrl;

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.mEmail = new MutableLiveData<>();
        this.mPassword = new MutableLiveData<>();
    }

    public MutableLiveData<String> getEmail() {
        return mEmail;
    }

    public MutableLiveData<String> getPassword() {
        return mPassword;
    }

    public LiveData<Resource<TospayUser>> getResponseLiveData() {
        return responseLiveData;
    }

    /**
     * Request user authentication
     */
    public void login() {
        LoginRequest request = new LoginRequest();
        request.setEmail(mEmail.getValue());
        request.setPassword(mPassword.getValue());
        responseLiveData = userRepository.login(request);
    }

    public void login(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        responseLiveData = userRepository.login(request);
    }


    public void getTransactionPinUrl(String bearer){
        pinsetupUrl = userRepository.getTransactionPinUrl(bearer);
    }

    public LiveData<Resource<TransactionPin>> getPinsetupUrl() {
        return pinsetupUrl;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            getNavigator().login(view);

        } else if (view.getId() == R.id.btn_sign_up) {
            getNavigator().signUp(view);

        } else if (view.getId() == R.id.btn_forgot_password) {
            getNavigator().forgotPassword(view);
        }
    }
}
