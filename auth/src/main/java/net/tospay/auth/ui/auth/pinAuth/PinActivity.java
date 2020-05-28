package net.tospay.auth.ui.auth.pinAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tospay.auth.R;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.model.TransactionPin;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.response.ExecuteResponse;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.auth.login.LoginViewModel;
import net.tospay.auth.ui.dialog.DialogTransactionLock;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.Encryption;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.utils.TextImages;
import net.tospay.auth.view.LoadingLayout;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PinActivity extends AppCompatActivity implements DialogTransactionLock.CloseTransactionLock {
    private ImageView one, two, three, four, five, six, seven, eight, nine, ten;
    private View pin_one, pin_two, pin_three, pin_four;
    private int clickCount;
    private ImageView delete;
    private TextView cant_remember, notification;
    private String pin = "";
    private boolean isNotFingerPrint = false;
    private String firstPin, secondPin;
    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt myBiometricPrompt;
    private static final String TAG = "PinActivity";
    private AnimationDrawable animationDrawable;
    public static final int REQUEST_PIN = 200;
    private SharedPrefManager sharedPrefManager;
    private LoginViewModel mViewModel;
    private LoadingLayout loadingLayout;
    private RelativeLayout base_layout;
    private boolean startSetUp = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin2);
        base_layout = (RelativeLayout) findViewById(R.id.base_layout);
        animationDrawable = (AnimationDrawable) base_layout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(3000);

        UserRepository repository = new UserRepository(new AppExecutors(),
                ServiceGenerator.createService(UserService.class, this));


        UserViewModelFactory factory = new UserViewModelFactory(repository);


        mViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);
        sharedPrefManager = SharedPrefManager.getInstance(this);



        loadingLayout = findViewById(R.id.loadingLayout);
        clickCount = 0;
        firstPin = null;
        secondPin = null;
        notification = findViewById(R.id.notification);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);
        ten = findViewById(R.id.ten);
        cant_remember = findViewById(R.id.cant_remember);
        delete = findViewById(R.id.delete);
        one.setImageDrawable(TextImages.textToImage("1", this));
        two.setImageDrawable(TextImages.textToImage("2", this));
        three.setImageDrawable(TextImages.textToImage("3", this));
        four.setImageDrawable(TextImages.textToImage("4", this));
        five.setImageDrawable(TextImages.textToImage("5", this));
        six.setImageDrawable(TextImages.textToImage("6", this));
        seven.setImageDrawable(TextImages.textToImage("7", this));
        eight.setImageDrawable(TextImages.textToImage("8", this));
        nine.setImageDrawable(TextImages.textToImage("9", this));
        ten.setImageDrawable(TextImages.textToImage("0", this));
        pin_one = findViewById(R.id.pin_one);
        pin_two = findViewById(R.id.pin_two);
        pin_three = findViewById(R.id.pin_three);
        pin_four = findViewById(R.id.pin_four);
        if (isFingerPrintEnabled()) {
            if (usesFingerprint() && !isPinSet()) {
                startSetUp = true;
                isNotFingerPrint = false;
                notification.setText("Use fingerprint or setup pin");
                validateFingerPrint();
                myBiometricPrompt.authenticate(promptInfo);
            } else if (isPinSet() && !usesFingerprint()) {
                //user uses pin and pin is set
                notification.setText("Enter your pin");
            } else if (isPinSet() && usesFingerprint()) {
                notification.setText("Use your pin or Fingerprint");
                isNotFingerPrint = false;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp));
            } else {
                //security not setup
                isNotFingerPrint = false;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp));
                notification.setText("Set up your pin / fingerprint");
            }
        } else {
            //does not have fingerprint
            if (isPinSet()) {
                //pin is set
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
                notification.setText("Enter your pin");
            } else {
                //pin is not set
                isNotFingerPrint = true;
                notification.setText("Set up pin");
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
            }
        }
        one.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "1";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        two.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "2";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        three.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "3";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        four.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pinN");
                }
                pin += "4";
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        five.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "5";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        six.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "6";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        seven.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "7";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        eight.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "8";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        nine.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "9";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        ten.setOnClickListener(v -> {
            if (clickCount <= 4 && clickCount >= 0) {
                clickCount += 1;
                pin += "0";
                if (startSetUp && firstPin == null) {
                    notification.setText("Setup pin");
                }
                if (pin.length() > 4) {
                    pin = pin.substring(0, 4);
                    clickCount = 4;
                }
                isNotFingerPrint = true;
                delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));

                if (clickCount == 1) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 2) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                } else if (clickCount == 3) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.GONE);

                } else if (clickCount == 4) {
                    pin_one.setVisibility(View.VISIBLE);
                    pin_two.setVisibility(View.VISIBLE);
                    pin_three.setVisibility(View.VISIBLE);
                    pin_four.setVisibility(View.VISIBLE);
                    pinComplete();
                }
            } else {
                clickCount = 4;
            }
        });

        delete.setOnClickListener(v -> {
            if (isNotFingerPrint) {
                clickCount -= 1;
                if (clickCount > 0) {
                    if (clickCount == 1) {
                        pin = removeLastCharacter(pin);
                        pin_one.setVisibility(View.VISIBLE);
                        pin_two.setVisibility(View.GONE);
                        pin_three.setVisibility(View.GONE);
                        pin_four.setVisibility(View.GONE);
                    } else if (clickCount == 2) {
                        pin = removeLastCharacter(pin);
                        pin_one.setVisibility(View.VISIBLE);
                        pin_two.setVisibility(View.VISIBLE);
                        pin_three.setVisibility(View.GONE);
                        pin_four.setVisibility(View.GONE);
                    } else if (clickCount == 3) {
                        pin = removeLastCharacter(pin);
                        pin_one.setVisibility(View.VISIBLE);
                        pin_two.setVisibility(View.VISIBLE);
                        pin_three.setVisibility(View.VISIBLE);
                        pin_four.setVisibility(View.GONE);

                    }

                } else {
                    if (clickCount == 0) {
                        pin = removeLastCharacter(pin);
                        pin_one.setVisibility(View.GONE);
                        pin_two.setVisibility(View.GONE);
                        pin_three.setVisibility(View.GONE);
                        pin_four.setVisibility(View.GONE);
                    }

                    if (isFingerPrintEnabled()) {
                        isNotFingerPrint = false;
                        delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp));
                    }
                    clickCount = 0;
                }
            } else {
                if (usesFingerprint()) {
                    validateFingerPrint();
                    myBiometricPrompt.authenticate(promptInfo);
                } else {
                    setUpFingerPrint();
                    myBiometricPrompt.authenticate(promptInfo);
                }
            }
        });

        cant_remember.setOnClickListener(view -> {
            SharedPreferences.Editor editor = getSharedPreferences(view.getContext().getString(R.string.app_name), Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            sharedPrefManager.save(Constants.KEY_PIN_SET, false);
            sharedPrefManager.save(Constants.USES_FINGERPRINT, false);
            startActivityForResult(new Intent(PinActivity.this, AuthActivity.class),
                    AuthActivity.REQUEST_CODE_LOGIN);
            finish();
        });

    }

    private void setUpFingerPrint() {
        Executor newExecutor = Executors.newSingleThreadExecutor();
        FragmentActivity activity = this;
        myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Log.e(TAG, "onAuthenticationError: ");
                } else {

                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                activity.runOnUiThread(() -> {
                    saveFingerprint();
                    String onlinePin = sharedPrefManager.read(SharedPrefManager.ONLINE_PIN_SET, null);
                    if (onlinePin.equals("false")){
                        showCustomDialog(getApplication(),getWindow().getDecorView().getRootView());
                    }
                    finishWithSuccess();
                });
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Set up fingerprint")
                .setSubtitle("Use fingerprint instead of pin")
                .setDescription("")
                .setNegativeButtonText("Abort")
                .build();
    }

    private void loginWithFingerPrint() {
        Executor newExecutor = Executors.newSingleThreadExecutor();
        FragmentActivity activity = this;
        myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {

                } else {

                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                activity.runOnUiThread(() -> reAuthenticateUser());
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                Log.d(TAG, "Fingerprint not recognised");
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Login")
                .setSubtitle("Use your fingerprint to log in")
                .setDescription("")
                .setNegativeButtonText("cancel")
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    private void validatePin(String pin) {
        if (matchPin(pin)) {
            reAuthenticateUser();
        } else {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_pf);
            findViewById(R.id.display).startAnimation(shake);
            //trigger vibration
            triggerVibration();
        }
    }

    private void validateFingerPrint() {
        loginWithFingerPrint();
    }

    public static String removeLastCharacter(String str) {
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 1);
        }
        return result;
    }

    private boolean isFingerPrintEnabled() {
        FingerprintManager fingerprintManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(getApplicationContext().FINGERPRINT_SERVICE);
        }
        if (fingerprintManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints());
            }
        }
        return false;
    }

    private void savePin(String pin) {
        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
        SharedPreferences.Editor editor = getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE).edit();
        editor.putString(Constants.KEY_PIN, encryption.encryptOrNull(pin));
        editor.putBoolean(Constants.KEY_PIN_SET, true);
        editor.apply();

        String onlinePin = sharedPrefManager.read(SharedPrefManager.ONLINE_PIN_SET, null);
        if (onlinePin.equals("false")){
            showCustomDialog(this,getWindow().getDecorView().getRootView());
        }
    }

    private void saveFingerprint() {
        SharedPreferences.Editor editor = getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constants.USES_FINGERPRINT, true);
        editor.apply();
    }

    private boolean usesFingerprint() {
        SharedPrefManager sharedPreferences = SharedPrefManager.getInstance(this);
        return sharedPreferences.read(Constants.USES_FINGERPRINT, false);
    }

    private boolean matchPin(String pin) {
        SharedPrefManager sharedPreferences = SharedPrefManager.getInstance(this);
        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
        String Storedpin = encryption.decryptOrNull(sharedPreferences.read(Constants.KEY_PIN, ""));
        return pin.equalsIgnoreCase(Storedpin);
    }

    private boolean isPinSet() {
        SharedPrefManager sharedPreferences = SharedPrefManager.getInstance(this);
        return sharedPreferences.read(Constants.KEY_PIN_SET, false);
    }

    private void reAuthenticateUser() {
        String email = sharedPrefManager.read(SharedPrefManager.KEY_EMAIL, null);
        String password = sharedPrefManager.read(SharedPrefManager.KEY_PASSWORD, null);
        mViewModel.login(email, password);
        mViewModel.getResponseLiveData().observe(this, resource -> {
            if (resource != null) {
                Log.e(TAG, "reAuthenticateUser: " + resource);
                switch (resource.status) {
                    case LOADING:
                        loadingLayout.setVisibility(View.VISIBLE);
                        break;

                    case SUCCESS:
                        loadingLayout.setVisibility(View.GONE);
                        sharedPrefManager.setActiveUser(resource.data);
                        finishWithSuccess();
                        break;

                    case ERROR:
                        loadingLayout.setVisibility(View.GONE);
                        AlertDialog alertDialog = new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Failed")
                                .setMessage(resource.message)
                                .setPositiveButton("Ok", (dialogInterface, i) -> {
                                    finishWithError(resource.message);
                                    finish();
                                }).show();
                        break;
                }
            }
        });
    }

    public void finishWithSuccess() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void finishWithError(String message) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", message);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void triggerVibration() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert v != null;
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            assert v != null;
            v.vibrate(500);
        }
    }

    private void pinComplete() {
        if (pin.length() > 3) {
            pin = pin.substring(0, 4);
            if (isPinSet()) {
                validatePin(pin);
            } else {
                //pin is not set
                if (firstPin == null) {
                    firstPin = pin;
                    pin_one.setVisibility(View.GONE);
                    pin_two.setVisibility(View.GONE);
                    pin_three.setVisibility(View.GONE);
                    pin_four.setVisibility(View.GONE);
                    notification.setText("Confirm pin");
                    pin = "";
                    clickCount = 0;
                } else {
                    secondPin = pin;
                }
                if (secondPin != null) {
                    if (firstPin.equals(secondPin)) {
                        savePin(firstPin);
                        finishWithSuccess();
                    } else {
                        firstPin = null;
                        secondPin = null;
                        pin = "";
                        clickCount = 0;
                        pin_one.setVisibility(View.GONE);
                        pin_two.setVisibility(View.GONE);
                        pin_three.setVisibility(View.GONE);
                        pin_four.setVisibility(View.GONE);
                        notification.setText("Setup pin");
                        isNotFingerPrint = false;
                        delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp));
                        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_pf);
                        findViewById(R.id.display).startAnimation(shake);
                        triggerVibration();
                    }
                }
            }
        } else {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_pf);
            findViewById(R.id.display).startAnimation(shake);
            triggerVibration();
        }

    }



    private void showCustomDialog( Context context, View view) {
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_transaction_notification, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        Button btn_delete = dialogView.findViewById(R.id.set_up_pin);
        btn_delete.setOnClickListener(v -> {
            getTransactionPinUrl(context,view);
            alertDialog.dismiss();
        });
    }

    private void getTransactionPinUrl(Context context, View view){
        mViewModel.getTransactionPinUrl("Bearer "+sharedPrefManager.getAccessToken());
        mViewModel.getPinsetupUrl().observe(this, resource -> {
            if (resource != null){
                switch (resource.status){
                    case ERROR:
                        loadingLayout.setVisibility(View.GONE);
                        break;
                    case LOADING:
                        loadingLayout.setVisibility(View.VISIBLE);
                        break;

                    case SUCCESS:
                        loadingLayout.setVisibility(View.GONE);
                        showPinSetUp(context,view,resource.data.getPinUrl());
                        break;

                    case RE_AUTHENTICATE:

                        break;
                }
            }
        });
    }



    private void showPinSetUp( Context context, View view,String url) {
        DialogTransactionLock dialogTransactionLock = new DialogTransactionLock(this,url,null,null,null);
        dialogTransactionLock.show(getSupportFragmentManager(), DialogTransactionLock.TAG);
    }


    @Override
    public void onTransactionLockedClosed(boolean transactionClosed, TospayUser user, String flag, ExecuteResponse data) {
        finishWithSuccess();
    }

}
