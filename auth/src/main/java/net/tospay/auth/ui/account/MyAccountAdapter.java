package net.tospay.auth.ui.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.tospay.auth.databinding.ListItemWalletViewBinding;
import net.tospay.auth.databinding.ListMyAccountBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.ui.base.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyAccountAdapter extends BaseAdapter<RecyclerView.ViewHolder, AccountType>
        implements Filterable {
    private List<AccountType> mAccountTypes;
    private List<AccountType> mAccountTypeListFiltered;
    private  OnAccountItemClickListener listener;

    public MyAccountAdapter(List<AccountType> mAccountTypes, List<AccountType> mAccountTypeListFiltered, OnAccountItemClickListener listener) {
        this.mAccountTypes = mAccountTypes;
        this.mAccountTypeListFiltered = mAccountTypeListFiltered;
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                int type = Integer.valueOf(charSequence.toString());

                if (type != AccountType.ALL) {
                    List<AccountType> filteredList = new ArrayList<>();
                    for (AccountType row : mAccountTypes) {
                        if (row.getType() == type) {
                            filteredList.add(row);
                        }
                    }

                    mAccountTypeListFiltered = filteredList;
                } else {
                    mAccountTypeListFiltered = mAccountTypes;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mAccountTypeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mAccountTypeListFiltered = (List<AccountType>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void setData(List<AccountType> accountTypes) {
        this.mAccountTypes = accountTypes;
        this.mAccountTypeListFiltered = accountTypes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mAccountTypeListFiltered.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case AccountType.BANK:
            case AccountType.CARD:
            case AccountType.MOBILE:


                ListMyAccountBinding itemAccountViewBinding =
                        ListMyAccountBinding.inflate(inflater, parent, false);
                return new MyAccountAdapter.AccountViewHolder(itemAccountViewBinding, listener);

            default:
                ListItemWalletViewBinding itemWalletViewBinding =
                        ListItemWalletViewBinding.inflate(inflater, parent, false);
                return new MyAccountAdapter.WalletViewHolder(itemWalletViewBinding, listener);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AccountType accountType = mAccountTypeListFiltered.get(position);

        switch (getItemViewType(position)) {

            case AccountType.WALLET:
                ((AccountAdapter.WalletViewHolder) holder).onBind((Wallet) accountType);
                break;

            case AccountType.BANK:
            case AccountType.CARD:
            case AccountType.MOBILE:
                ((MyAccountAdapter.AccountViewHolder) holder).onBind((Account) accountType);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mAccountTypeListFiltered != null ? mAccountTypeListFiltered.size() : 0;
    }

    private class AccountViewHolder extends RecyclerView.ViewHolder {

        private ListMyAccountBinding mBinding;

        private AccountViewHolder(ListMyAccountBinding mBinding, OnAccountItemClickListener listener) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        public ListMyAccountBinding getBinding() {
            return mBinding;
        }

        void onBind(Account account) {
            mBinding.setAccount(account);
            mBinding.executePendingBindings();
        }
    }

    public class WalletViewHolder extends RecyclerView.ViewHolder {

        private ListItemWalletViewBinding mBinding;

        private WalletViewHolder(ListItemWalletViewBinding binding, OnAccountItemClickListener mListener) {
            super(binding.getRoot());
            this.mBinding = binding;

            mBinding.btnTopup.setOnClickListener(view -> mListener.onTopupClick(mBinding.getWallet()));
        }

        public ListItemWalletViewBinding getBinding() {
            return mBinding;
        }

        void onBind(Wallet wallet) {
            mBinding.setWallet(wallet);
            mBinding.executePendingBindings();
        }
    }
}
