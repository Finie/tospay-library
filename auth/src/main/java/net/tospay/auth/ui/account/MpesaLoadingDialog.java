package net.tospay.auth.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.databinding.DialogMpesaLoadingBinding;
import net.tospay.auth.event.NotificationEvent;
import net.tospay.auth.event.Payload;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.TransferResponse;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.auth.pinAuth.PinActivity;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MpesaLoadingDialog extends BottomSheetDialogFragment {

    public static final String TAG = "MpesaLoadingDialog";
    private static final String KEY_TRANSFER_RESPONSE = "transfer_response";

    private AccountViewModel mViewModel;
    private DialogMpesaLoadingBinding mBinding;
    private SharedPrefManager mSharedPrefManager;

    private TransferResponse transferResponse;
    private String status = NotificationEvent.STATUS_FAILED, message = "Error Unknown";
    private final Handler handler = new Handler();
    private Runnable runnable;
    private boolean shouldRun = true, isSocketNotified = false;
    private int count = 0;

    public MpesaLoadingDialog() {
        // Required empty public constructor
    }

    public static MpesaLoadingDialog newInstance(TransferResponse transferResponse) {
        MpesaLoadingDialog fragment = new MpesaLoadingDialog();
        Bundle args = new Bundle();
        args.putParcelable(KEY_TRANSFER_RESPONSE, transferResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);
        mSharedPrefManager = SharedPrefManager.getInstance(getContext());

        if (getArguments() != null) {
            transferResponse = getArguments().getParcelable(KEY_TRANSFER_RESPONSE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_mpesa_loading, container, false);
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

        view.findViewById(R.id.btn_done).setOnClickListener(view1 -> {
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }

            dismiss();
        });

        new Handler().postDelayed(this::triggerTimer, Constants.RETRY_DELAY);
    }

    private void reloadBearerToken() {
        String bearerToken = "Bearer " + mSharedPrefManager.getAccessToken();
        mViewModel.setBearerToken(bearerToken);
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

    private void completeProcessing() {
        handler.removeCallbacksAndMessages(null);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Payload payload = new Payload(NotificationEvent.TOPUP, status, message);
        NotificationEvent notification = new NotificationEvent(payload);
        EventBus.getDefault().post(notification);
        dismiss();
    }

    private void checkTransactionStatus() {
        if (shouldRun) {
            if (count <= Constants.RETRY_MAX) {
                mViewModel.status(transferResponse);
                mViewModel.getTransferStatusResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
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
                                count++;
                                mViewModel.setIsLoading(false);
                                mViewModel.setIsError(false);
                                if (resource.data != null) {
                                    status = resource.data.getStatus();
                                    if (status.equals(NotificationEvent.STATUS_SUCCESS)) {
                                        status = NotificationEvent.STATUS_SUCCESS;
                                        message = "Congratulation! Your Topup was successful";
                                        completeProcessing();
                                    } else {
                                        shouldRun = false;
                                        status = NotificationEvent.STATUS_FAILED;
                                        message = "Failed to complete transaction. Please try again";
                                    }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(NotificationEvent notification) {
        handler.removeCallbacksAndMessages(null);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (notification != null) {
            if (notification.getPayload().getTopic().equals(NotificationEvent.TOPUP)) {
                isSocketNotified = true;
                shouldRun = false;
                dismiss();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PinActivity.REQUEST_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                reloadBearerToken();
                triggerTimer();
            }
        }
    }
}
