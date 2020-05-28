package net.tospay.auth.ui.account.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentVerifyMobileBinding;
import net.tospay.auth.model.Account;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.MobileRepository;
import net.tospay.auth.remote.service.MobileService;
import net.tospay.auth.ui.account.account_fragments.liking.LinkAct;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.NetworkUtils;
import net.tospay.auth.viewmodelfactory.MobileViewModelFactory;

import java.util.Objects;

public class VerifyMobileFragment extends BaseFragment<FragmentVerifyMobileBinding, MobileMoneyViewModel>
        implements MobileMoneyNavigator {

    private MobileMoneyViewModel mViewModel;
    private FragmentVerifyMobileBinding mBinding;
    private Account account;

    public VerifyMobileFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.mobileMoneyViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_verify_mobile;
    }

    @Override
    public MobileMoneyViewModel getViewModel() {
        MobileRepository repository = new MobileRepository(getAppExecutors(),
                ServiceGenerator.createService(MobileService.class, getContext()));
        MobileViewModelFactory factory = new MobileViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(MobileMoneyViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setMobileViewModel(mViewModel);
        mViewModel.setNavigator(this);

        if (getArguments() != null) {
            account = VerifyMobileFragmentArgs.fromBundle(getArguments()).getAccount();
            mViewModel.getPhone().setValue("****" + account.getTrunc());
        }
    }

    @Override
    public void onConfirmClick(View view) {
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

        mViewModel.verify(account.getId());
        mViewModel.getVerifyResourceLiveData().observe(this, resultResource -> {
            if (resultResource != null) {
                switch (resultResource.status) {
                    case LOADING:
                        mViewModel.setIsError(false);
                        mViewModel.setIsLoading(true);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resultResource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        Intent intent = new Intent();
                        intent.putExtra("result","Mobile account has been successfully linked");
                        Objects.requireNonNull(getActivity()).setResult(LinkAct.LINK_ACCOUT_REQUEST,intent);
                        Objects.requireNonNull(getActivity()).finish();
                        break;
                }
            }
        });
    }

    @Override
    public void onResendClick(View view) {
        mBinding.otpView.setText(null);
        mViewModel.resend(account.getId());
        mViewModel.getResendResourceLiveData().observe(this, resultResource -> {
            if (resultResource != null) {
                switch (resultResource.status) {
                    case LOADING:
                        mViewModel.setIsError(false);
                        mViewModel.setIsLoading(true);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resultResource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        Toast.makeText(getContext(), "OTP Sent. Please check your SMS", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
