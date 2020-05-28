package net.tospay.auth.ui.account.card;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLinkCardBinding;
import net.tospay.auth.interfaces.IOnBackPressed;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.CardRepository;
import net.tospay.auth.remote.response.CardInitRes;
import net.tospay.auth.remote.service.CardService;
import net.tospay.auth.ui.account.account_fragments.liking.LinkAct;
import net.tospay.auth.ui.base.BaseFragment;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.LinkCardViewModelFactory;


/**
 * A simple {@link BaseFragment} subclass.
 */
public class LinkCardFragment extends BaseFragment<FragmentLinkCardBinding, LinkCardViewModel> implements IOnBackPressed {

    private LinkCardViewModel mViewModel;
    private FragmentLinkCardBinding mBinding;
    private String url;
    private static final String TAG = "LinkCardFragment";
    private String from;

    public LinkCardFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.linkCardViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_link_card;
    }

    @Override
    public LinkCardViewModel getViewModel() {
        CardService cardService = ServiceGenerator.createService(CardService.class, getContext());
        CardRepository cardRepository = new CardRepository(cardService, getAppExecutors());
        SharedPrefManager sharedPrefManager = getSharedPrefManager();
        LinkCardViewModelFactory factory = new LinkCardViewModelFactory(cardRepository, sharedPrefManager);
        mViewModel = ViewModelProviders.of(this, factory).get(LinkCardViewModel.class);
        return mViewModel;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setLinkCardViewModel(mViewModel);
        assert getArguments() != null;
        from = LinkCardFragmentArgs.fromBundle(getArguments()).getFrom();
        mBinding.btnBackImageView.setOnClickListener(view1 -> {
                    if (from.equalsIgnoreCase("account")) {
                        Intent intent = new Intent();
                        intent.putExtra("result", "Linking  canceled");
                        getActivity().setResult(LinkAct.LINK_ACCOUT_REQUEST, intent);
                        getActivity().finish();
                    } else {
                        Navigation.findNavController(view1).navigateUp();
                    }
                }
        );


        mViewModel.getCardData();
        mViewModel.getCardResponse().observe(this, resource -> {
            if (resource != null){
                switch (resource.status){
                    case SUCCESS:
                        mViewModel.setIsError(false);
                        initLoad(resource.data);
                        break;

                    case LOADING:
                        mViewModel.setIsError(false);
                        mViewModel.setIsLoading(true);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case RE_AUTHENTICATE:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage("Re auth required");
                        break;
                }
            }
        });

        mBinding.swipeRefreshLayout.setOnRefreshListener(this::loadUrl);

    }

    private void initLoad(String token){
        url = token;
        final WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setAllowFileAccess(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT < 18) {
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mViewModel.setIsLoading(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mViewModel.setIsLoading(false);
            }
        });
        loadUrl();
    }

    private void loadUrl() {
        mBinding.webView.loadUrl(url);
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }
}
