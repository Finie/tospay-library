package net.tospay.auth.ui.dialog.network;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Network;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.List;

public class NetworkViewModel extends BaseViewModel {

    private GatewayRepository gatewayRepository;
    private LiveData<Resource<List<Network>>> resourceLiveData;

    public NetworkViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public LiveData<Resource<List<Network>>> getResourceLiveData() {
        return resourceLiveData;
    }

    public void networks(String iso) {
        setIsLoading(true);
        resourceLiveData = gatewayRepository.networks((String) getBearerToken().get(), iso);
    }
}
