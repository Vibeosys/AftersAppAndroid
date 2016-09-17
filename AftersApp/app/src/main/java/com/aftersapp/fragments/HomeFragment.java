package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aftersapp.R;

/**
 * Created by akshay on 16-09-2016.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout mLayoutFindParty,mLayoutHostParty,mLayoutEditProfile;
    private static final String HOME_FRAGMENT = "home";
    private static final String SEARCH_FRAGMENT = "search";
    private static final String HOST_FRAGMENT = "host";
    private static final String MORE_FRAGMENT = "more";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        mLayoutFindParty = (LinearLayout) rootView.findViewById(R.id.layoutFindParty);
        mLayoutHostParty = (LinearLayout) rootView.findViewById(R.id.layoutHostParty);
        mLayoutEditProfile = (LinearLayout) rootView.findViewById(R.id.layoutEditProfile);
        mLayoutEditProfile.setOnClickListener(this);
        mLayoutFindParty.setOnClickListener(this);
        mLayoutHostParty.setOnClickListener(this);
        return rootView;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.layoutFindParty:
                break;
            case R.id.layoutHostParty:
                HostPartyFragment hostPartyFragment = new HostPartyFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay,hostPartyFragment,HOST_FRAGMENT).commit();
                break;
            case R.id.layoutEditProfile:
                EditMyProfileFragment editMyProfileFragment = new EditMyProfileFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay,editMyProfileFragment,MORE_FRAGMENT).commit();

        }

    }
}
