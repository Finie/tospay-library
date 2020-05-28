package net.tospay.auth.ui.account.account_fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccoutDeleteDialog extends BottomSheetDialogFragment {
    private static final String KEY_ACCOUNT = "account";
    private OnAccountActionListener mListener;
    private Account account;
    private TextView account_details;
    public AccoutDeleteDialog() {
        // Required empty public constructor
    }

    public static AccoutDeleteDialog newInstance(Account account) {
        AccoutDeleteDialog fragment = new AccoutDeleteDialog();
        Bundle args = new Bundle();
        args.putParcelable(KEY_ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BottomSheetDialogTheme);
        if (getArguments() != null) {
            account = getArguments().getParcelable(KEY_ACCOUNT);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accout_delete_dialog, container, false);
        account_details = view.findViewById(R.id.account_details);
        account_details.setText(account.getAlias()+" ***"+account.getTrunc());
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.delete_layout).setOnClickListener(view1 -> {
            mListener.onDeleteAccount(account);
            dismiss();
        });
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (OnAccountActionListener) parent;
        } else {
            mListener = (OnAccountActionListener) context;
        }
    }
    public interface OnAccountActionListener {
        void onEditAccount(Account account);

        void onDeleteAccount(Account account);
    }

}
