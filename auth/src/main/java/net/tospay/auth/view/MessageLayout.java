package net.tospay.auth.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import net.tospay.auth.R;

public class MessageLayout extends LinearLayout {

    public MessageLayout(Context context) {
        this(context, null, 0);
    }

    public MessageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.message_layout, this, true);
    }
}