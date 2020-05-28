package net.tospay.auth.ui.account.bank;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.model.Bank;
import net.tospay.auth.model.Country;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.request.BankRequest;
import net.tospay.auth.remote.response.AccountLinkResponse;
import net.tospay.auth.remote.response.LinkBankResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.ui.account.bank.bank_request.LinkBankReq;
import net.tospay.auth.ui.account.bank.model.BankOtpRequest;
import net.tospay.auth.ui.account.bank.model.Branch;
import net.tospay.auth.ui.account.bank.model.Currency;
import net.tospay.auth.ui.account.bank.model.ResendOtp;
import net.tospay.auth.ui.base.BaseViewModel;
import net.tospay.auth.utils.SharedPrefManager;

import java.util.List;

public class BankViewModel extends BaseViewModel<BankNavigator> implements View.OnClickListener {

    private MutableLiveData<Country> country = new MutableLiveData<>();
    private MutableLiveData<Bank> bank = new MutableLiveData<>();
    private MutableLiveData<Currency> currency = new MutableLiveData<>();
    private MutableLiveData<Branch> branch = new MutableLiveData<>();
    private BankRepository bankRepository;
    private GatewayRepository gatewayRepository;
    private SharedPrefManager sharedPrefManager;
    private LiveData<Resource<Result>> BankVerify;
    private MutableLiveData<String> phone = new MutableLiveData<>();
    private MutableLiveData<String> otp = new MutableLiveData<>();
    private LiveData<Resource<LinkBankResponse>> linkResourceLiveData;
    private LiveData<Resource<Result>> resendOtp;
    private LiveData<Resource<List<Bank>>> banksResourceLiveData;
    private LiveData<Resource<List<Branch>>> mbranchLiveData;
    LiveData<Resource<List<Currency>>> mCurrency;

    public BankViewModel(BankRepository bankRepository, GatewayRepository gatewayRepository,SharedPrefManager sharedPrefManager) {
        this.bankRepository = bankRepository;
        this.gatewayRepository = gatewayRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public BankViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public MutableLiveData<Country> getCountry() {
        return country;
    }

    public MutableLiveData<Bank> getBank() {
        return bank;
    }

    public LiveData<Resource<LinkBankResponse>> getLinkResourceLiveData() {
        return linkResourceLiveData;
    }

    public void link(LinkBankReq request) {
        linkResourceLiveData = bankRepository.link(getBearerToken().get(), request);
    }

    public void resendOtp(ResendOtp resendOtp1){
        resendOtp  = bankRepository.resendOtp(getBearerToken().get(),resendOtp1);
    }

    public LiveData<Resource<Result>> getResendOtp() {
        return resendOtp;
    }

    public LiveData<Resource<List<Bank>>> getBanksResourceLiveData() {
        return banksResourceLiveData;
    }

    public void banks(String country_name) {
        banksResourceLiveData = gatewayRepository.banks(getBearerToken().get(), country_name,"0","100");
    }

    public MutableLiveData<Branch> getBranch() {
        return branch;
    }

    public  void  branch(String bank_id){
        mbranchLiveData = gatewayRepository.branch(getBearerToken().get(),bank_id,"0","100");
    }

    public LiveData<Resource<List<Branch>>> getMbranchLiveData() {
        return mbranchLiveData;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.selectCountryTextView) {
            getNavigator().onSelectCountryClick(view);

        } else if (view.getId() == R.id.selectBankTextView) {
            getNavigator().onSelectBankClick(view);

        } else if (view.getId() == R.id.btn_save) {
            getNavigator().onDone(view);
        }
        else if (view.getId()==R.id.selectCurrency){
            getNavigator().selectCurrency();
        }
        else if (view.getId()==R.id.selectBranch){
            getNavigator().selectBranch();
        }
        else if (view.getId()==R.id.btn_verify_otp_save){
            getNavigator().onVerify();
        }

    }

    public void fetchCurrencies(){
        mCurrency = gatewayRepository.currencies();
    }

    public MutableLiveData<Currency> getCurrency() {
        return currency;
    }

    public LiveData<Resource<List<Currency>>> getmCurrency() {
        return mCurrency;
    }

    public MutableLiveData<String> getOtp() {
        return otp;
    }

    public MutableLiveData<String> getPhone() {
        return phone;
    }

    public void verifyLinkBank(BankOtpRequest request){
        BankVerify = bankRepository.verifyBankLink(getBearerToken().get(),request);
    }

    public LiveData<Resource<Result>> getBankVerify() {
        return BankVerify;
    }
}
