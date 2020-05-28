package net.tospay.auth.ui.account.account_fragments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.R;
import net.tospay.auth.databinding.ListAccountsBinding;
import net.tospay.auth.model.Account;

import java.util.List;


public class AcountsAdapter  extends RecyclerView.Adapter<AcountsAdapter.AcountsViewHolder> {
    List<Account> mAccounts;
    private static final String TAG = "AcountsAdapter";
    private  AccountselectListener mListener;
    public AcountsAdapter(List<Account> mAccounts,AccountselectListener mListener) {
        this.mAccounts = mAccounts;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public AcountsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListAccountsBinding mBinding = ListAccountsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new AcountsViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AcountsViewHolder holder, int position) {
        Account account = mAccounts.get(position);
        holder.onBind(account,mListener);
    }

    public void setmAccounts(List<Account> mAccounts) {
        if (mAccounts.size() == 0){
            this.mAccounts.clear();
            notifyDataSetChanged();
        }
        this.mAccounts = mAccounts;
        notifyItemRangeChanged(0,mAccounts.size());
    }

    public List<Account> getmAccounts() {
        return mAccounts;
    }

    @Override
    public int getItemCount() {
        return mAccounts==null ? 0: mAccounts.size();
    }

    public class AcountsViewHolder extends RecyclerView.ViewHolder {
        ListAccountsBinding mBinding;
        public AcountsViewHolder(@NonNull ListAccountsBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }


        public void onBind(Account account, AccountselectListener mListener) {
            mBinding.itemHolder.setOnClickListener(v -> {
                mListener.OnAccountSelected(account);
            });
            Log.e(TAG, "onBind: "+account.getNetwork() );
            if (account.getNetwork().equalsIgnoreCase("Visa")){
                mBinding.logo.setImageResource(R.drawable.ic_link_acc___visa);
            }
            else if (account.getNetwork().equalsIgnoreCase("Safaricom")){
                mBinding.logo.setImageResource(R.drawable.ic_link_acc___mpesa);
            }
            else {
                mBinding.logo.setImageResource(R.drawable.ic_link_acc___nbk);
            }
            mBinding.setAccount(account);
            mBinding.executePendingBindings();
        }
    }

    public interface AccountselectListener{
        void OnAccountSelected(Account account);
    }
}
