package net.tospay.auth.ui.account;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import net.tospay.auth.R;
import net.tospay.auth.ui.account.account_fragments.BankAccount;
import net.tospay.auth.ui.account.account_fragments.CardAccount;
import net.tospay.auth.ui.account.account_fragments.MobileAccount;
import net.tospay.auth.ui.account.adapter.SectionsPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountsFragment extends Fragment {


    public AccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_accounts, container, false);


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), getFragmentManager());
        sectionsPagerAdapter.addFragment(new CardAccount(),getResources().getString(R.string.card_account));
        sectionsPagerAdapter.addFragment(new BankAccount(),getResources().getString(R.string.bank_account));
        sectionsPagerAdapter.addFragment(new MobileAccount(),getResources().getString(R.string.mobile_account));
        ViewPager viewPager =view.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        return view;
    }

}
