package com.aftersapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aftersapp.R;


public class PartyDetailsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CHAT_HOST_FRAGMENT = "Chat with host";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mBtnChatHost;

    public PartyDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PartyDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PartyDetailsFragment newInstance(String param1, String param2) {
        PartyDetailsFragment fragment = new PartyDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_party_details, container, false);
        mBtnChatHost = (Button) view.findViewById(R.id.chatHost);
        mBtnChatHost.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.chatHost:
                ChatWithHostFragment chatWithHostFragment = new ChatWithHostFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, chatWithHostFragment, CHAT_HOST_FRAGMENT).commit();
                break;
        }
    }
}
