package net.tospay.auth.ui.account.account_fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentCardAccountBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.account.AccountViewModel;
import net.tospay.auth.ui.account.account_fragments.liking.LinkAct;
import net.tospay.auth.ui.auth.pinAuth.PinActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardAccount extends BaseFragment<FragmentCardAccountBinding, AccountViewModel>
        implements AcountsAdapter.AccountselectListener, AccoutDeleteDialog.OnAccountActionListener {

    private FragmentCardAccountBinding mBinding;
    private AccountViewModel mViewModel;
    private AcountsAdapter adapter;
    List<Account> mAccounts;
    private ProgressDialog progressDialog;

    @Override
    public int getBindingVariable() {
        return BR.mViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_card_account;
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
        mBinding.setMViewModel(mViewModel);
        adapter = new AcountsAdapter(new ArrayList<>(), this);
        mBinding.recyclerView.setAdapter(adapter);
        mBinding.linkAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LinkAct.class);
            intent.putExtra("link", "card");
            startActivityForResult(intent, LinkAct.LINK_ACCOUT_REQUEST);
        });
        progressDialog = new ProgressDialog(getContext());
        fetchData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LinkAct.LINK_ACCOUT_REQUEST) {
            fetchData();
        } else if (requestCode == PinActivity.REQUEST_PIN) {
            fetchData();
        }
    }


    void fetchData() {

        mViewModel.fetchCards();
        mViewModel.getCardAccount().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        if (resource.data != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            adapter.setmAccounts(resource.data);
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            adapter.setmAccounts(new ArrayList<>());
                            Toast.makeText(getActivity(), "No linked Card accounts", Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }
        });
    }

    @Override
    public void OnAccountSelected(Account account) {
        AccoutDeleteDialog.newInstance(account).show(getChildFragmentManager(), CardAccount.class.getSimpleName());
    }

    @Override
    public void onEditAccount(Account account) {

    }

    @Override
    public void onDeleteAccount(Account account) {
        showCustomDialog(account,getActivity(),getView());
    }

    private void deleteAccount(Account account) {
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Deleting account. Please wait...");
        mViewModel.delete(account, AccountType.CARD);
        mViewModel.getResultResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        progressDialog.show();
                        break;

                    case SUCCESS:
                        progressDialog.setMessage("account deleted, please wait..");
                        fetchData();
                        break;

                    case ERROR:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
    private void showCustomDialog(Account account, Context context, View view) {
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btn_delete = dialogView.findViewById(R.id.btn_delete);
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> alertDialog.dismiss());
        btn_delete.setOnClickListener(v -> {
            deleteAccount(account);
            alertDialog.dismiss();
        });
    }
}

