package net.tospay.auth.interfaces;

import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.exception.TospayException;
import net.tospay.auth.remote.response.TransferResponse;

public interface PaymentListener {

    default void onPaymentSuccess(TransferResponse transferResponse,
                                  String title, String body) {

    }

    default void onPaymentFailed(TospayException exception) {

    }

    default void onLoginSuccess(TospayUser user) {

    }

    default void onLoginFailed() {

    }
}
