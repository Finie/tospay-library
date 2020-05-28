package net.tospay.auth.databinding;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.tospay.auth.R;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.utils.StringUtil;
import net.tospay.auth.view.ErrorLayout;
import net.tospay.auth.view.LoadingLayout;

import java.text.NumberFormat;

public final class BindingUtils {

    private BindingUtils() {
        // This class is not publicly instantiable
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String imageUrl) {
        Context context = imageView.getContext();

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_placeholder);

        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(imageUrl)
                .into(imageView);
    }

    @BindingAdapter("topupImageResource")
    public static void imageTopupResource(ImageView imageView, String sourceChannel) {
        if (TextUtils.equals(sourceChannel, "mobile")) {
            imageView.setImageResource(R.drawable.ic_mobile);

        } else if (TextUtils.equals(sourceChannel, "card")) {
            imageView.setImageResource(R.drawable.ic_card);

        } else if (TextUtils.equals(sourceChannel, "bank")) {
            imageView.setImageResource(R.drawable.ic_bank);

        } else {
            imageView.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @BindingAdapter("circular")
    public static void setCircularImage(ImageView imageView, String imageUrl) {
        Context context = imageView.getContext();

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile);

        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(imageUrl)
                .into(imageView);
    }

    @BindingAdapter("refreshing")
    public static void refreshing(SwipeRefreshLayout swipeRefreshLayout, boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    @BindingAdapter("error_message")
    public static void errorMessage(ErrorLayout errorLayout, String message) {
        errorLayout.setErrorMessage(message);
    }

    @BindingAdapter("profile_pic")
    public static void profilePic(ImageView imageView, String imageUrl) {
        if (imageUrl == null) {
            imageView.setVisibility(View.INVISIBLE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            setCircularImage(imageView, imageUrl);
        }
    }

    @BindingAdapter("account_type_icon")
    public static void accountTypeLogo(ImageView imageView, int type) {
        switch (type) {
            case AccountType.BANK:
                imageView.setImageResource(R.drawable.ic_bank);
                break;

            case AccountType.MOBILE:
                imageView.setImageResource(R.drawable.ic_mobile);
                break;

            case AccountType.CARD:
                imageView.setImageResource(R.drawable.ic_card);
                break;
        }
    }

    @BindingAdapter("account_type_background")
    public static void setAccountTypeBackground(View view, int type) {
        switch (type) {
            case AccountType.BANK:
                view.setBackgroundResource(R.drawable.ic_nbk_card);
                break;

            case AccountType.MOBILE:
                view.setBackgroundResource(R.drawable.ic_mpesa_card);
                break;

            case AccountType.CARD:
                view.setBackgroundResource(R.drawable.ic_visa_card);
                break;
        }
    }

    @BindingAdapter("html")
    public static void setHtml(TextView textView, String html) {
        if (html != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView.setText(Html.fromHtml(html));
            }
        }
    }

    @BindingAdapter("currency")
    public static void setCurrency(TextView textView, String amount) {
        textView.setText(StringUtil.formatAmount(amount));
    }
}
