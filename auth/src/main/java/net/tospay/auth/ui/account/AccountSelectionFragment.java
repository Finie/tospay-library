package net.tospay.auth.ui.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.Tospay;
import net.tospay.auth.databinding.FragmentAccountSelectionBinding;
import net.tospay.auth.event.NotificationEvent;
import net.tospay.auth.event.Payload;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.model.TotalDisp;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.model.transfer.Account;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.DeviceInfo;
import net.tospay.auth.model.transfer.Store;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.exception.TospayException;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.ExecuteResponse;
import net.tospay.auth.remote.response.TransferResponse;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.ui.account.bank.model.FxRequest;
import net.tospay.auth.ui.account.bank.model.FxResponse;
import net.tospay.auth.ui.account.bank.model.Origin;
import net.tospay.auth.ui.account.topup.StatusReq;
import net.tospay.auth.ui.account.topup.TopupAccountSelectionDialog;
import net.tospay.auth.ui.account.topup.TopupAmountDialog;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.auth.pinAuth.PinActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.dialog.DialogTransactionLock;
import net.tospay.auth.ui.dialog.TransferDialog;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.DeviceDetails;
import net.tospay.auth.utils.RoundOffLib;
import net.tospay.auth.utils.Utils;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountSelectionFragment extends BaseFragment<FragmentAccountSelectionBinding, AccountViewModel>
        implements OnAccountItemClickListener, PaymentListener, AccountNavigator,
        TopupAccountSelectionDialog.OnAccountListener, TopupAmountDialog.OnTopupListener, PayCard3ds.ClosePay3Ds, DialogTransactionLock.CloseTransactionLock {

    private AccountViewModel mViewModel;
    private FragmentAccountSelectionBinding mBinding;
    private Amount charge;
    private Transfer transfer;
    private Account account;
    private int interval = 5000;
    private int count = 0;
    private String paymentId;
    private List<Store> sources;
    private double withdrawalAmount = 0;
    private String status = NotificationEvent.STATUS_FAILED, title = "", message = "Error Unknown";
    private Wallet topupWallet;
    private TransferResponse transferResponse;
    private static final String TAG = "AccountSelectionFragmen";
    private final Handler handler = new Handler();
    private Runnable runnable;
    private boolean shouldRun = true, isSocketNotified = false;
    private FxResponse fxResponse;
    private Tospay tospay;
    private TospayUser tospayUser;
    private MutableLiveData<TospayUser> user;
    private Store source = new Store();
    private DeviceInfo deviceInfo = new DeviceInfo();
    private ExecuteResponse executeResponse = new ExecuteResponse();

    public AccountSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.accountViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_account_selection;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tospay = Tospay.getInstance(getContext());
        this.tospayUser = tospay.getCurrentUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setAccountViewModel(mViewModel);
        mBinding.setName(getSharedPrefManager().getActiveUser().getName());
        mViewModel.setNavigator(this);

        if (getArguments() != null) {
            transfer = AccountSelectionFragmentArgs.fromBundle(getArguments()).getTransfer();
            paymentId = AccountSelectionFragmentArgs.fromBundle(getArguments()).getPaymentId();
            fxResponse = AccountSelectionFragmentArgs.fromBundle(getArguments()).getFx();
            mViewModel.getTransfer().setValue(transfer);
            mViewModel.getMfxResponse().setValue(fxResponse);
        }

        List<AccountType> accountTypes = new ArrayList<>();
        AccountAdapter adapter = new AccountAdapter(accountTypes, this);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);

        mBinding.layoutLinkCard.setOnClickListener(view12 ->
                NavHostFragment.findNavController(this)
                        .navigate(AccountSelectionFragmentDirections
                                .actionNavigationAccountSelectionToNavigationLinkCardAccount("checkout"))
        );

        mBinding.layoutLinkMobile.setOnClickListener(view13 ->
                NavHostFragment.findNavController(this)
                        .navigate(AccountSelectionFragmentDirections
                                .actionNavigationAccountSelectionToNavigationLinkMobileAccount("checkout"))
        );

        mBinding.layoutLinkBank.setOnClickListener(view15 -> {
            NavHostFragment.findNavController(this)
                    .navigate(AccountSelectionFragmentDirections
                            .actionNavigationAccountSelectionToNavigationLinkBank("checkout"));
        });

        mBinding.btnBackImageView.setOnClickListener(view1 ->
                Navigation.findNavController(view).navigateUp());

        mBinding.btnPay.setOnClickListener(view14 -> executePayment());
        mBinding.swipeRefreshLayout.setOnRefreshListener(this::fetchAccounts);
        mViewModel.getUser().setValue(tospayUser);
        fetchAccounts();
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
    public void onTopupClick(Wallet wallet) {
        this.topupWallet = wallet;
        TopupAccountSelectionDialog.newInstance().show(getChildFragmentManager(), TopupAccountSelectionDialog.TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onAccountSelectedListener(AccountType accountType) {
        this.account = new Account();
        mViewModel.getSource().setValue(null);

        if (accountType instanceof Wallet) {
            Wallet wallet = (Wallet) accountType;
            account.setType("wallet");
            account.setId(wallet.getId());
            account.setCurrency(wallet.getCurrency());
        } else {
            net.tospay.auth.model.Account acc = (net.tospay.auth.model.Account) accountType;

            String currency = "KES";
            if (acc.getCurrency() != null) {
                currency = acc.getCurrency();
            }

            account.setCurrency(currency);
            account.setId(acc.getId());
            account.setType(Utils.getAccountType(accountType.getType()));
        }

        getLocation();
    }

    @Override
    public void onVerifyClick(AccountType accountType) {
        AccountSelectionFragmentDirections.ActionNavigationAccountSelectionToNavigationVerifyMobile
                action = AccountSelectionFragmentDirections
                .actionNavigationAccountSelectionToNavigationVerifyMobile((net.tospay.auth.model.Account) accountType);

        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthActivity.REQUEST_CODE_LOGIN || requestCode == PinActivity.REQUEST_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                reloadBearerToken();
                fetchAccounts();
            }
        }
    }

    @Override
    public void onRefresh() {
        fetchAccounts();
    }

    private void performChargeLookup() {
        Transfer chargeTransfer = new Transfer();
        chargeTransfer.setType(Transfer.PAYMENT);
        chargeTransfer.setOrderInfo(transfer.getOrderInfo());
        Amount amount = transfer.getOrderInfo().getAmount();

        //sources
        sources = new ArrayList<>();
        Store source = new Store();
        source.setAccount(account);
        source.setOrder(amount);
        source.setTotal(amount);
        sources.add(source);

        //deliveries
        List<Store> deliveries = new ArrayList<>();
        Store delivery = transfer.getDelivery().get(0);
        delivery.setOrder(amount);
        delivery.setTotal(amount);
        deliveries.add(delivery);
        chargeTransfer.setDelivery(deliveries);
        chargeTransfer.setSource(sources);
        chargeTransfer.setDeviceInfo(deviceInfo);
        mViewModel.chargeLookup(chargeTransfer, Transfer.PAYMENT);
        mViewModel.getAmountResourceLiveData().observe(this, resource -> {
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
                        if (resource.data == null) {
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(true);
                            mViewModel.setErrorMessage(resource.message);
                            return;
                        }
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        charge = resource.data;
                        withdrawalAmount = Double.valueOf(chargeTransfer.getOrderInfo().getAmount().getAmount());
                        withdrawalAmount += Double.parseDouble(charge.getAmount());
                        source.setCharge(charge);
                        source.setTotal(new Amount(String.valueOf(withdrawalAmount), delivery.getOrder().getCurrency()));
                        sources.set(0, source);
                        getFxCharge(charge.getCurrency(), charge.getAmount(), withdrawalAmount);
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        openActivityOnTokenExpire();
                        break;
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getFxCharge(String currency, String amount, double withdrawalAmount) {
        Amount secondCharge = new Amount();
        FxRequest request = new FxRequest();
        Origin origin = new Origin();
        origin.setAmount(Double.valueOf(amount));
        origin.setCurrency(currency);
        request.setOrigin(origin);
        mViewModel.FxConversion(request);
        mViewModel.getmFxResponce().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        /*
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        fxResponse = resource.data;
                        mViewModel.getMfxResponse().setValue(fxResponse);*/
                        if (resource.data != null) {
                            secondCharge.setCurrency(resource.data.getDestination().getCurrency());
                            secondCharge.setAmount(String.valueOf(RoundOffLib.roundOffValue(resource.data.getDestination().getAmount())));
                            mViewModel.getCharge().setValue(secondCharge);
                            mBinding.chargeTextView.setText(resource.data.getDestination().getCurrency() + " " + resource.data.getDestination().getAmount());
                            getFxCharge2(transfer.getOrderInfo().getAmount().getCurrency(), withdrawalAmount);
                        }

                        break;
                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        mListener.onPaymentFailed(new TospayException(resource.message));
                        break;

                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case RE_AUTHENTICATE:
                        startActivityForResult(new Intent(getContext(), AuthActivity.class), AuthActivity.REQUEST_CODE_LOGIN);
                        break;
                }
            }
        });


    }

    private void executePayment() {
        Transfer payload = new Transfer();
        payload.setSource(sources);
        payload.setDeviceInfo(deviceInfo);
        isSocketNotified = false;
        shouldRun = true;
        count = 0;

        mViewModel.pay(paymentId, payload);
        mViewModel.getPaymentResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
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
                        if (resource.data != null) {
                            if (resource.data.getPinSet()!=null && resource.data.getPinSet()) {

                                showPinSetUp(getContext(),getView(),resource.data.getPinUrl(),null,resource.data);
                            }
                            else {

                                if (resource.data.getStatus().equalsIgnoreCase("PROCESSING")){

                                    if (resource.data.getHtml() != null ){
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        PayCard3ds cardPaymentDialog = new PayCard3ds(resource.data,this);
                                        cardPaymentDialog.show(getChildFragmentManager(), PayCard3ds.TAG);
                                        return;
                                    }
                                    delay(resource.data.getReference());
                                    return;
                                }

                                showCustomDialog(getContext(),getView(),resource.data.getStatus());
                                executeResponse = resource.data;
                                Toast.makeText(getActivity(), "Transaction pin is not set", Toast.LENGTH_SHORT).show();
                            }


                       /* try {
                            mViewModel.setIsError(false);
                            transferResponse = resource.data;
                            assert transferResponse != null;
                            if (transferResponse.getHtml() != null) {
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                CardPaymentDialog.newInstance(transferResponse.getHtml())
                                        .show(getChildFragmentManager(), CardPaymentDialog.TAG);
                            } else {
                                checkStatus(transferResponse.getId(), transferResponse.getReference(), transferResponse.getTransactionId());
                                *//*      new Handler().postDelayed(this::triggerTimer, 5000);*//*
                            }
                        } catch (Exception e) {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(true);
                            mViewModel.setErrorMessage("Sorry we could not complete your transaction");
                        }*/







                        } else {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(true);
                            mViewModel.setErrorMessage("Sorry we could not complete your transaction");
                        }


                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        openActivityOnTokenExpire();
                        break;
                }
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void getFxCharge2(String currency, double withdrawalAmount) {
        TotalDisp totalDisp = new TotalDisp();
        FxRequest request = new FxRequest();
        Origin origin = new Origin();
        origin.setAmount(withdrawalAmount);
        origin.setCurrency(currency);
        request.setOrigin(origin);
        mViewModel.FxConversion(request);
        mViewModel.getmFxResponce().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        fxResponse = resource.data;
                        mViewModel.getMfxResponse().setValue(fxResponse);
                        if (resource.data != null) {
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(false);
                            totalDisp.setCurrency(resource.data.getDestination().getCurrency());
                            totalDisp.setAmount(String.valueOf(RoundOffLib.roundOffValue(resource.data.getDestination().getAmount())));
                            mViewModel.getTotalDisplay().setValue(totalDisp);
                            Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                        break;
                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        mListener.onPaymentFailed(new TospayException(resource.message));
                        break;

                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case RE_AUTHENTICATE:
                        startActivityForResult(new Intent(getContext(), AuthActivity.class), AuthActivity.REQUEST_CODE_LOGIN);
                        break;
                }
            }
        });


    }


    private void triggerTimer() {
        if (!isSocketNotified) {
            runnable = new Runnable() {

                @Override
                public void run() {
                    checkTransactionStatus();

                    if (shouldRun) {
                        handler.postDelayed(this, Constants.RETRY_DELAY);
                    } else {
                        handler.removeCallbacks(runnable);
                    }
                }
            };

            handler.post(runnable);
        }
    }

    private void checkStatus( String reference) {
        StatusReq req = new StatusReq();
        req.setReference(reference);
        mViewModel.checkStatus(req);
        mViewModel.getStatusCheck().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        if (resource.data != null) {
                            if (resource.data.getStatus().equalsIgnoreCase("PROCESSING")) {
                                mViewModel.setIsLoading(true);
                                delay(reference);
                            } else if (resource.data.getStatus().equalsIgnoreCase("Success")) {
                                Intent intent = new Intent();
                                intent.putExtra("result", "Success");
                                getActivity().setResult(Activity.RESULT_OK, intent);
                                getActivity().finish();
                            } else if (resource.data.getStatus().equalsIgnoreCase("CREATED")) {
                                delay(reference);
                            } else {
                                mViewModel.setIsLoading(false);
                                mViewModel.setIsError(true);
                                mViewModel.setErrorMessage(resource.data.getReason());
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }

                        } else {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(true);
                            mViewModel.setErrorMessage("FAILED");
                        }
                        break;
                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;
                    case LOADING:
                        break;
                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        startActivityForResult(new Intent(getContext(), PinActivity.class), PinActivity.REQUEST_PIN);
                        break;
                }
            }
        });

    }

    private void delay( String reference) {

        if (count <= 5) {
            Handler handler = new Handler();
            new Thread(() -> {
                try {
                    Thread.sleep(interval);
                } catch (Exception e) {
                }
                handler.post(() -> {
                    count++;
                    checkStatus(reference);
                });
            }).start();
        } else {
            mViewModel.setIsLoading(false);
            mViewModel.setIsError(true);
            mViewModel.setErrorMessage("The process is taking too long to respond");
        }

    }


    private void checkTransactionStatus() {
        if (shouldRun) {
            if (count <= Constants.RETRY_MAX) {
                mViewModel.status(transferResponse);
                mViewModel.getTransferStatusResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
                    if (resource != null) {
                        switch (resource.status) {
                            case ERROR:
                                mViewModel.setIsLoading(false);
                                mViewModel.setIsError(true);
                                mViewModel.setErrorMessage(resource.message);
                                handler.removeCallbacks(runnable);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                break;

                            case SUCCESS:
                                count++;
                                if (resource.data != null) {

                                    status = resource.data.getStatus();
                                    if (status.equals(NotificationEvent.STATUS_SUCCESS)) {
                                        status = NotificationEvent.STATUS_SUCCESS;
                                        message = "Congratulation! Your Topup was successful";
                                        completeProcessing();
                                    } else {
                                        status = NotificationEvent.STATUS_FAILED;
                                        message = "Failed to complete transaction. Please try again";
                                    }
                                } else {
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    handler.removeCallbacks(runnable);
                                    mViewModel.setIsLoading(false);
                                    mViewModel.setIsError(true);
                                    mViewModel.setErrorMessage("Unable to process this transaction");
                                }
                                break;

                            case RE_AUTHENTICATE:
                                shouldRun = false;
                                isSocketNotified = false;
                                mViewModel.setIsLoading(false);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                startActivityForResult(new Intent(getContext(), PinActivity.class), PinActivity.REQUEST_PIN);
                                break;
                        }
                    }
                });
            } else {
                completeProcessing();
            }
        } else {
            completeProcessing();
        }
    }

    private void completeProcessing() {
        handler.removeCallbacksAndMessages(null);
        if (status.equals(NotificationEvent.STATUS_SUCCESS)) {
            Intent intent = new Intent();
            intent.putExtra("result", "Success");
            getActivity().setResult(1, intent);
            getActivity().finish();
            /*m
            mListener.onPaymentSuccess(transferResponse, title, message);*/
        } /*else {
            Intent intent = new Intent();
            intent.putExtra("result","failed");
            getActivity().setResult(1,intent);
            getActivity().finish();*//*
            mListener.onPaymentFailed(new TospayException(message));*//*
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPaymentNotification(NotificationEvent event) {
        if (event != null) {
            Payload payload = event.getPayload();
            switch (payload.getTopic()) {
                case NotificationEvent.TOPUP:
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (!payload.getStatus().equals(NotificationEvent.STATUS_FAILED)) {
                        fetchAccounts();
                    } else {
                        TransferDialog.newInstance(event).show(getChildFragmentManager(), TransferDialog.TAG);
                    }
                    break;

                case NotificationEvent.PAYMENT:
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    title = event.getNotification().getTitle();
                    message = event.getNotification().getBody();

                    shouldRun = false;
                    isSocketNotified = true;
                    handler.removeCallbacksAndMessages(null);

                    mViewModel.setIsLoading(false);
                    if (payload.getStatus().equals(NotificationEvent.STATUS_FAILED)) {
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(payload.getReason());
                    } else {
                        mListener.onPaymentSuccess(transferResponse, title, message);
                    }
                    break;
            }
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
        handler.removeCallbacksAndMessages(null);
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
        MpesaLoadingDialog.newInstance(transferResponse)
                .show(getChildFragmentManager(), MpesaLoadingDialog.TAG);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private DeviceInfo getLocation() {
        if (DeviceDetails.getLocationRequest(getActivity()) != null){
            LocationReq req = DeviceDetails.getLocationRequest(getActivity());
            mViewModel.getGeolocations(req);
            mViewModel.setIsLoading(true);
            mViewModel.setIsError(false);
            Handler handler = new Handler();
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
                handler.post(() -> {
                    mViewModel.getmLocationRequest().observe(getViewLifecycleOwner(), response -> {
                        if (response != null) {
                            if (response.getLocation() != null) {
                                mViewModel.setIsLoading(false);
                                mViewModel.setIsError(false);
                                deviceInfo.setLocation_accuracy(response.getAccuracy().toString());
                                deviceInfo.setLatitude(response.getLocation().getLat().toString());
                                deviceInfo.setLongitude(response.getLocation().getLng().toString());
                                if (DeviceDetails.readDevice(getActivity()) != null){
                                    deviceInfo.setNetworkCountryIso(DeviceDetails.readDevice(getActivity()).getNetworkCountryIso());
                                    deviceInfo.setImei(DeviceDetails.readDevice(getActivity()).getImei());
                                    deviceInfo.setIp(DeviceDetails.readDevice(getActivity()).getIp());
                                    deviceInfo.setOsVersion(DeviceDetails.readDevice(getActivity()).getOsVersion());
                                    deviceInfo.setPhone(DeviceDetails.readDevice(getActivity()).getPhone());
                                    deviceInfo.setPhoneSerial(DeviceDetails.readDevice(getActivity()).getPhoneSerial());
                                    deviceInfo.setSimCardSerial(DeviceDetails.readDevice(getActivity()).getSimCardSerial());
                                    deviceInfo.setUserAgent("");
                                    //do add accuracy
                                    performChargeLookup();
                                }
                                else{

                                }

                            }
                        }
                    });
                });
            }).start();

            return deviceInfo;

        }
       else {
          statusCheck();
           return null;
        }

    }




    private void showCustomDialog( Context context, View view,String status) {
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_transaction_notification, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        TextView notification = dialogView.findViewById(R.id.notification);
        notification.setText("Your trasaction status flag is marked as "+status+" this is because your transaction pin has not been set");
        Button btn_delete = dialogView.findViewById(R.id.set_up_pin);
        btn_delete.setOnClickListener(v -> {
            getTransactionPinUrl(context,view);
            alertDialog.dismiss();
        });
    }

    private void getTransactionPinUrl(Context context, View view){
        mViewModel.getTransactionPinUrl("Bearer "+getSharedPrefManager().getAccessToken());
        mViewModel.getPinsetupUrl().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null){
                switch (resource.status){
                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        break;
                    case LOADING:

                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        showPinSetUp(context,view,resource.data.getPinUrl(),"Set-up", null);
                        break;

                    case RE_AUTHENTICATE:

                        break;
                }
            }
        });
    }


    private void showPinSetUp(Context context, View view, String url, String flag, ExecuteResponse data) {
        DialogTransactionLock dialogTransactionLock = new DialogTransactionLock(this,url,null,flag,data);
        dialogTransactionLock.show(getChildFragmentManager(), DialogTransactionLock.TAG);
    }


    @Override
    public void onTransactionLockedClosed(boolean transactionClosed, TospayUser user, String flag, ExecuteResponse data) {
        if (flag != null && flag.equalsIgnoreCase("Set-up")){
            executePayment();
            return;
        }
        checkStatus(data.getReference());
    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Location service is required before a transaction can made, please turn on location")
                .setCancelable(false)
                .setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onPayment3dsClose(ExecuteResponse data) {
        checkStatus(data.getReference());
    }
}
