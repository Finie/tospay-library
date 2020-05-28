package net.tospay.auth.ui.auth.reset;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentResetPasswordBinding;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

public class ResetPasswordFragment extends BaseFragment<FragmentResetPasswordBinding, ResetPasswordViewModel>
        implements ResetPasswordNavigator {

    private ResetPasswordViewModel mViewModel;
    private TextInputLayout passwordInputLayout;
    private FragmentResetPasswordBinding mBinding;
    private NavController navController;

    @Override
    public int getBindingVariable() {
        return BR.resetPasswordViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_reset_password;
    }

    @Override
    public ResetPasswordViewModel getViewModel() {
        UserRepository repository = new UserRepository(getAppExecutors(),
                ServiceGenerator.createService(UserService.class, getContext()));
        UserViewModelFactory factory = new UserViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(ResetPasswordViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setResetPasswordViewModel(mViewModel);
        mViewModel.setNavigator(this);
        mBinding.codeEditText.addTextChangedListener(otpTextWatcher);

        String email = ResetPasswordFragmentArgs.fromBundle(getArguments()).getEmail();

        passwordInputLayout = mBinding.passwordInputLayout;
        mBinding.passwordEditText.addTextChangedListener(passwordTextWatcher);

        mViewModel.getEmail().setValue(email);
        navController = Navigation.findNavController(view);
    }

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
                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError(getString(R.string.invalid_password));
                } else {
                    passwordInputLayout.setErrorEnabled(false);
                    passwordInputLayout.setError(null);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private TextWatcher otpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(charSequence)) {
                if (charSequence.length() >= 5) {
                    mBinding.codeInputLayout.setError(null);
                    mViewModel.getOtp().setValue(charSequence.toString());
                } else {
                    mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
                }
            } else {
                mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * Validates user email
     *
     * @param password - keyed password
     * @return true|false
     */
    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 5;
    }

    @Override
    public void onResetPasswordClick(View view) {
        mBinding.codeInputLayout.setError(null);
        mBinding.passwordInputLayout.setError(null);

        String code = mBinding.codeEditText.getText().toString();
        mViewModel.getOtp().setValue(code);

        if (TextUtils.isEmpty(code)) {
            mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
            return;
        }

        if (code.length() < 5) {
            mBinding.codeInputLayout.setError(getString(R.string.invalid_otp));
            return;
        }

        if (!isPasswordValid(mBinding.passwordEditText.getText().toString())) {
            passwordInputLayout.setError(getString(R.string.invalid_password));
            return;
        }

        mViewModel.reset(mBinding.passwordEditText.getText().toString());
        mViewModel.getResetResourceLiveData().observe(this, resource -> {
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
                        Toast.makeText(view.getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        navController.navigate(ResetPasswordFragmentDirections.actionNavigationResetPasswordToNavigationLogin());
                        break;
                }
            }
        });
    }

    @Override
    public void onResendClick(View view) {
        mViewModel.resend();
        mViewModel.getResendResourceLiveData().observe(this, resource -> {
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
                        Toast.makeText(view.getContext(), "OTP sent to your email", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
