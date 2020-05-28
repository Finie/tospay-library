package net.tospay.auth.ui.account;

import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Wallet;

public interface OnAccountItemClickListener {

    default void onTopupClick(Wallet wallet) {

    }

    default void onAccountSelectedListener(AccountType accountType) {

    }

    default void onVerifyClick(AccountType accountType) {

    }
}
