package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.ui.main.MainViewModel;
import net.tospay.auth.utils.SharedPrefManager;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final UserRepository userRepository;
    private final SharedPrefManager sharedPrefManager;

    public MainViewModelFactory(UserRepository userRepository, SharedPrefManager sharedPrefManager) {
        this.userRepository = userRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            //noinspection unchecked
            return (T) new MainViewModel(userRepository,sharedPrefManager);

        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
