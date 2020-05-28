package net.tospay.auth.ui.scanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.ActivityScannerBinding;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.base.BaseActivity;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.device_model.LocationRes;
import net.tospay.auth.ui.main.MainViewModel;
import net.tospay.auth.utils.DeviceDetails;
import net.tospay.auth.viewmodelfactory.MainViewModelFactory;

public class Scanner extends BaseActivity<ActivityScannerBinding, MainViewModel> {

    private ActivityScannerBinding mBinding;
    private MainViewModel mViewModel;
    private static final String TAG = "Scanner";

    @Override
    public int getBindingVariable() {
        return BR.mViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scanner;
    }

    @Override
    public MainViewModel getViewModel() {
        UserRepository repository = new UserRepository(new AppExecutors(),
                ServiceGenerator.createService(UserService.class, this));

        MainViewModelFactory factory = new MainViewModelFactory(repository, getSharedPrefManager());
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        return mViewModel;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.location.setOnClickListener(v -> {
            handleResult();
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.P)
    public void handleResult() {

        LocationReq req = DeviceDetails.getLocationRequest(this);
        mViewModel.getGeolocations(req);
        mBinding.loadingLayout.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            handler.post(() -> {
                mViewModel.getmLocationRequest().observe(this, response -> {
                    if (response != null){
                        if (response.getLocation() != null){
                            mBinding.loadingLayout.setVisibility(View.GONE);
                            Log.e(TAG, "longitude : "+response.getLocation().getLng() );
                            Log.e(TAG, "longitude : "+response.getLocation().getLat() );
                            Log.e(TAG, "longitude : "+response.getAccuracy());
                            mBinding.content.setText(response.toString());
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        }).start();


    }



}
