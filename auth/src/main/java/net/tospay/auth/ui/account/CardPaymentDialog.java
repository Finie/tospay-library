package net.tospay.auth.ui.account;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.tospay.auth.R;
import net.tospay.auth.event.NotificationEvent;
import net.tospay.auth.view.LoadingLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CardPaymentDialog extends DialogFragment {

    public static final String TAG = "CardPaymentDialog";
    private static final String KEY_HTML = "html";
    private String html;
    private Close3dsClickListener close3dsClickListener;

    public CardPaymentDialog() {
    }

    public static CardPaymentDialog newInstance(String html) {
        CardPaymentDialog fragment = new CardPaymentDialog();
        Bundle args = new Bundle();
        args.putString(KEY_HTML, html);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        } else {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            html = getArguments().getString(KEY_HTML);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_card_payment, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoadingLayout loadingLayout = view.findViewById(R.id.loader);
        loadingLayout.setVisibility(View.VISIBLE);
        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                loadingLayout.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.btn_close).setOnClickListener(view1 ->
        {

            dismiss();
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(NotificationEvent notification) {
        if (notification != null) {
            dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public interface Close3dsClickListener{
        void on3dsClose(String cardClose);
    }
}
