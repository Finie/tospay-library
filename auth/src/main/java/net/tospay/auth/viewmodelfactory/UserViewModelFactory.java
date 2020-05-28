package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.ui.auth.email.EmailViewModel;
import net.tospay.auth.ui.auth.login.LoginViewModel;
import net.tospay.auth.ui.auth.forgot.ForgotPasswordViewModel;
import net.tospay.auth.ui.auth.phone.PhoneViewModel;
import net.tospay.auth.ui.auth.register.RegisterViewModel;
import net.tospay.auth.ui.auth.reset.ResetPasswordViewModel;

public class UserViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final UserRepository userRepository;

    public UserViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            //noinspection unchecked
            return (T) new LoginViewModel(userRepository);

        } else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            //noinspection unchecked
            return (T) new RegisterViewModel(userRepository);

        } else if (modelClass.isAssignableFrom(EmailViewModel.class)) {
            //noinspection unchecked
            return (T) new EmailViewModel(userRepository);

        } else if (modelClass.isAssignableFrom(PhoneViewModel.class)) {
            //noinspection unchecked
            return (T) new PhoneViewModel(userRepository);

        } else if (modelClass.isAssignableFrom(ForgotPasswordViewModel.class)) {
            //noinspection unchecked
            return (T) new ForgotPasswordViewModel(userRepository);

        } else if (modelClass.isAssignableFrom(ResetPasswordViewModel.class)) {
            //noinspection unchecked
            return (T) new ResetPasswordViewModel(userRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}