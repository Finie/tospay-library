package net.tospay.auth.ui.dialog;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.response.ExecuteResponse;
import net.tospay.auth.view.LoadingLayout;

public class DialogTransactionLock extends DialogFragment {

    private CloseTransactionLock mListener;
    private static final String PIN_URL = "url";
    private String url,flag;
    private TospayUser user;
    public static final String TAG = "DialogTransactionLock";
    private ExecuteResponse data;

    public DialogTransactionLock(CloseTransactionLock mListener, String url, TospayUser user, String flag, ExecuteResponse data) {
        this.mListener = mListener;
        this.url = url;
        this.user = user;
        this.flag = flag;
        this.data = data;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        } else {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_pin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoadingLayout layout = view.findViewById(R.id.loadingLayout);
        WebView webView = view.findViewById(R.id.pin_web_view);
        final WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                layout.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(url);
        ImageView btn_delete = view.findViewById(R.id.cancel_button);
        btn_delete.setOnClickListener(v -> {
            mListener.onTransactionLockedClosed(true,user,flag,data);
            dismiss();
        });
    }

    public interface CloseTransactionLock{
        void onTransactionLockedClosed(boolean transactionClosed, TospayUser user,String flag,ExecuteResponse data);
    }
}
