package net.tospay.auth.ui.account.bank;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.databinding.DialogBankBinding;
import net.tospay.auth.databinding.ListItemBankViewBinding;
import net.tospay.auth.model.Bank;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.GatewayViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class BankDialog extends BottomSheetDialogFragment {

    public static final String TAG = BankDialog.class.getSimpleName();
    private static final String ARG_ITEM_ISO = "iso";

    private DialogBankBinding mBinding;
    private OnBankSelectedListener mListener;
    private BankAdapter adapter;
    private BankViewModel mViewModel;
    private String country_name;

    public BankDialog() {
        // Required empty public constructor
    }

    public static BankDialog newInstance(String country_name) {
        final BankDialog fragment = new BankDialog();
        final Bundle args = new Bundle();
        args.putString(ARG_ITEM_ISO, country_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);
        this.adapter = new BankAdapter(new ArrayList<>(), mListener);

        if (getArguments() != null) {
            country_name = getArguments().getString(ARG_ITEM_ISO);
        }

        AppExecutors mAppExecutors = new AppExecutors();
        GatewayService gatewayService = ServiceGenerator.createService(GatewayService.class, getContext());

        GatewayRepository mGatewayRepository = new GatewayRepository(mAppExecutors, gatewayService);
        GatewayViewModelFactory factory = new GatewayViewModelFactory(mGatewayRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(BankViewModel.class);

        SharedPrefManager mSharedPrefManager = SharedPrefManager.getInstance(getContext());
        setBearerToken(mSharedPrefManager.getAccessToken());
    }

    public void setBearerToken(String token) {
        String bearerToken = "Bearer " + token;
        mViewModel.setBearerToken(bearerToken);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_bank, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.setBankViewModel(mViewModel);
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.HORIZONTAL));
        mBinding.recyclerView.setAdapter(adapter);

        mViewModel.banks(country_name);
        mViewModel.getBanksResourceLiveData().observe(getViewLifecycleOwner(), resource -> {
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
                        adapter.setBanks(resource.data);
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
            mListener = (OnBankSelectedListener) parent;
        } else {
            mListener = (OnBankSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface OnBankSelectedListener {
        void onBankSelected(Bank bank);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemBankViewBinding mBinding;

        ViewHolder(ListItemBankViewBinding mBinding, OnBankSelectedListener mListener) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onBankSelected(mBinding.getBank());
                    dismiss();
                }
            });
        }

        void onBind(Bank bank) {
            if (bank.getName().contains("Cooperative Bank of Kenya")||(bank.getName().equalsIgnoreCase("Cooperative Bank of Kenya"))){
                mBinding.imageView.setImageResource(R.drawable.ic_co_op_bank_partner_logo_01);
            }
            else if (bank.getName().contains("National Bank")||bank.getName().equalsIgnoreCase("National Bank")){
                mBinding.imageView.setImageResource(R.drawable.ic_link_acc___nbk);
            }
            else{
                mBinding.imageView.setImageResource(R.drawable.ic_bank);
            }

            mBinding.setBank(bank);
            mBinding.executePendingBindings();
        }
    }

    private class BankAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Bank> banks;
        private OnBankSelectedListener mListener;

        BankAdapter(List<Bank> banks, OnBankSelectedListener mListener) {
            this.banks = banks;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ListItemBankViewBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false), mListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Bank bank = banks.get(position);
            holder.onBind(bank);
        }

        @Override
        public int getItemCount() {
            return banks == null ? 0 : banks.size();
        }

        void setBanks(List<Bank> banks) {
            this.banks = banks;
            notifyItemRangeChanged(0, banks.size());
        }
    }
}
