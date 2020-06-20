package net.tospay.auth.ui.auth.pinAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tospay.auth.R;
import net.tospay.auth.databinding.ActivityPin2Binding;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.model.TransactionPin;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.ExecuteResponse;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.auth.login.LoginViewModel;
import net.tospay.auth.ui.dialog.DialogTransactionLock;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.EmailValidator;
import net.tospay.auth.utils.Encryption;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.utils.TextImages;
import net.tospay.auth.view.LoadingLayout;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static net.tospay.auth.utils.EmailValidator.isEmailValid;

public class PinActivity extends AppCompatActivity  {
    private static final String TAG = "PinActivity";
    public static final int REQUEST_PIN = 200;
    private SharedPrefManager sharedPrefManager;
    private LoginViewModel mViewModel;
    private ActivityPin2Binding mBinding;
    private String email="",password="";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_pin2);

        UserRepository repository = new UserRepository(new AppExecutors(),
                ServiceGenerator.createService(UserService.class, this));


        UserViewModelFactory factory = new UserViewModelFactory(repository);


        mViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);
        sharedPrefManager = SharedPrefManager.getInstance(this);

        if (sharedPrefManager.read(SharedPrefManager.KEY_REMEMBER_ME, false)) {
            if (sharedPrefManager.getActiveUser() != null) {
                mBinding.emailEditText.setText(sharedPrefManager.getActiveUser().getEmail());
                email = sharedPrefManager.getActiveUser().getEmail();
            }
        }

        mBinding.emailEditText.addTextChangedListener(emailTextWatcher);
        mBinding.passwordEditText.addTextChangedListener(passwordTextWatcher);

        mBinding.btnLogin.setOnClickListener(v -> {
            if (email == null){
                mBinding.emailInputLayout.setErrorEnabled(true);
                mBinding.emailInputLayout.setError(getString(R.string.invalid_email));
            }
            else if (password == null){
                mBinding.passwordInputLayout.setErrorEnabled(true);
                mBinding. passwordInputLayout.setError(getString(R.string.invalid_password_));
            }

            else{
                reAuthenticateUser(email, password );
            }
        });

    }


    private void reAuthenticateUser(String email, String password) {
        mViewModel.login(email, password);
        mViewModel.getResponseLiveData().observe(this, resource -> {
            if (resource != null) {

                switch (resource.status) {
                    case LOADING:
                        mBinding.loadingLayout.setVisibility(View.VISIBLE);
                        break;

                    case SUCCESS:
                        mBinding.loadingLayout.setVisibility(View.GONE);
                        sharedPrefManager.setActiveUser(resource.data);
                        finishWithSuccess();
                        break;

                    case ERROR:
                        mBinding.loadingLayout.setVisibility(View.GONE);
                        mBinding.errorLayout.setVisibility(View.VISIBLE);
                        mBinding.errorLayout.setErrorMessage(resource.message);
                        break;
                }
            }
        });
    }

    public void finishWithSuccess() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void finishWithError(String message) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", message);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    /**
     * Validates email input on text change
     */
    private TextWatcher emailTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!TextUtils.isEmpty(s)) {
                if (isEmailValid(s.toString())) {
                    mBinding.emailInputLayout.setErrorEnabled(false);
                    mBinding.emailInputLayout.setError(null);
                    email = s.toString();
                } else {
                    mBinding.emailInputLayout.setErrorEnabled(true);
                    mBinding.emailInputLayout.setError(getString(R.string.invalid_email));
                }
            }
        }
    };

    /**
     * Validate password input on text changed
     */
    private TextWatcher passwordTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                if (s.length() < 6) {
                    mBinding.passwordInputLayout.setErrorEnabled(true);
                   mBinding. passwordInputLayout.setError(getString(R.string.invalid_password_));

                } else {
                    mBinding.passwordInputLayout.setErrorEnabled(false);
                    mBinding.passwordInputLayout.setError(null);
                    password = s.toString();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * Validate user email
     *
     * @param email - keyed email
     * @return true|false
     */
    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && EmailValidator.isEmailValid(email);
    }

    /**
     * Validates user email
     *
     * @param password - keyed password
     * @return true|false
     */
    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 0;
    }

}
