package net.tospay.auth.ui.account.bank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLinkBankBinding;
import net.tospay.auth.interfaces.IOnBackPressed;
import net.tospay.auth.model.Bank;
import net.tospay.auth.model.Country;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.service.BankService;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.ui.account.account_fragments.liking.LinkAct;
import net.tospay.auth.ui.account.bank.bank_request.LinkBankReq;
import net.tospay.auth.ui.account.bank.model.Branch;
import net.tospay.auth.ui.account.bank.model.Currency;
import net.tospay.auth.ui.account.bank.model.VerifyD;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.auth.pinAuth.PinActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.dialog.country.CountryDialog;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.BankViewModelFactory;

public class LinkBankFragment extends BaseFragment<FragmentLinkBankBinding, BankViewModel>
        implements CountryDialog.CountrySelectedListener, BankDialog.OnBankSelectedListener, BankNavigator
        , CurrencyDialog.OnCurrencySelectedListener, BranchDialog.OnBranchSelectedListener, IOnBackPressed {

    private Country country = null;
    private Bank bank = null;
    private FragmentLinkBankBinding mBinding;
    private BankViewModel mViewModel;
    private Currency currency = null;
    private Branch branch = null;
    private LinkBankReq request;
    private String from;
    private static final String TAG = "LinkBankFragment";

    @Override
    public int getBindingVariable() {
        return BR.bankViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_link_bank;
    }

    @Override
    public BankViewModel getViewModel() {
        BankService bankService = ServiceGenerator.createService(BankService.class, getContext());
        GatewayService gatewayService = ServiceGenerator.createService(GatewayService.class, getContext());

        BankRepository bankRepository = new BankRepository(getAppExecutors(), bankService);
        GatewayRepository gatewayRepository = new GatewayRepository(getAppExecutors(), gatewayService);
        SharedPrefManager sharedPrefManager = getSharedPrefManager();
        BankViewModelFactory factory = new BankViewModelFactory(bankRepository, gatewayRepository, sharedPrefManager);
        mViewModel = ViewModelProviders.of(this, factory).get(BankViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        from = LinkBankFragmentArgs.fromBundle(getArguments()).getFrom();
        mBinding = getViewDataBinding();
        mBinding.setBankViewModel(mViewModel);
        mViewModel.setNavigator(this);
        mBinding.btnBackImageView.setOnClickListener(v -> {
            if(from.equalsIgnoreCase("account")){
                Intent intent = new Intent();
                intent.putExtra("result","Linking  canceled");
                getActivity().setResult(LinkAct.LINK_ACCOUT_REQUEST,intent);
                getActivity().finish();
            }
            else{
                Navigation.findNavController(view).navigateUp();
            }

        });
    }

    @Override
    public void onCountrySelected(Country country) {
        this.country = country;
        mViewModel.getCountry().setValue(country);
    }

    @Override
    public void onSelectCountryClick(View view) {
        CountryDialog.newInstance(GatewayRepository.CountryType.MOBILE)
                .show(getChildFragmentManager(), CountryDialog.TAG);
    }

    @Override
    public void onSelectBankClick(View view) {
        if (country == null) {
            mBinding.selectCountryTextView.setError("Please select country");
            return;
        }

        BankDialog.newInstance(country.getName()).show(getChildFragmentManager(), BankDialog.TAG);
    }

    @Override
    public void onDone(View view) {
        performTransaction();
    }

    private void performTransaction() {
        mBinding.selectCountryTextView.setError(null);
        mBinding.selectBankTextView.setError(null);
        mBinding.phoneNumber.setError(null);
        mBinding.phoneInputLayout.setErrorEnabled(false);
        mBinding.phoneInputLayout.setError(null);
        mBinding.accountNoInputLayout.setErrorEnabled(false);
        mBinding.accountNoInputLayout.setError(null);

        if (country == null) {
            mBinding.selectCountryTextView.setError("Country is required");
            return;
        }

        String phone = mBinding.phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            mBinding.phoneInputLayout.setErrorEnabled(true);
            mBinding.phoneInputLayout.setError("Phone is required");
            return;
        }

        if (bank == null) {
            mBinding.selectBankTextView.setError("Bank is required");
            return;
        }
        if (currency == null) {
            mBinding.selectCurrency.setError("Currency is required");
        }

        if (branch == null) {
            mBinding.selectBranch.setError("Bank Branch is required");
        }

        String accountName = mBinding.accountName.getText().toString();
        if (TextUtils.isEmpty(accountName)) {
            mBinding.accountNameInputField.setErrorEnabled(true);
            mBinding.accountNameInputField.setError("Account name is required");

        }
        String alliasName = mBinding.aliasName.getText().toString();
        if (TextUtils.isEmpty(alliasName)) {
            mBinding.aliasNameLayout.setErrorEnabled(true);
            mBinding.aliasNameLayout.setError("Alias name is required");

        }
        String accountNo = mBinding.accountNumber.getText().toString();
        if (TextUtils.isEmpty(accountNo)) {
            mBinding.accountNoInputLayout.setErrorEnabled(true);
            mBinding.accountNoInputLayout.setError("Account Number is required");
            return;
        }
        String customerNumber = accountNo.substring(5, 12);
        if (TextUtils.isEmpty(customerNumber)) {
            return;
        }


        if (currency == null) {
            Toast.makeText(getActivity(), "Currency not selected", Toast.LENGTH_SHORT).show();
            mBinding.selectCurrency.setError("please select currency");
            return;
        }
        String Signatories = mBinding.signatories.getText().toString();
        if (TextUtils.isEmpty(Signatories)) {
            mBinding.signatoryLayout.setErrorEnabled(true);
            mBinding.signatories.setError("Signatories required");

        }


        request = new LinkBankReq();
        net.tospay.auth.ui.account.bank.bank_request.Bank bank_ke = new net.tospay.auth.ui.account.bank.bank_request.Bank();
        bank_ke.setBankCode(bank.getCode());
        bank_ke.setCountryName(country.getName());
        bank_ke.setCustomerPhone(phone);
        bank_ke.setAccountNumber(accountNo);
        bank_ke.setAccountAlias(alliasName);
        bank_ke.setAccountName(accountName);
        bank_ke.setBankAbbrv(bank.getAbbr());
        bank_ke.setBranchToken(branch.getToken());
        bank_ke.setCountryCode(country.getIso());
        bank_ke.setCustomerNumber(customerNumber);
        bank_ke.setSignatories(Signatories);
        net.tospay.auth.ui.account.bank.bank_request.Currency currency1 = new net.tospay.auth.ui.account.bank.bank_request.Currency();
        currency1.setName(currency.getCurrency());
        currency1.setIso(country.getIso());
        bank_ke.setCurrency(currency1);
        request.setBank(bank_ke);
        mViewModel.link(request);
        mViewModel.getLinkResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        if (resource.data != null) {
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(false);
                            VerifyD verifyD = new VerifyD();
                            verifyD.setPhone(phone);
                            verifyD.setCode(bank.getCode());
                            NavHostFragment.findNavController(this)
                                    .navigate(LinkBankFragmentDirections.actionNavigateToVerifyBank(verifyD, resource.data, request));
                        } else {
                            mViewModel.setIsLoading(false);
                            mViewModel.setIsError(true);
                            mViewModel.setErrorMessage("Sorry we did not get any response");
                        }
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        startActivityForResult(new Intent(getContext(), PinActivity.class), Activity.RESULT_OK);
                        break;
                }
            }
        });
    }

    @Override
    public void onBankSelected(Bank bank) {
        this.bank = bank;
        mViewModel.getBank().setValue(bank);
    }

    @Override
    public void selectCurrency() {
        CurrencyDialog.newInstance().show(getChildFragmentManager(), CurrencyDialog.TAG);
    }

    @Override
    public void selectBranch() {
        if (bank == null) {
            mBinding.selectBankTextView.setError("Bank is required before branch selection");
            return;
        }
        BranchDialog.newInstance(bank.getId()).show(getChildFragmentManager(), BranchDialog.TAG);
    }

    @Override
    public void OnCurrencySelected(Currency currency) {
        this.currency = currency;
        mViewModel.getCurrency().setValue(currency);
    }

    @Override
    public void onBrabchSelected(Branch branch) {
        this.branch = branch;
        mBinding.selectBranch.setText(branch.getBranchName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthActivity.REQUEST_CODE_LOGIN || requestCode == PinActivity.REQUEST_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                performTransaction();
            }
        }
    }


    @Override
    public boolean onBackPressed() {
        Log.e(TAG, "onViewCreated: "+"back pressed" );
        return true;

    }
}
