package net.tospay.auth.ui.account.bank;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentOtpVeifyBinding;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.response.AccountLinkResponse;
import net.tospay.auth.remote.response.LinkBankResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.BankService;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.ui.account.bank.bank_request.LinkBankReq;
import net.tospay.auth.ui.account.bank.model.BankOtpRequest;
import net.tospay.auth.ui.account.bank.model.ResendOtp;
import net.tospay.auth.ui.account.bank.model.VerifyD;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.BankViewModelFactory;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtpVeifyFragment extends BaseFragment<FragmentOtpVeifyBinding,BankViewModel>
        implements BankNavigator{
    private FragmentOtpVeifyBinding mBinding;
    private BankViewModel mViewModel;
    private VerifyD details;
    private LinkBankResponse response;
    private LinkBankReq request;

    public OtpVeifyFragment() {
        // Required empty public constructor
    }


    @Override
    public int getBindingVariable() {
        return BR.bankViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_otp_veify;
    }

    @Override
    public BankViewModel getViewModel() {
        BankService bankService = ServiceGenerator.createService(BankService.class, getContext());
        GatewayService gatewayService = ServiceGenerator.createService(GatewayService.class, getContext());
        BankRepository bankRepository = new BankRepository(getAppExecutors(), bankService);
        GatewayRepository gatewayRepository = new GatewayRepository(getAppExecutors(), gatewayService);
        SharedPrefManager sharedPrefManager = getSharedPrefManager();
        BankViewModelFactory factory = new BankViewModelFactory(bankRepository, gatewayRepository,sharedPrefManager);
        mViewModel = ViewModelProviders.of(this, factory).get(BankViewModel.class);
        return mViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        onViewStateRestored(savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setBankViewModel(mViewModel);
        mViewModel.setNavigator(this);
        assert getArguments() != null;
        details = OtpVeifyFragmentArgs.fromBundle(getArguments()).getBank();
        response = OtpVeifyFragmentArgs.fromBundle(getArguments()).getResponse();
        mViewModel.getPhone().setValue(details.getPhone());
        request = OtpVeifyFragmentArgs.fromBundle(getArguments()).getRequest();
        mBinding.resendOtp.setOnClickListener(v -> {
            ResendOtp resendOtp = new ResendOtp();
            resendOtp.setBankAbbrv(details.getCode());
            resendOtp.setBankAccountToken(response.getToken());
            resendOtp(resendOtp);
        });


    }

    private void resendOtp(ResendOtp request) {
        mViewModel.resendOtp(request);
        mViewModel.getResendOtp().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        Toast.makeText(getActivity(), "Otp has been sent", Toast.LENGTH_SHORT).show();
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        openActivityOnTokenExpire();
                        break;
                }
            }
        });

    }

    @Override
    public void onDone(View view) {

    }

    @Override
    public void onVerify() {
        String code = Objects.requireNonNull(mBinding.otpView.getText()).toString();
        mViewModel.getOtp().setValue(code);
        if (TextUtils.isEmpty(code)) {
            mViewModel.setIsError(true);
            mViewModel.setErrorMessage(getString(R.string.invalid_otp));
            return;
        }
        if (code.length() < 5) {
            mViewModel.setErrorMessage(getString(R.string.invalid_otp));
            return;
        }
        BankOtpRequest request = new BankOtpRequest();
        request.setBankAccountToken(response.getToken());
        request.setBankCode(details.getCode());
        request.setToken(code);
        mViewModel.verifyLinkBank(request);
        mViewModel.getBankVerify().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null){
                switch (resource.status){
                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        startActivityForResult(new Intent(getContext(), AuthActivity.class), AuthActivity.REQUEST_CODE_LOGIN);
                        break;

                    case LOADING:
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
                        NavHostFragment.findNavController(this)
                                .navigate(OtpVeifyFragmentDirections.actionNavigateConfirmBankToNavigateToLinkSummary());
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK){
            onVerify();
        }
    }


}
