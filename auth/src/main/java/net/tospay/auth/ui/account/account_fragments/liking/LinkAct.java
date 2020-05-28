package net.tospay.auth.ui.account.account_fragments.liking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;

import net.tospay.auth.R;
import net.tospay.auth.interfaces.IOnBackPressed;
import net.tospay.auth.ui.account.AccountSelectionFragmentDirections;

public class LinkAct extends AppCompatActivity {
    public static int LINK_ACCOUT_REQUEST =900;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            Intent intent = new Intent();
            intent.putExtra("result","Linking  canceled");
            setResult(LinkAct.LINK_ACCOUT_REQUEST,intent);
            finish();
        }
    }
}
