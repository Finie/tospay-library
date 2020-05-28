package net.tospay.auth.ui.account.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLinkMobileAccountBinding;
import net.tospay.auth.interfaces.IOnBackPressed;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.repository.MobileRepository;
import net.tospay.auth.remote.service.MobileService;
import net.tospay.auth.ui.account.account_fragments.liking.LinkAct;
import net.tospay.auth.ui.auth.pinAuth.PinActivity;
import net.tospay.auth.viewmodelfactory.MobileViewModelFactory;
import net.tospay.auth.ui.account.AccountSelectionFragmentDirections;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.ui.dialog.country.CountryDialog;
import net.tospay.auth.ui.dialog.network.NetworkDialog;

public class LinkMobileAccountFragment extends BaseFragment<FragmentLinkMobileAccountBinding, MobileMoneyViewModel>
        implements CountryDialog.CountrySelectedListener,
        NetworkDialog.NetworkSelectedListener, MobileMoneyNavigator, IOnBackPressed {

    private Country country = null;
    private Network network = null;
    private FragmentLinkMobileAccountBinding mBinding;
    private MobileMoneyViewModel mViewModel;
    private String from;
    private static final String TAG = "LinkMobileAccountFragme";
    public LinkMobileAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.mobileMoneyViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_link_mobile_account;
    }

    @Override
    public MobileMoneyViewModel getViewModel() {
        MobileService service = ServiceGenerator.createService(MobileService.class, getContext());
        MobileRepository repository = new MobileRepository(getAppExecutors(), service);
        MobileViewModelFactory factory = new MobileViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(MobileMoneyViewModel.class);
        return mViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        assert getArguments() != null;
        from = LinkMobileAccountFragmentArgs.fromBundle(getArguments()).getFrom();
        mBinding.setMobileMoneyViewModel(mViewModel);
        mViewModel.setNavigator(this);
        mBinding.btnBackImageView.setOnClickListener(view1 -> {
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
    public void onNetworkSelected(Network network) {
        this.network = network;
        mViewModel.getNetwork().setValue(network);
    }

    @Override
    public void onSelectCountryClick(View view) {
        CountryDialog.newInstance(GatewayRepository.CountryType.MOBILE)
                .show(getChildFragmentManager(), CountryDialog.TAG);
    }

    @Override
    public void onNetworkCountryClick(View view) {
        if (country == null) {
            mBinding.selectCountryTextView.setError("Please select country");
            return;
        }

        NetworkDialog.newInstance(country.getIso()).show(getChildFragmentManager(), NetworkDialog.TAG);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onConfirmClick(View view) {
        mBinding.selectCountryTextView.setError(null);
        mBinding.selectNetworkTextView.setError(null);

        if (country == null) {
            mBinding.selectCountryTextView.setError("Country is required");
            return;
        }

        if (network == null) {
            mBinding.selectNetworkTextView.setError("Network is required");
            return;
        }

        String phone = mBinding.phoneEditText.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "Phone is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String alias = mBinding.nameEditText.getText().toString();

        Account account = new Account();
        account.setAlias(alias);
        account.setNetwork(network.getOperator());
        account.setTrunc(phone.substring(phone.length() - 4));

        mViewModel.link(phone, mBinding.nameEditText.getText().toString());
        mViewModel.getMobileResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);

                        if (resource.data != null) {
                            account.setId(resource.data.getId());
                        }

                        AccountSelectionFragmentDirections.ActionNavigationAccountSelectionToNavigationVerifyMobile action =
                                AccountSelectionFragmentDirections
                                        .actionNavigationAccountSelectionToNavigationVerifyMobile(account);

                        NavHostFragment.findNavController(this).navigate(action);

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
                        openActivityOnTokenExpire();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthActivity.REQUEST_CODE_LOGIN || requestCode == PinActivity.REQUEST_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                reloadBearerToken();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }
}
