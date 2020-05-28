package net.tospay.auth.ui.account;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.model.TotalDisp;
import net.tospay.auth.model.TransactionPin;
import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.Store;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.ExecuteResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.response.TransferResponse;
import net.tospay.auth.ui.account.bank.model.Destination;
import net.tospay.auth.ui.account.bank.model.FxRequest;
import net.tospay.auth.ui.account.bank.model.FxResponse;
import net.tospay.auth.ui.account.topup.StatusReq;
import net.tospay.auth.ui.account.topup.StatusRes;
import net.tospay.auth.ui.base.BaseViewModel;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.device_model.LocationRes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AccountViewModel extends BaseViewModel<AccountNavigator>
        implements SwipeRefreshLayout.OnRefreshListener {

    private ObservableBoolean isEmpty;

    private AccountRepository accountRepository;
    private PaymentRepository paymentRepository;
    private UserRepository userRepository;
    private LiveData<Resource<StatusRes>> statusCheck;
    private LiveData<Resource<List<AccountType>>> accountsResourceLiveData;
    private LiveData<Resource<Amount>> amountResourceLiveData;
    private LiveData<Resource<ExecuteResponse>> paymentResourceLiveData;
    private LiveData<Resource<TransferResponse>> transferResourceLiveData;
    private LiveData<Resource<TransferResponse>> transferStatusResourceLiveData;
    private LiveData<Resource<Result>> resultResourceLiveData;
    private MutableLiveData<FxResponse> mfxResponse;
    private LiveData<Resource<FxResponse>> mFxResponce;
    private MutableLiveData<Transfer> transfer;
    private MutableLiveData<Amount> charge;
    private MutableLiveData<Amount> secondCharge;
    private MutableLiveData<Account> account;
    private MutableLiveData<Store> source;
    private MutableLiveData<TospayUser> user;
    private LiveData<Resource<List<Account>>> cardAccount, mobileAccount, bankAccount;
    private MutableLiveData<TotalDisp> totalDisplay;
    private LiveData<LocationRes> mLocationRequest;
    private LiveData<Resource<TransactionPin>> pinsetupUrl;

    public AccountViewModel(AccountRepository accountRepository, PaymentRepository paymentRepository,UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.isEmpty = new ObservableBoolean();
        this.transfer = new MutableLiveData<>();
        this.account = new MutableLiveData<>();
        this.charge = new MutableLiveData<>();
        this.source = new MutableLiveData<>();
        this.mfxResponse = new MutableLiveData<>();
        this.user = new MutableLiveData<>();
        this.secondCharge = new MutableLiveData<>();
        this.totalDisplay = new MutableLiveData<>();
    }

    public MutableLiveData<Transfer> getTransfer() {
        return transfer;
    }

    public LiveData<Resource<List<AccountType>>> getAccountsResourceLiveData() {
        return accountsResourceLiveData;
    }

    public LiveData<Resource<ExecuteResponse>> getPaymentResourceLiveData() {
        return paymentResourceLiveData;
    }

    public MutableLiveData<TotalDisp> getTotalDisplay() {
        return totalDisplay;
    }

    public MutableLiveData<Amount> getSecondCharge() {
        return secondCharge;
    }

    public void FxConversion(FxRequest request){
        Destination destination = new Destination();
        destination.setCurrency(Objects.requireNonNull(getUser().getValue()).getCurrency());
        request.setDestination(destination);
        mFxResponce = paymentRepository.fxConverter(request);
    }

    public LiveData<Resource<FxResponse>> getmFxResponce() {
        return mFxResponce;
    }

    public MutableLiveData<TospayUser> getUser() {
        return user;
    }

    public MutableLiveData<Account> getAccount() {
        return account;
    }

    public MutableLiveData<Amount> getCharge() {
        return charge;
    }

    public MutableLiveData<Store> getSource() {
        return source;
    }

    public void fetchAccounts(boolean showWallet) {
        String bearerToken = getBearerToken().get();
        accountsResourceLiveData = accountRepository.accounts(bearerToken, showWallet);
    }

    public void pay(String paymentId, Transfer transfer) {
        String bearerToken = getBearerToken().get();
        paymentResourceLiveData = paymentRepository.pay(bearerToken, paymentId, transfer);
    }

    public void chargeLookup(Transfer transfer, String type) {
        amountResourceLiveData = paymentRepository.chargeLookup(getBearerToken().get(), transfer, type);
    }

    public LiveData<Resource<Amount>> getAmountResourceLiveData() {
        return amountResourceLiveData;
    }

    public void topup(Transfer transfer) {
        transferResourceLiveData = paymentRepository.transfer(getBearerToken().get(), transfer);
    }

    public LiveData<Resource<TransferResponse>> getTransferResourceLiveData() {
        return transferResourceLiveData;
    }

    public void status(TransferResponse response) {
        transferStatusResourceLiveData = paymentRepository.status(getBearerToken().get(), response);
    }

    public LiveData<Resource<TransferResponse>> getTransferStatusResourceLiveData() {
        return transferStatusResourceLiveData;
    }

    @Override
    public void onRefresh() {
        getNavigator().onRefresh();
    }

    public ObservableBoolean getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty.set(isEmpty);
    }

    public void fetchCards() {
        cardAccount = accountRepository.getCards(getBearerToken().get());
    }

    public void fetchMobile() {
        mobileAccount = accountRepository.getMobile(getBearerToken().get());
    }

    public void fetchBank() {
        bankAccount = accountRepository.getBank(getBearerToken().get());
    }

    public LiveData<Resource<List<Account>>> getBankAccount() {
        return bankAccount;
    }

    public LiveData<Resource<List<Account>>> getMobileAccount() {
        return mobileAccount;
    }

    public LiveData<Resource<List<Account>>> getCardAccount() {
        return cardAccount;
    }

    public MutableLiveData<FxResponse> getMfxResponse() {
        return mfxResponse;
    }

    public void delete(Account account, int accountType) {
        Map<String, Object> param = new HashMap<>();
        param.put("account_id", account.getId());
        if (accountType == AccountType.MOBILE) {
            param.put("type", "mobile");
            resultResourceLiveData = accountRepository.delete(getBearerToken().get(), param);
        } else if (accountType == AccountType.CARD) {
            param.put("type", "card");
            resultResourceLiveData = accountRepository.delete(getBearerToken().get(), param);
        }
        else if (accountType == AccountType.BANK) {
            param.put("type", "bank");
            resultResourceLiveData = accountRepository.delete(getBearerToken().get(), param);
        }

    }

    public void checkStatus(StatusReq request) {
        statusCheck = accountRepository.getStatus(getBearerToken().get(), request);
    }

    public LiveData<Resource<StatusRes>> getStatusCheck() {
        return statusCheck;
    }


    public LiveData<Resource<Result>> getResultResourceLiveData() {
        return resultResourceLiveData;
    }

    public void getGeolocations(LocationReq request){
        mLocationRequest =  accountRepository.getGeoLocation(request);
    }

    public LiveData<LocationRes>  getmLocationRequest() {
        return mLocationRequest;
    }


    public void getTransactionPinUrl(String bearer){
        pinsetupUrl = userRepository.getTransactionPinUrl(bearer);
    }

    public LiveData<Resource<TransactionPin>> getPinsetupUrl() {
        return pinsetupUrl;
    }


}
