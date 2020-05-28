package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.MobileRepository;
import net.tospay.auth.ui.account.mobile.MobileMoneyViewModel;

public class MobileViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MobileRepository repository;

    public MobileViewModelFactory(MobileRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MobileMoneyViewModel.class)) {
            //noinspection unchecked
            return (T) new MobileMoneyViewModel(repository);
        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
