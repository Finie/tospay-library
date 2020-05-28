package net.tospay.auth.ui.account.bank;

import android.view.View;

public interface BankNavigator {

    default void onSelectCountryClick(View view) {

    }

    default void onSelectBankClick(View view) {

    }

    void onDone(View view);

    default void selectCurrency(){

    }

    default void  selectBranch(){

    }

    default void onVerify(){

    }
}
