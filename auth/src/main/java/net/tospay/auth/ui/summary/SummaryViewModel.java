package net.tospay.auth.ui.summary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.model.TospayUser;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.ui.account.bank.model.Destination;
import net.tospay.auth.ui.account.bank.model.FxRequest;
import net.tospay.auth.ui.account.bank.model.FxResponse;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.Objects;

public class SummaryViewModel extends BaseViewModel<SummaryNavigator>
        implements SwipeRefreshLayout.OnRefreshListener {

    private final PaymentRepository paymentRepository;
    private LiveData<Resource<Transfer>> detailsResourceLiveData;
    private LiveData<Resource<FxResponse>> mFxResponce;
    private MutableLiveData<Transfer> transfer;
    private MutableLiveData<TospayUser> user;
    private MutableLiveData<FxResponse> fxResponse;
    public SummaryViewModel(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.transfer = new MutableLiveData<>();
        this.user = new MutableLiveData<>();
        this.fxResponse = new MutableLiveData<>();
    }

    public MutableLiveData<Transfer> getTransfer() {
        return transfer;
    }


    public MutableLiveData<TospayUser> getUser() {
        return user;
    }

    public void details(String paymentId) {
        detailsResourceLiveData = paymentRepository.details(paymentId);
    }

    public void FxConversion(FxRequest request){

        Destination destination = new Destination();
        destination.setCurrency(getUser().getValue().getCurrency());
        request.setDestination(destination);
        mFxResponce = paymentRepository.fxConverter(request);
    }
    public LiveData<Resource<Transfer>> getDetailsResourceLiveData() {
        return detailsResourceLiveData;
    }

    public MutableLiveData<FxResponse> getFxResponse() {
        return fxResponse;
    }

    @Override
    public void onRefresh() {
        getNavigator().onRefresh();
    }

    public LiveData<Resource<FxResponse>> getmFxResponce() {
        return mFxResponce;
    }
}
