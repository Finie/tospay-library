package net.tospay.auth.ui.summary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;

public class LoginTypeDialog extends BottomSheetDialogFragment {

    static final String TAG = LoginTypeDialog.class.getSimpleName();
    private OnLoginTypeListener mListener;

    public LoginTypeDialog() {
        // Required empty public constructor
    }

    static LoginTypeDialog newInstance() {
        return new LoginTypeDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_guest).setOnClickListener(view1 -> {
            mListener.onLoginAsGuest();
            dismiss();
        });

        view.findViewById(R.id.btn_tospay).setOnClickListener(view12 -> {
            mListener.onLoginTospayUser();
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (OnLoginTypeListener) parent;
        } else {
            mListener = (OnLoginTypeListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLoginTypeListener {
        void onLoginAsGuest();
        void onLoginTospayUser();
    }
}
