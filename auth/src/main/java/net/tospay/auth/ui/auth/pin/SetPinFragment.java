package net.tospay.auth.ui.auth.pin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentSetPinBinding;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.auth.login.LoginViewModel;
import net.tospay.auth.ui.auth.pinAuth.PinActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

public class SetPinFragment extends BaseFragment<FragmentSetPinBinding, LoginViewModel> {

    private LoginViewModel mViewModel;

    @Override
    public int getBindingVariable() {
        return BR.loginViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_set_pin;
    }

    @Override
    public LoginViewModel getViewModel() {
        UserRepository repository = new UserRepository(getAppExecutors(),
                ServiceGenerator.createService(UserService.class, getContext()));
        UserViewModelFactory factory = new UserViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);
        return mViewModel;
    }

    public SetPinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentSetPinBinding mBinding = getViewDataBinding();
        mBinding.setLoginViewModel(mViewModel);
        mBinding.setMessage(getString(R.string.account_success));
        mBinding.btnSetPin.setOnClickListener(view1 -> {
            startActivityForResult(new Intent(getContext(), PinActivity.class), PinActivity.REQUEST_PIN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PinActivity.REQUEST_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                mListener.onLoginSuccess(getSharedPrefManager().getActiveUser());
            } else {
                mListener.onLoginFailed();
            }
        }
    }
}
