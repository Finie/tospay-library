package net.tospay.auth.ui.dialog.network;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.databinding.DialogNetworkBinding;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.viewmodelfactory.GatewayViewModelFactory;
import net.tospay.auth.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class NetworkDialog extends BottomSheetDialogFragment {

    public static final String TAG = NetworkDialog.class.getSimpleName();
    private static final String ARG_ITEM_ISO = "iso";

    private DialogNetworkBinding mBinding;
    private NetworkSelectedListener mListener;
    private String iso;
    private List<Network> networks;
    private NetworkAdapter adapter;
    private NetworkViewModel mViewModel;

    public static NetworkDialog newInstance(String iso) {
        final NetworkDialog fragment = new NetworkDialog();
        final Bundle args = new Bundle();
        args.putString(ARG_ITEM_ISO, iso);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);
        this.networks = new ArrayList<>();
        this.adapter = new NetworkAdapter(networks);

        if (getArguments() != null) {
            iso = getArguments().getString(ARG_ITEM_ISO);
        }

        AppExecutors mAppExecutors = new AppExecutors();

        GatewayService gatewayService = ServiceGenerator.createService(GatewayService.class, getContext());

        GatewayRepository mGatewayRepository = new GatewayRepository(mAppExecutors, gatewayService);
        GatewayViewModelFactory factory = new GatewayViewModelFactory(mGatewayRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(NetworkViewModel.class);

        SharedPrefManager mSharedPrefManager = SharedPrefManager.getInstance(getContext());
        setBearerToken(mSharedPrefManager.getAccessToken());
    }

    public void setBearerToken(String token) {
        String bearerToken = "Bearer " + token;
        mViewModel.setBearerToken(bearerToken);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_network, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.setNetworkViewModel(mViewModel);
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        mBinding.recyclerView.setAdapter(adapter);

        mViewModel.networks(iso);
        mViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        if (resource.data != null) {
                            this.networks = resource.data;
                            adapter.setNetworks(networks);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (NetworkSelectedListener) parent;
        } else {
            mListener = (NetworkSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface NetworkSelectedListener {
        void onNetworkSelected(Network network);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_network_view, parent, false));
            text = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onNetworkSelected(networks.get(getAdapterPosition()));
                    dismiss();
                }
            });
        }
    }

    private class NetworkAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Network> networks;

        NetworkAdapter(List<Network> networks) {
            this.networks = networks;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Network network = networks.get(position);
            holder.text.setText(network.getOperator());
        }

        @Override
        public int getItemCount() {
            return networks == null ? 0 : networks.size();
        }

        void setNetworks(List<Network> networks) {
            this.networks = networks;
            notifyItemRangeChanged(0, networks.size());
        }
    }
}
