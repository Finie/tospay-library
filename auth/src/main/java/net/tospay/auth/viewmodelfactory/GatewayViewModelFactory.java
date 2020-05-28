package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.account.bank.BankViewModel;
import net.tospay.auth.ui.dialog.country.CountryViewModel;
import net.tospay.auth.ui.dialog.network.NetworkViewModel;

public class GatewayViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private GatewayRepository gatewayRepository;

    public GatewayViewModelFactory(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CountryViewModel.class)) {
            //noinspection unchecked
            return (T) new CountryViewModel(gatewayRepository);

        } else if (modelClass.isAssignableFrom(NetworkViewModel.class)) {
            //noinspection unchecked
            return (T) new NetworkViewModel(gatewayRepository);

        } else if (modelClass.isAssignableFrom(BankViewModel.class)) {
            //noinspection unchecked
            return (T) new BankViewModel(gatewayRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
