package net.tospay.auth.ui.auth.forgot;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentForgotPasswordBinding;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.EmailValidator;
import net.tospay.auth.utils.NetworkUtils;

public class ForgotPasswordFragment extends BaseFragment<FragmentForgotPasswordBinding, ForgotPasswordViewModel>
        implements ForgotPasswordNavigator {

    private ForgotPasswordViewModel mViewModel;
    private TextInputLayout emailInputLayout;
    private EditText emailEditText;
    private NavController navController;

    @Override
    public int getBindingVariable() {
        return BR.forgotPasswordViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_forgot_password;
    }

    @Override
    public ForgotPasswordViewModel getViewModel() {
        UserRepository repository = new UserRepository(getAppExecutors(),
                ServiceGenerator.createService(UserService.class, getContext()));
        UserViewModelFactory factory = new UserViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(ForgotPasswordViewModel.class);
        return mViewModel;
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
            if (!TextUtils.isEmpty(s)) {
                if (isEmailValid(s.toString())) {
                    emailInputLayout.setErrorEnabled(false);
                    emailInputLayout.setError(null);
                } else {
                    emailInputLayout.setErrorEnabled(true);
                    emailInputLayout.setError(getString(R.string.invalid_email));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentForgotPasswordBinding mBinding = getViewDataBinding();
        mBinding.setForgotPasswordViewModel(mViewModel);
        mViewModel.setNavigator(this);
        emailEditText = mBinding.emailEditText;
        emailInputLayout = mBinding.emailInputLayout;
        emailEditText.addTextChangedListener(emailTextWatcher);
        navController = Navigation.findNavController(view);
    }

    /**
     * Validate user email
     *
     * @param email - keyed email
     * @return true|false
     */
    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && EmailValidator.isEmailValid(email);
    }

    @Override
    public void onForgotPasswordClick(View view) {
        emailInputLayout.setError(null);

        String email = emailEditText.getText().toString();
        if (!isEmailValid(email)) {
            emailInputLayout.setError(getString(R.string.invalid_email));
            return;
        }

        mViewModel.getEmail().setValue(email);
        mViewModel.forgotPassword();
        mViewModel.getResponseLiveData().observe(this, this::handleResponse);
    }

    private void handleResponse(Resource<Result> resource) {
        if (resource != null) {
            switch (resource.status) {
                case LOADING:
                    hideKeyboard();
                    mViewModel.setIsLoading(true);
                    mViewModel.setIsError(false);
                    break;

                case ERROR:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(true);
                    mViewModel.setErrorMessage(resource.message);
                    break;

                case SUCCESS:
                    mViewModel.setIsLoading(false);
                    mViewModel.setIsError(false);
                    Toast.makeText(getContext(), "OTP sent to your email", Toast.LENGTH_SHORT).show();
                    ForgotPasswordFragmentDirections.ActionNavigationForgotPasswordToNavigationResetPassword
                            action = ForgotPasswordFragmentDirections
                            .actionNavigationForgotPasswordToNavigationResetPassword(mViewModel.getEmail().getValue());
                    navController.navigate(action);
                    break;
            }
        }
    }

    @Override
    public void onLoginClick(View view) {
        navController.navigateUp();
    }
}
