package net.tospay.auth.ui.account;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.tospay.auth.R;
import net.tospay.auth.remote.response.ExecuteResponse;
import net.tospay.auth.view.LoadingLayout;

import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayCard3ds extends DialogFragment {

    private ExecuteResponse data;
    private String html;
    private ClosePay3Ds mListener;
    public static final String TAG = "PayCard3ds";

    public PayCard3ds(ExecuteResponse data,ClosePay3Ds mListener) {
        this.html = data.getHtml();
        this.data = data;
        this.mListener = mListener;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pay_card3ds, container, false);
    }


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
            mListener.onPayment3dsClose(data);
            dismiss();
        });
    }

    public interface ClosePay3Ds{
        void onPayment3dsClose(ExecuteResponse data);
    }
}
