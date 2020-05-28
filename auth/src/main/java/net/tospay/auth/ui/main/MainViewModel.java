package net.tospay.auth.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.request.LoginRequest;
import net.tospay.auth.ui.base.BaseViewModel;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.device_model.LocationRes;
import net.tospay.auth.utils.SharedPrefManager;

public class MainViewModel extends BaseViewModel {

    private final UserRepository userRepository;
    private final SharedPrefManager sharedPrefManager;
    private LiveData<Resource<TospayUser>> responseLiveData;
    private LiveData<LocationRes> mLocationRequest;

    public MainViewModel(UserRepository userRepository, SharedPrefManager sharedPrefManager) {
        this.userRepository = userRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public void login() {
        String email = sharedPrefManager.read(SharedPrefManager.KEY_EMAIL, null);
        String password = sharedPrefManager.read(SharedPrefManager.KEY_PASSWORD, null);

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        responseLiveData = userRepository.login(request);
    }

    public LiveData<Resource<TospayUser>> getResponseLiveData() {
        return responseLiveData;
    }

    public void getGeolocations(LocationReq request){
      mLocationRequest =  userRepository.getGeoLocation(request);
    }

    public LiveData<LocationRes>  getmLocationRequest() {
        return mLocationRequest;
    }


}
