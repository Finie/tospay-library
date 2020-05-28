package net.tospay.auth.ui.account.topup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.databinding.DialogTopupAmountBinding;
import net.tospay.auth.event.NotificationEvent;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.model.transfer.Account;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.OrderInfo;
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
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.account.AccountViewModel;
import net.tospay.auth.ui.account.CardPaymentDialog;
import net.tospay.auth.ui.auth.pinAuth.PinActivity;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.utils.StringUtil;
import net.tospay.auth.utils.Utils;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TopupAmountDialog extends BottomSheetDialogFragment {

    public static final String TAG = "TopupMobileAmountDialog";

    private static final String KEY_WALLET = "wallet";
    private static final String KEY_ACCOUNT = "account";

    private OnTopupListener mListener;
    private AccountViewModel mViewModel;
    private DialogTopupAmountBinding mBinding;
    private SharedPrefManager mSharedPrefManager;
    private Transfer transfer;
    private Wallet wallet;
    private Amount charge;
    private net.tospay.auth.model.Account selectedAccount;
    private double withdrawalAmount = 0;
    private String currency;

    //source
    private List<Store> sources;
    private Store source;

    public TopupAmountDialog() {
        // Required empty public constructor
    }

    public static TopupAmountDialog newInstance(Wallet wallet, net.tospay.auth.model.Account selectedAccount) {
        TopupAmountDialog fragment = new TopupAmountDialog();
        Bundle args = new Bundle();
        args.putParcelable(KEY_WALLET, wallet);
        args.putParcelable(KEY_ACCOUNT, selectedAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);
        mSharedPrefManager = SharedPrefManager.getInstance(getContext());

        transfer = new Transfer();
        transfer.setType(Transfer.TOPUP);

        if (getArguments() != null) {
            wallet = getArguments().getParcelable(KEY_WALLET);
            selectedAccount = getArguments().getParcelable(KEY_ACCOUNT);
            currency = wallet.getCurrency();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_topup_amount, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppExecutors mAppExecutors = new AppExecutors();

        AccountRepository accountRepository = new AccountRepository(mAppExecutors,
                ServiceGenerator.createService(AccountService.class, getContext()));

        PaymentRepository paymentRepository = new PaymentRepository(mAppExecutors,
                ServiceGenerator.createService(PaymentService.class, getContext()));

        UserRepository userRepository = new UserRepository(mAppExecutors,
                ServiceGenerator.createService(UserService.class,getContext()));

        AccountViewModelFactory factory =
                new AccountViewModelFactory(accountRepository, paymentRepository,userRepository);

        mViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel.class);
        mBinding.setAccountViewModel(mViewModel);

        reloadBearerToken();

        mViewModel.getAccount().setValue(selectedAccount);

        mBinding.amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                charge = null;
                mBinding.btnPay.setVisibility(View.GONE);
                mBinding.btnChargeLookup.setVisibility(View.VISIBLE);
            }
        });

        mBinding.btnChargeLookup.setOnClickListener(view1 -> {
            mBinding.amountEditText.clearFocus();
            performChargeLookup();
        });

        mBinding.btnPay.setOnClickListener(view12 -> {
            mBinding.amountEditText.clearFocus();
            topupWallet();
        });
    }

    private void reloadBearerToken() {
        String bearerToken = "Bearer " + mSharedPrefManager.getAccessToken();
        mViewModel.setBearerToken(bearerToken);
    }

    private void performChargeLookup() {
        if (TextUtils.isEmpty(mBinding.amountEditText.getText())) {
            mBinding.amountEditText.setError("Amount is required");
            return;
        }

        charge = null;
        sources = new ArrayList<>();
        source = new Store();

        currency = "KES";

        if (selectedAccount.getCurrency() != null) {
            currency = selectedAccount.getCurrency();
        }

        Account account = new Account();
        account.setCurrency(currency);
        account.setId(selectedAccount.getId());
        account.setType(Utils.getAccountType(selectedAccount.getType()));
        source.setAccount(account);

        withdrawalAmount = Double.parseDouble(mBinding.amountEditText.getText().toString());

        Amount amount = new Amount(String.valueOf(withdrawalAmount), currency);
        source.setOrder(amount);
        source.setTotal(amount);

        sources.add(source);
        transfer.setSource(sources);

        //delivery
        List<Store> deliveries = new ArrayList<>();

        Amount walletAmount = new Amount();
        walletAmount.setAmount(String.valueOf(withdrawalAmount));
        walletAmount.setCurrency(wallet.getCurrency());

        Store delivery = new Store();
        delivery.setAccount(new Account(wallet.getId(), Account.TYPE_WALLET, wallet.getCurrency()));
        delivery.setOrder(amount);
        delivery.setTotal(amount);
        deliveries.add(delivery);
        transfer.setDelivery(deliveries);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setAmount(amount);
        orderInfo.setReference(UUID.randomUUID().toString());
        transfer.setOrderInfo(orderInfo);

        mViewModel.chargeLookup(transfer, Transfer.TOPUP);
        mViewModel.getAmountResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        break;

                    case SUCCESS:
                        if (resource.data == null){
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(true);
                            mViewModel.setErrorMessage(resource.message);
                        }

                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);

                        charge = resource.data;

                        mBinding.paymentSummeryTextView.setVisibility(View.VISIBLE);
                        mBinding.chargeTitleView.setVisibility(View.VISIBLE);
                        mBinding.chargeTextView.setVisibility(View.VISIBLE);
                        mBinding.chargeTextView.setText(String.format("%s %s", charge.getCurrency(),
                                StringUtil.formatAmount(charge.getAmount())));

                        withdrawalAmount += Double.parseDouble(charge.getAmount());
                        source.setTotal(new Amount(String.valueOf(withdrawalAmount), currency));

                        mBinding.totalTitleView.setVisibility(View.VISIBLE);
                        mBinding.totalTextView.setVisibility(View.VISIBLE);
                        mBinding.totalTextView.setText(String.format("%s %s", currency,
                                StringUtil.formatAmount(withdrawalAmount)));

                        mBinding.btnPay.setVisibility(View.VISIBLE);
                        mBinding.btnChargeLookup.setVisibility(View.GONE);

                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        startActivityForResult(new Intent(getContext(), PinActivity.class), PinActivity.REQUEST_PIN);
                        break;
                }
            }
        });
    }

    private void topupWallet() {
        if (charge == null) {
            mViewModel.setIsError(true);
            mViewModel.setErrorMessage("Unable to process request. Please make sure you have entered amount and selected an account");
            return;
        }

        mBinding.amountEditText.setError(null);

        if (TextUtils.isEmpty(mBinding.amountEditText.getText())) {
            mBinding.amountEditText.setError("Amount is required");
            return;
        }

        source.setCharge(charge);
        sources.set(sources.indexOf(source), source);

        mViewModel.topup(transfer);
        mViewModel.getTransferResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mViewModel.setIsError(false);
                        mViewModel.setIsLoading(false);

                        TransferResponse response = resource.data;
                        assert response != null;

                        if (selectedAccount.getType() == AccountType.MOBILE) {
                            mListener.onTopupSuccess(response);
                            dismiss();
                        } else {
                            if (response.getHtml() != null) {
                                CardPaymentDialog.newInstance(response.getHtml())
                                        .show(getChildFragmentManager(), CardPaymentDialog.TAG);
                            } else {
                                dismiss();
                            }
                        }
                        break;

                    case ERROR:
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mViewModel.setIsLoading(false);
                        mViewModel.setErrorMessage(resource.message);
                        mViewModel.setIsError(true);
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        startActivityForResult(new Intent(getContext(), PinActivity.class), PinActivity.REQUEST_PIN);
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PinActivity.REQUEST_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                reloadBearerToken();
            }
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (OnTopupListener) parent;
        } else {
            mListener = (OnTopupListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTopupListener {

        void onTopupSuccess(TransferResponse transferResponse);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(NotificationEvent notification) {
        if (notification != null) {
            dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
