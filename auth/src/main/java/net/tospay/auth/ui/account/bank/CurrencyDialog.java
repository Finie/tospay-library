package net.tospay.auth.ui.account.bank;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.databinding.DialogCurrencyBinding;
import net.tospay.auth.databinding.ListCurrencyBinding;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.account.bank.model.Currency;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.GatewayViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class CurrencyDialog extends BottomSheetDialogFragment {
    private CurrencyAdapter adapter;
    private OnCurrencySelectedListener mListener;
    private BankViewModel mViewModel;
    private DialogCurrencyBinding mBinding;
    public static final String TAG = CurrencyDialog.class.getSimpleName();

    public CurrencyDialog() {
    }

    public static CurrencyDialog newInstance() {
        Bundle args = new Bundle();
        CurrencyDialog fragment = new CurrencyDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);
        this.adapter = new CurrencyAdapter(new ArrayList<>(), mListener);
        AppExecutors mAppExecutors = new AppExecutors();
        GatewayService gatewayService = ServiceGenerator.createService(GatewayService.class, getContext());

        GatewayRepository mGatewayRepository = new GatewayRepository(mAppExecutors, gatewayService);
        GatewayViewModelFactory factory = new GatewayViewModelFactory(mGatewayRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(BankViewModel.class);

        SharedPrefManager mSharedPrefManager = SharedPrefManager.getInstance(getContext());
        setBearerToken(mSharedPrefManager.getAccessToken());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_currency, container, false);
        return mBinding.getRoot();
    }

    public void setBearerToken(String token) {
        String bearerToken = "Bearer " + token;
        mViewModel.setBearerToken(bearerToken);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setBankViewModel(mViewModel);
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.HORIZONTAL));
        mBinding.recyclerView.setAdapter(adapter);
        mViewModel.fetchCurrencies();
        mViewModel.getmCurrency().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        adapter.setmCurrency(resource.data);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (CurrencyDialog.OnCurrencySelectedListener) parent;
        } else {
            mListener = (CurrencyDialog.OnCurrencySelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCurrencySelectedListener {
        void OnCurrencySelected(Currency currency);
    }

    public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {


        private List<Currency> mCurrency;
        private CurrencyDialog.OnCurrencySelectedListener mListener;

        public CurrencyAdapter(List<Currency> mCurrency, CurrencyDialog.OnCurrencySelectedListener mListener) {
            this.mCurrency = mCurrency;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public CurrencyAdapter.CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CurrencyAdapter.CurrencyViewHolder(ListCurrencyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), mListener);
        }

        @Override
        public void onBindViewHolder(@NonNull CurrencyAdapter.CurrencyViewHolder holder, int position) {
            Currency currency = mCurrency.get(position);
            holder.bind(currency,mListener);
        }

        @Override
        public int getItemCount() {
            return mCurrency==null ? 0 : mCurrency.size();
        }

        public void setmCurrency(List<Currency> mCurrency) {
            this.mCurrency = mCurrency;
            notifyItemRangeChanged(0,mCurrency.size());
        }

        public class CurrencyViewHolder extends RecyclerView.ViewHolder {
            private  ListCurrencyBinding mBinding;
            public CurrencyViewHolder(@NonNull ListCurrencyBinding mBinding, CurrencyDialog.OnCurrencySelectedListener mListener) {
                super(mBinding.getRoot());
                this.mBinding = mBinding;
            }

            public void bind(Currency currency, CurrencyDialog.OnCurrencySelectedListener mListener) {
                mBinding.selectCurrency.setOnClickListener(v -> {
                    mListener.OnCurrencySelected(mBinding.getCurrency());
                    dismiss();
                });
                mBinding.setCurrency(currency);
                mBinding.executePendingBindings();
            }
        }

    }
}
