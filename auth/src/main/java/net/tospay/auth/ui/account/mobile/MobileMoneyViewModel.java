package net.tospay.auth.ui.account.mobile;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.MobileRepository;
import net.tospay.auth.remote.request.MobileAccountVerificationRequest;
import net.tospay.auth.remote.request.MobileRequest;
import net.tospay.auth.remote.response.AccountLinkResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.HashMap;
import java.util.Map;

public class MobileMoneyViewModel extends BaseViewModel<MobileMoneyNavigator> implements View.OnClickListener {

    private MutableLiveData<Country> country = new MutableLiveData<>();
    private MutableLiveData<Network> network = new MutableLiveData<>();
    private MutableLiveData<String> phone = new MutableLiveData<>();
    public MutableLiveData<String> otp = new MutableLiveData<>();

    private LiveData<Resource<AccountLinkResponse>> mobileResourceLiveData;
    private LiveData<Resource<Result>> resendResourceLiveData;
    private LiveData<Resource<Result>> verifyResourceLiveData;

    private final MobileRepository repository;

    public MobileMoneyViewModel(MobileRepository repository) {
        this.repository = repository;
    }

    public MutableLiveData<Country> getCountry() {
        return country;
    }

    public MutableLiveData<Network> getNetwork() {
        return network;
    }

    public MutableLiveData<String> getPhone() {
        return phone;
    }

    public MutableLiveData<String> getOtp() {
        return otp;
    }

    public void setOtp(MutableLiveData<String> otp) {
        this.otp = otp;
    }

    public LiveData<Resource<AccountLinkResponse>> getMobileResourceLiveData() {
        return mobileResourceLiveData;
    }

    public LiveData<Resource<Result>> getResendResourceLiveData() {
        return resendResourceLiveData;
    }

    public LiveData<Resource<Result>> getVerifyResourceLiveData() {
        return verifyResourceLiveData;
    }

    public void link(String phone, String name) {
        MobileRequest request = new MobileRequest();
        request.setCountry(country.getValue());
        request.setNetwork(network.getValue());
        request.setNumber(phone);
        request.setAlias(name);

        mobileResourceLiveData = repository.link(getBearerToken().get(), request);
    }

    public void verify(String accountId) {
        String otp = getOtp().getValue();
        MobileAccountVerificationRequest request
                = new MobileAccountVerificationRequest(accountId, otp);
        verifyResourceLiveData = repository.verify(getBearerToken().get(), request);
    }

    public void resend(String accountId) {
        Map<String, Object> request = new HashMap<>();
        request.put("id", accountId);
        resendResourceLiveData = repository.resend(getBearerToken().get(), request);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.selectCountryTextView) {
            getNavigator().onSelectCountryClick(view);

        } else if (view.getId() == R.id.selectNetworkTextView) {
            getNavigator().onNetworkCountryClick(view);

        } else if (view.getId() == R.id.btn_save) {
            getNavigator().onConfirmClick(view);

        } else if (view.getId() == R.id.btn_resend) {
            getNavigator().onResendClick(view);

        } else if (view.getId() == R.id.btn_verify_phone) {
            getNavigator().onConfirmClick(view);
        }
    }
}
