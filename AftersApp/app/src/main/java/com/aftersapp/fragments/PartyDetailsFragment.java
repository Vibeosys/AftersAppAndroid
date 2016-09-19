package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aftersapp.R;
import com.aftersapp.data.PartyDataDTO;
import com.aftersapp.utils.CustomVolleyRequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;


public class PartyDetailsFragment extends BaseFragment implements View.OnClickListener {

    public static final String PARTY_ID = "party_id";
    private static final String CHAT_HOST_FRAGMENT = "Chat with host";
    private ImageLoader mImageLoader;
    private long mPartyId;
    private PartyDataDTO partyData;
    private Button mBtnChatHost;
    private TextView mTxtPartyName, mTxtDesc, mTxtAddress, mTxtAge, mTxtAttending;
    private NetworkImageView networkImageView;

    public PartyDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPartyId = getArguments().getLong(PARTY_ID);
            partyData = mDbRepository.getPartyData(mPartyId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_party_details, container, false);
        mBtnChatHost = (Button) view.findViewById(R.id.chatHost);

        mTxtPartyName = (TextView) view.findViewById(R.id.txtPartyName);
        mTxtDesc = (TextView) view.findViewById(R.id.txtDesc);
        mTxtAddress = (TextView) view.findViewById(R.id.txtAddress);
        mTxtAge = (TextView) view.findViewById(R.id.txtAge);
        mTxtAttending = (TextView) view.findViewById(R.id.txtAttending);
        networkImageView = (NetworkImageView) view.findViewById(R.id.imgPartyImage);
        mTxtPartyName.setText(partyData.getTitle());
        mTxtDesc.setText(partyData.getDesc());
        mTxtAddress.setText(partyData.getLocation());
        mTxtAge.setText(partyData.getAge());
        mTxtAttending.setText("" + partyData.getAttending());
        setImage();
        mBtnChatHost.setOnClickListener(this);
        return view;
    }

    private void setImage() {
        mImageLoader = CustomVolleyRequestQueue.getInstance(getContext())
                .getImageLoader();
        final String url = partyData.getImage();
        if (url != null && !url.isEmpty()) {
            try {
                mImageLoader.get(url, ImageLoader.getImageListener(networkImageView,
                        R.drawable.party1, R.drawable.party1));
                networkImageView.setImageUrl(url, mImageLoader);
            } catch (Exception e) {
                networkImageView.setImageResource(R.drawable.party1);
            }
        } else {
            networkImageView.setImageResource(R.drawable.party1);
        }
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
