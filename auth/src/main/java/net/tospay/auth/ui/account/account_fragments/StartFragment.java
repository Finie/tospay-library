package net.tospay.auth.ui.account.account_fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.tospay.auth.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {


    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_start, container, false);
        String link = getActivity().getIntent().getStringExtra("link");
        if (link.equalsIgnoreCase("bank")){
            NavHostFragment.findNavController(this)
                    .navigate(StartFragmentDirections
                            .actionNavigateAccountsToNavigationLinkBank("account"));
        }
        else if (link.equalsIgnoreCase("mobile")){
            NavHostFragment.findNavController(this)
                    .navigate(StartFragmentDirections
                            .actionNavigationAccountSelectionToNavigationLinkMobileAccount("account"));
        }

        else if (link.equalsIgnoreCase("card")){
            NavHostFragment.findNavController(this)
                    .navigate(StartFragmentDirections
                            .actionNavigateAccountsToNavigationLinkCardAccount("account"));
        }
       return  view;
    }

}
