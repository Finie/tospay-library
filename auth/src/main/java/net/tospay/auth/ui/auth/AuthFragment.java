package net.tospay.auth.ui.auth;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentAuthBinding;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.auth.login.LoginViewModel;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

public class AuthFragment extends BaseFragment<FragmentAuthBinding, LoginViewModel> {

    private LoginViewModel mViewModel;

    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.loginViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_auth;
    }

    @Override
    public LoginViewModel getViewModel() {
        UserRepository repository = new UserRepository(getAppExecutors(),
                ServiceGenerator.createService(UserService.class, getContext()));
        UserViewModelFactory factory = new UserViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentAuthBinding mBinding = getViewDataBinding();
        mBinding.setLoginViewModel(mViewModel);

        mBinding.btnLogin.setOnClickListener(view1 -> Navigation.findNavController(view1)
                .navigate(AuthFragmentDirections.actionNavigationAuthToNavigationLogin()));

        mBinding.btnRegister.setOnClickListener(view12 -> Navigation.findNavController(view12)
                .navigate(AuthFragmentDirections.actionNavigationAuthToNavigationRegister()));

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mListener.onLoginFailed();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }
}
