package net.tospay.auth.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tospay.auth.R;

public class ErrorLayout extends LinearLayout {

    private TextView textView;

    public ErrorLayout(Context context) {
        this(context, null, 0);
    }

    public ErrorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.error_layout, this, true);
        textView = findViewById(R.id.warning_text_view);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ErrorLayout);
        String message = attributes.getString(R.styleable.ErrorLayout_text);
        if (message != null) {
            textView.setText(message);
        } else {
            textView.setText(null);
        }

        attributes.recycle();
    }

    public void setErrorMessage(String text) {
        textView.setText(text);
    }
}
