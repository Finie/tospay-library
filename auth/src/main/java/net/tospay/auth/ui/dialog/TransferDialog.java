package net.tospay.auth.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.event.NotificationEvent;

public class TransferDialog extends BottomSheetDialogFragment {

    public static final String TAG = "TransferDialog";
    private static final String KEY_NOTIFICATION = "notification";
    private NotificationEvent event;

    public TransferDialog() {
        // Required empty public constructor
    }

    public static TransferDialog newInstance(NotificationEvent notification) {
        TransferDialog dialog = new TransferDialog();
        Bundle args = new Bundle();
        args.putParcelable(KEY_NOTIFICATION, notification);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);
        if (getArguments() != null) {
            event = getArguments().getParcelable(KEY_NOTIFICATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);

        titleTextView.setText(String.format("%s %s", event.getPayload().getTopic(), event.getPayload().getStatus()));
        messageTextView.setText(event.getPayload().getMessage());

        if (event.getPayload().getStatus().equals("FAILED")) {
            titleTextView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.red_palette_error));
            imageView.setImageResource(R.drawable.ic_failed_illustration);
        } else {
            titleTextView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.green));
            imageView.setImageResource(R.drawable.ic_success_illustration);
        }

        view.findViewById(R.id.btn_close).setOnClickListener(view1 -> dismiss());
    }
}
