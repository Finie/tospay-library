package net.tospay.auth.ui.account.mobile;

import android.view.View;


public interface MobileMoneyNavigator {

    default void onSelectCountryClick(View view) {

    }

    default void onNetworkCountryClick(View view) {

    }

    void onConfirmClick(View view);

    default void onVerifyClick(View view) {

    }

    default void onResendClick(View view) {

    }
}
