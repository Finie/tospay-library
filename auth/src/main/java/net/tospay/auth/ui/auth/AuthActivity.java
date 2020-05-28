package net.tospay.auth.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.R;
import net.tospay.auth.Tospay;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.SharedPrefManager;

public class AuthActivity extends AppCompatActivity implements PaymentListener {

    public static final int REQUEST_CODE_LOGIN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        SharedPrefManager sharedPrefManager = Tospay.getInstance(this).getSharedPrefManager();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_auth);

        TospayUser tospayUser = SharedPrefManager.getInstance(this).getActiveUser();
        if (tospayUser != null) {
            if (!tospayUser.isEmailVerified()) {
                navController.navigate(R.id.navigation_email_verification);

            } else if (!tospayUser.isPhoneVerified()) {
                navController.navigate(R.id.navigation_phone_verification);

            } else if (!sharedPrefManager.read(Constants.KEY_PIN_SET, false)) {
                navController.navigate(R.id.navigation_set_pin);
            }
        }
    }

    @Override
    public void onLoginSuccess(TospayUser user) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", user);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onLoginFailed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
