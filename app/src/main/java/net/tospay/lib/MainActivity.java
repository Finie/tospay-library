package net.tospay.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.tospay.auth.Tospay;
import net.tospay.auth.model.Account;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.auth.pinAuth.PinActivity;
import net.tospay.auth.ui.main.TospayActivity;
import net.tospay.auth.ui.scanner.Scanner;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signOut(View view) {
        Tospay.getInstance(this).signOut();
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
    }

    public void pay(View view) {
        String token = "ABACGB5M2XUSD4BJ";
        String url = "https://developer.android.com/guide/topics/resources/string-resource";

        Intent intent = Tospay.getInstance(this)
                .setPaymentToken(token)
                .setTermsAndConditionsUrl(url)
                .getPaymentIntent();

        /*Intent intent = Tospay.getInstance(this)
                .setPaymentToken(token)
                .setTermsAndConditionsUrl(url)
                .getAuthenticationIntent();*/

        startActivityForResult(intent, 1);
    }

    public  void  Scan(View view){
        startActivity(new Intent(MainActivity.this, Scanner.class));
    }

    public void account(View view) {
        String token = "Accounts";
        String url = "https://developer.android.com/guide/topics/resources/string-resource";

        Intent intent = Tospay.getInstance(this)
                .setPaymentToken(token)
                .setTermsAndConditionsUrl(url)
                .getPaymentIntent();

        /*Intent intent = Tospay.getInstance(this)
                .setPaymentToken(token)
                .setTermsAndConditionsUrl(url)
                .getAuthenticationIntent();*/

        startActivityForResult(intent, 1);
    }

    public void Pin(View view){
        SharedPrefManager sharedPreferences = SharedPrefManager.getInstance(this);
       boolean ispinset = sharedPreferences.read(Constants.KEY_PIN_SET,false);
        if(sharedPreferences.getActiveUser() != null){
            startActivity(new Intent(MainActivity.this, PinActivity.class));
        }
        else {
            startActivityForResult(new Intent(MainActivity.this, AuthActivity.class),
                    AuthActivity.REQUEST_CODE_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult: "+requestCode );
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if(data != null){
                    String result = data.getStringExtra("result");
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "onActivityResult: data was null" );
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    String result = data.getStringExtra("result");
                    if (result != null) {
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e(TAG, "onActivityResult: "+"Data is null" );
            }
        }
    }
}
