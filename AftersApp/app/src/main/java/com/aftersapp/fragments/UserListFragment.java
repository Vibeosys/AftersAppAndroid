package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aftersapp.R;

/**
 * Created by akshay on 17-09-2016.
 */
public class UserListFragment extends BaseFragment {

    private static final String CHAT_HOST_FRAGMENT = "Chat";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_list_fragment, container, false);
        return rootView;
    }

}
