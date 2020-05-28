package net.tospay.auth.ui.account.link;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLinkBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.model.transfer.Account;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.Store;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.TransferResponse;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.account.AccountAdapter;
import net.tospay.auth.ui.account.AccountNavigator;
import net.tospay.auth.ui.account.AccountSelectionFragmentDirections;
import net.tospay.auth.ui.account.AccountViewModel;
import net.tospay.auth.ui.account.MpesaLoadingDialog;
import net.tospay.auth.ui.account.OnAccountItemClickListener;
import net.tospay.auth.ui.account.topup.TopupAccountSelectionDialog;
import net.tospay.auth.ui.account.topup.TopupAmountDialog;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class LinkFragment extends BaseFragment<FragmentLinkBinding, AccountViewModel>
        implements OnAccountItemClickListener, AccountNavigator,
        TopupAccountSelectionDialog.OnAccountListener,
        TopupAmountDialog.OnTopupListener{

    private FragmentLinkBinding mBinding;
    private AccountViewModel mViewModel;
    private Amount charge;
    private Transfer transfer;
    private Account account;
    private String paymentId;
    private List<Store> sources;

    private Wallet topupWallet;


    @Override
    public int getBindingVariable() {
        return BR.accountViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_link;
    }

    @Override
    public AccountViewModel getViewModel() {
        AccountRepository accountRepository = new AccountRepository(getAppExecutors(),
                ServiceGenerator.createService(AccountService.class, getContext()));


        PaymentRepository paymentRepository = new PaymentRepository(getAppExecutors(),
                ServiceGenerator.createService(PaymentService.class, getContext()));

        UserRepository userRepository = new UserRepository(getAppExecutors(),
                ServiceGenerator.createService(UserService.class,getContext()));

        AccountViewModelFactory factory =
                new AccountViewModelFactory(accountRepository, paymentRepository,userRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setAccountViewModel(mViewModel);
        mBinding.layoutLinkCard.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(LinkFragmentDirections.actionNavigateToLinkCard("account")));
        mBinding.layoutLinkMobile.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(LinkFragmentDirections.actionNavigateToLinkMobile("account")));
        List<AccountType> accountTypes = new ArrayList<>();
        AccountAdapter adapter = new AccountAdapter(accountTypes, this);
        fetchAccounts();
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onTopupClick(Wallet wallet) {
        this.topupWallet = wallet;
        TopupAccountSelectionDialog.newInstance().show(getChildFragmentManager(), TopupAccountSelectionDialog.TAG);
    }


    private void fetchAccounts() {
        mViewModel.fetchAccounts(true);
        mViewModel.getAccountsResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        if (resource.data != null && resource.data.size() > 0) {
                            mBinding.setResource(resource);
                        } else {
                            mViewModel.setIsEmpty(true);
                        }
                        break;

                    case RE_AUTHENTICATE:
                        mBinding.swipeRefreshLayout.setRefreshing(false);
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
    public void onRefresh() {
        fetchAccounts();
    }

    @Override
    public void onAccount(net.tospay.auth.model.Account account) {
        if (topupWallet == null) {
            return;
        }

        TopupAmountDialog.newInstance(topupWallet, account)
                .show(getChildFragmentManager(), TopupAmountDialog.TAG);
    }

    @Override
    public void onVerifyAccount(net.tospay.auth.model.Account account) {
        AccountSelectionFragmentDirections.ActionNavigationAccountSelectionToNavigationVerifyMobile
                action = AccountSelectionFragmentDirections
                .actionNavigationAccountSelectionToNavigationVerifyMobile(account);

        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onTopupSuccess(TransferResponse transferResponse) {
        MpesaLoadingDialog.newInstance(transferResponse).show(getChildFragmentManager(), MpesaLoadingDialog.TAG);
    }
}
