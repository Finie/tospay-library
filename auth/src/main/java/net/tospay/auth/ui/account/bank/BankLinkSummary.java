package net.tospay.auth.ui.account.bank;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentBankLinkSummaryBinding;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.service.BankService;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.ui.account.account_fragments.liking.LinkAct;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.BankViewModelFactory;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankLinkSummary extends BaseFragment<FragmentBankLinkSummaryBinding,BankViewModel> {

    private FragmentBankLinkSummaryBinding mBinding;
    private BankViewModel mViewModel;

    @Override
    public int getBindingVariable() {
        return BR.bankViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bank_link_summary;
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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setBankViewModel(mViewModel);
        mBinding.btnDone.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("result","Bank account has been successfully linked");
            Objects.requireNonNull(getActivity()).setResult(LinkAct.LINK_ACCOUT_REQUEST,intent);
            Objects.requireNonNull(getActivity()).finish();
        });

    }
}
