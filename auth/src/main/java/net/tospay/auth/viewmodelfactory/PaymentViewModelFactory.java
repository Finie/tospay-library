package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.ui.summary.SummaryViewModel;

public class PaymentViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PaymentRepository paymentRepository;

    public PaymentViewModelFactory(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SummaryViewModel.class)) {
            //noinspection unchecked
            return (T) new SummaryViewModel(paymentRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}