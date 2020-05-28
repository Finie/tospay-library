package net.tospay.auth.ui.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import net.tospay.auth.R;
import net.tospay.auth.utils.SharedPrefManager;

public abstract class BaseActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {

    private VM mViewModel;
    private DB mDataBinding;
    private SharedPrefManager mSharedPrefManager;

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    @LayoutRes
    public abstract int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract VM getViewModel();

    public DB getViewDataBinding() {
        return mDataBinding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        mSharedPrefManager = SharedPrefManager.getInstance(this);
        performDataBinding();
        setBearerToken(mSharedPrefManager.getAccessToken());
    }

    /**
     * Performs data binding
     */
    private void performDataBinding() {
        mDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mViewModel = mViewModel == null ? getViewModel() : mViewModel;
        mDataBinding.setVariable(getBindingVariable(), mViewModel);
        mDataBinding.setLifecycleOwner(this);
        mDataBinding.executePendingBindings();
    }

    @Override
    protected void onDestroy() {
        mDataBinding.unbind();
        super.onDestroy();
    }

    public void setBearerToken(String token) {
        String bearerToken = "Bearer " + token;
        mViewModel.setBearerToken(bearerToken);
    }

    /**
     * Uses the new bearer token
     */
    public void reloadBearerToken() {
        setBearerToken(getAccessToken());
    }

    /**
     * Return Access token
     *
     * @return
     */
    public String getAccessToken() {
        return mSharedPrefManager.getAccessToken();
    }

    public SharedPrefManager getSharedPrefManager() {
        return mSharedPrefManager;
    }

    /**
     * Hides KeyBoard
     */
    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }
    }
}
