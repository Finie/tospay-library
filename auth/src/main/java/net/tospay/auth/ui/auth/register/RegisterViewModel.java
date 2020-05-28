package net.tospay.auth.ui.auth.register;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.tospay.auth.R;
import net.tospay.auth.remote.request.RegisterRequest;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.ui.base.BaseViewModel;

public class RegisterViewModel extends BaseViewModel<RegisterNavigation>
        implements View.OnClickListener {

    private MutableLiveData<Country> country = new MutableLiveData<>();
    private LiveData<Resource<TospayUser>> responseLiveData;
    private UserRepository userRepository;

    public RegisterViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<Country> getCountry() {
        return country;
    }

    public LiveData<Resource<TospayUser>> getResponseLiveData() {
        return responseLiveData;
    }

    public void register(String firstName, String lastName, String email, String password, String phone, Country country) {
        RegisterRequest request = new RegisterRequest();
        request.setFirstname(firstName);
        request.setLastname(lastName);
        request.setEmail(email);
        request.setPassword(password);
        request.setPhone(phone);
        request.setCountry(country);
        responseLiveData = userRepository.register(request);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_sign_up) {
            getNavigator().onRegisterClick(view);

        } else if (id == R.id.btn_login) {
            getNavigator().onLoginClick(view);

        } else if (id == R.id.selectCountryTextView) {
            getNavigator().onSelectCountry(view);
        }
    }
}
