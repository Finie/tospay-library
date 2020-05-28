package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.ui.account.AccountViewModel;

public class AccountViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AccountRepository accountRepository;
    private PaymentRepository paymentRepository;
    private UserRepository userRepository;

    public AccountViewModelFactory(AccountRepository accountRepository,
                                   PaymentRepository paymentRepository,UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AccountViewModel.class)) {
            //noinspection unchecked
            return (T) new AccountViewModel(accountRepository, paymentRepository,userRepository);

        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
