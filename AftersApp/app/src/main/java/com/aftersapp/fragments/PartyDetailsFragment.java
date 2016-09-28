package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.R;
import com.aftersapp.data.PartyDataDTO;
import com.aftersapp.data.requestdata.BaseRequestDTO;
import com.aftersapp.data.requestdata.LikePartyRequest;
import com.aftersapp.helper.ChatHelper;
import com.aftersapp.utils.AppConstants;
import com.aftersapp.utils.CustomVolleyRequestQueue;
import com.aftersapp.utils.NetworkUtils;
import com.aftersapp.utils.ServerRequestConstants;
import com.aftersapp.utils.ServerSyncManager;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;


public class PartyDetailsFragment extends BaseFragment implements View.OnClickListener,
        ServerSyncManager.OnSuccessResultReceived,
        ServerSyncManager.OnErrorResultReceived {

    public static final String PARTY_ID = "party_id";
    private static final String CHAT_HOST_FRAGMENT = "Chat with host";
    private static final String TAG = PartyDetailsFragment.class.getSimpleName();
    private ImageLoader mImageLoader;
    private long mPartyId;
    private PartyDataDTO partyData;
    private Button mBtnChatHost, iamAttending, saveFavourite;
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
        mBtnChatHost = (Button) view.findViewById(R.id.btnChatWithHost);
        iamAttending = (Button) view.findViewById(R.id.iamAttending);
        saveFavourite = (Button) view.findViewById(R.id.saveFavourite);
        mTxtPartyName = (TextView) view.findViewById(R.id.txtPartyName);
        mTxtDesc = (TextView) view.findViewById(R.id.txtDesc);
        mTxtAddress = (TextView) view.findViewById(R.id.txtAddress);
        mTxtAge = (TextView) view.findViewById(R.id.txtAge);
        mTxtAttending = (TextView) view.findViewById(R.id.txtAttending);
        networkImageView = (NetworkImageView) view.findViewById(R.id.imgPartyImage);
        mTxtPartyName.setText(partyData.getTitle());
        mTxtDesc.setText(partyData.getDesc());
        mServerSyncManager.setOnStringErrorReceived(this);
        mServerSyncManager.setOnStringResultReceived(this);
        mTxtAddress.setText(partyData.getLocation());
        mTxtAge.setText(partyData.getAge());
        mTxtAttending.setText("" + partyData.getAttending());
        setImage();
        if (partyData.getHost() == mSessionManager.getUserId()) {
            mBtnChatHost.setVisibility(View.INVISIBLE);
        }
        mBtnChatHost.setOnClickListener(this);
        iamAttending.setOnClickListener(this);
        saveFavourite.setOnClickListener(this);
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
            case R.id.btnChatWithHost:
                getHostUser();
                break;
            case R.id.saveFavourite:
                if (NetworkUtils.isActiveNetworkAvailable(getContext())) {
                    addToFavParty();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().
                            getString(R.string.str_connect_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iamAttending:
                if (NetworkUtils.isActiveNetworkAvailable(getContext())) {
                    attendancePartyMark();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().
                            getString(R.string.str_connect_internet), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void chatWithHost(final QBUser selectedUsers) {

        progressDialog.show();
        final QBChatService chatService = QBChatService.getInstance();
        QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
        chatService.setDebugEnabled(true);
        chatService.setDefaultPacketReplyTimeout(150000); //add this
        chatService.setDefaultConnectionTimeout(150000); //add this
        chatService.setUseStreamManagement(true);
        //chatService.addConnectionListener(chatConnectionListener);
        final QBUser user = new QBUser(mSessionManager.getEmail(), mSessionManager.getEmail() + mSessionManager.getUserId());
        QBAuth.createSession(user, new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success, login to chat

                user.setId(session.getUserId());

                chatService.login(user, new QBEntityCallback() {

                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        progressDialog.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                createDialog(selectedUsers);
                            }
                        });
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e("UserList", errors.getMessage());
                        progressDialog.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                createDialog(selectedUsers);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }

    private void createDialog(final QBUser selectedUsers) {
        progressDialog.show();
        ChatHelper.getInstance().createDialogWithSelectedUser(selectedUsers,
                new QBEntityCallback<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {
                        progressDialog.dismiss();
                        ChatFragment chatFragment = new ChatFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ChatFragment.EXTRA_DIALOG, dialog);
                        chatFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().
                                replace(R.id.fragment_frame_lay, chatFragment, "ChatFragment").commit();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.msg_err),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void getHostUser() {

        QBUsers.getUserByExternalId(String.valueOf(partyData.getHost()), new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                chatWithHost(user);
            }

            @Override
            public void onError(QBResponseException errors) {
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.str_host_not_found), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToFavParty() {
        partyData.setIsFavourite(AppConstants.FAV_PARTY);
        LikePartyRequest likePartyRequest = new LikePartyRequest(mSessionManager.getUserId(), partyData.getPartyId());
        Gson gson = new Gson();
        String serializedJsonString = gson.toJson(likePartyRequest);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serializedJsonString);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_ADD_FAV_PARTY,
                mSessionManager.addFavPartyUrl(), baseRequestDTO);
    }

    private void attendancePartyMark() {
        partyData.setAttending(AppConstants.ATTENDING_PARTY);
        LikePartyRequest likePartyRequest = new LikePartyRequest(mSessionManager.getUserId(), partyData.getPartyId());
        Gson gson = new Gson();
        String serializedJsonString = gson.toJson(likePartyRequest);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serializedJsonString);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_LIKE_PARTY,
                mSessionManager.getLikePartyUrl(), baseRequestDTO);
    }

    @Override
    public void onVolleyErrorReceived(@NonNull VolleyError error, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_LIKE_PARTY:
                Log.e(TAG, "##Volley Server error " + error.toString());
                break;
            case ServerRequestConstants.REQUEST_ADD_FAV_PARTY:
                Log.e(TAG, "##Volley Server error " + error.toString());
                break;
        }
    }

    @Override
    public void onDataErrorReceived(int errorCode, String errorMessage, int requestToken) {
        switch (requestToken) {

            case ServerRequestConstants.REQUEST_LIKE_PARTY:
                Log.d(TAG, "##Volley Data error " + errorMessage);
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.str_already_like), Toast.LENGTH_SHORT).show();
                break;
            case ServerRequestConstants.REQUEST_ADD_FAV_PARTY:
                Log.d(TAG, "##Volley Data error " + errorMessage);
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.str_already_fav), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onResultReceived(@NonNull String data, int requestToken) {
        switch (requestToken) {

            case ServerRequestConstants.REQUEST_LIKE_PARTY:
                if (data.equals("0")) {
                    Toast.makeText(getContext(), getContext().getResources().
                            getString(R.string.party_like_success), Toast.LENGTH_SHORT).show();
                }
                break;
            case ServerRequestConstants.REQUEST_ADD_FAV_PARTY:
                if (data.equals("0")) {
                    Toast.makeText(getContext(), getContext().getResources().
                            getString(R.string.party_add_fav_success), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
