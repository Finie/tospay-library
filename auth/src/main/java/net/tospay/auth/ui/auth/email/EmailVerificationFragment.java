package net.tospay.auth.ui.auth.email;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentEmailVerificationBinding;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

public class EmailVerificationFragment extends BaseFragment<FragmentEmailVerificationBinding, EmailViewModel>
        implements EmailNavigator {

    private FragmentEmailVerificationBinding mBinding;
    private EmailViewModel mViewModel;
    private TospayUser tospayUser;

    public EmailVerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.emailViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_email_verification;
    }

    @Override
    public EmailViewModel getViewModel() {
        UserRepository repository = new UserRepository(getAppExecutors(),
                ServiceGenerator.createService(UserService.class, getContext()));
        UserViewModelFactory factory = new UserViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(EmailViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mViewModel.setNavigator(this);
        tospayUser = getSharedPrefManager().getActiveUser();
        mViewModel.getUser().setValue(tospayUser);
    }

    @Override
    public void onVerifyClick(View view) {
        String code = mBinding.otpView.getText().toString();
        mViewModel.getOtp().setValue(code);

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(view.getContext(), getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.length() < 5) {
            Toast.makeText(view.getContext(), getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
            return;
        }

        mViewModel.verify();
        mViewModel.getVerifyResourceLiveData().observe(this, this::handleResponse);
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
                    mViewModel.setIsError(false);
                    tospayUser.setEmailVerified(true);
                    getSharedPrefManager().setActiveUser(tospayUser);
                    mViewModel.setIsLoading(false);
                    NavHostFragment.findNavController(this)
                            .navigate(EmailVerificationFragmentDirections
                                    .actionNavigationEmailVerificationToNavigationPhoneVerification());
                    break;
            }
        }
    }

    @Override
    public void onResendClick(View view) {
        mViewModel.resend();
        mViewModel.getResendResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        hideKeyboard();
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsError(false);
                        mViewModel.setIsLoading(false);
                        Toast.makeText(view.getContext(), "Please check your email for OTP", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
