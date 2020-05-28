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
import net.tospay.auth.databinding.FragmentDialogBranchBinding;
import net.tospay.auth.databinding.ListItemBranchBinding;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.account.bank.model.Branch;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.GatewayViewModelFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BranchDialog extends BottomSheetDialogFragment {
    public static final String TAG = BranchDialog.class.getSimpleName();
    private static final String ARG_ITEM_ISO = "iso";

    private FragmentDialogBranchBinding mBinding;
    private BranchDialog.OnBranchSelectedListener mListener;
    private BranchAdapter adapter;
    private BankViewModel mViewModel;
    private String bank_id;


    public BranchDialog() {
        // Required empty public constructor
    }

    public static BranchDialog newInstance(String id) {
        final BranchDialog fragment = new BranchDialog();
        final Bundle args = new Bundle();
        args.putString(ARG_ITEM_ISO, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);

        this.adapter = new BranchAdapter(new ArrayList<>(), mListener);
        if (getArguments() != null) {
            bank_id = getArguments().getString(ARG_ITEM_ISO);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setBankViewModel(mViewModel);
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.HORIZONTAL));
        mBinding.recyclerView.setAdapter(adapter);
        mViewModel.branch(bank_id);
        mViewModel.getMbranchLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null){
                switch (resource.status){

                    case SUCCESS:
                        mViewModel.setIsError(false);
                        if(resource.data != null){
                            adapter.setBranchList(resource.data);
                            adapter.notifyDataSetChanged();
                        }
                        mViewModel.setIsLoading(false);
                        break;
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;
                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_dialog_branch,container,false);
        return mBinding.getRoot();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (OnBranchSelectedListener) parent;
        } else {
            mListener = (OnBranchSelectedListener) context;
        }

    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface OnBranchSelectedListener {
        void onBrabchSelected(Branch branch);
    }

    private class BranchAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Branch> branchList;
        private OnBranchSelectedListener mListener;

        public BranchAdapter(List<Branch> branchList, OnBranchSelectedListener mListener) {
            this.branchList = branchList;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ListItemBranchBinding.
                    inflate(LayoutInflater.from(parent.getContext()), parent, false), mListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Branch branch = branchList.get(position);
            holder.onBind(branch);
        }

        @Override
        public int getItemCount() {
            return branchList == null ? 0 : branchList.size();
        }

        public void setBranchList(List<Branch> branchList) {
     Collections.sort(branchList, (branch1, branch2) -> branch1.getBranchName().compareTo(branch2.getBranchName()));
            this.branchList = branchList;
            notifyItemRangeChanged(0,branchList.size());
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemBranchBinding mBinding;

        ViewHolder(ListItemBranchBinding mBinding,  OnBranchSelectedListener mListener) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onBrabchSelected(mBinding.getBranch());
                    dismiss();
                }
            });
        }

        void onBind(Branch branch) {
            mBinding.setBranch(branch);
            mBinding.executePendingBindings();
        }
    }

}
