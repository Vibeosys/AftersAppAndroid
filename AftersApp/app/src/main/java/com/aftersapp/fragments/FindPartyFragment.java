package com.aftersapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.R;
import com.aftersapp.adapters.PartyAdapter;
import com.aftersapp.data.requestdata.BaseRequestDTO;
import com.aftersapp.data.requestdata.GetPartyDTO;
import com.aftersapp.data.PartyDataDTO;
import com.aftersapp.data.requestdata.LikePartyRequest;
import com.aftersapp.services.GPSTracker;
import com.aftersapp.utils.AppConstants;
import com.aftersapp.utils.NetworkUtils;
import com.aftersapp.utils.ServerRequestConstants;
import com.aftersapp.utils.ServerSyncManager;
import com.android.volley.VolleyError;
import com.google.android.gms.games.appcontent.AppContentAction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay on 16-09-2016.
 */
public class FindPartyFragment extends BaseFragment implements
        OnMapReadyCallback, ServerSyncManager.OnSuccessResultReceived,
        ServerSyncManager.OnErrorResultReceived, PartyAdapter.OnLikeOrFavClick {

    private static final String TAG = FindPartyFragment.class.getSimpleName();
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private PartyAdapter mPartyAdapter;
    private ListView mListParties;
    private ArrayList<PartyDataDTO> partyDataDTOs = new ArrayList<>();
    private ProgressBar progressBar;
    private GPSTracker gps;
    private double latitude;
    double longitude;
    private TextView txtErrorMsg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GPS Code
        gps = new GPSTracker(getContext());

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            // \n is for new line
            //Toast.makeText(getContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.find_party_fragment, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mListParties = (ListView) rootView.findViewById(R.id.listParties);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        txtErrorMsg = (TextView) rootView.findViewById(R.id.txtErrorMsg);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        mServerSyncManager.setOnStringErrorReceived(this);
        mServerSyncManager.setOnStringResultReceived(this);


        if (NetworkUtils.isActiveNetworkAvailable(getContext())) {
            showProgress(true, mListParties, progressBar);
            GetPartyDTO getPartyDTO = new GetPartyDTO(mSessionManager.getUserId(), 18.520430, 73.856744);
            Gson gson = new Gson();
            String serializedJsonString = gson.toJson(getPartyDTO);
            BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
            baseRequestDTO.setData(serializedJsonString);
            mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_GET_PARTY,
                    mSessionManager.getPartyUrl(), baseRequestDTO);
        } else {
            partyDataDTOs = mDbRepository.getParties();
            setAdapter(false);
        }


        return rootView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        // For showing a move to my location button
        mGoogleMap.setMyLocationEnabled(true);
    }

    private void drawMarker(long selectedId) {
        // Creating an instance of MarkerOptions
        mGoogleMap.clear();
        LatLng selectedLocation = new LatLng(latitude, longitude);
        for (int i = 0; i < partyDataDTOs.size(); i++) {
            PartyDataDTO partyData = partyDataDTOs.get(i);
            LatLng location = new LatLng(partyData.getLatitude(), partyData.getLongitude());
            String partyName = partyData.getTitle();
            if (partyData.getPartyId() == selectedId) {
                MarkerOptions markerOptions = new MarkerOptions().
                        position(location).title(partyName)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
                selectedLocation = new LatLng(partyData.getLatitude(), partyData.getLongitude());

                mGoogleMap.addMarker(markerOptions);
            } else {
                MarkerOptions markerOptions = new MarkerOptions().
                        position(location).title(partyName);
                mGoogleMap.addMarker(markerOptions);
            }

        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(selectedLocation).zoom(13).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onVolleyErrorReceived(@NonNull VolleyError error, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_GET_PARTY:
                showProgress(false, mListParties, progressBar);
                Log.e(TAG, "##Volley Server error " + error.toString());
                break;
            case ServerRequestConstants.REQUEST_LIKE_PARTY:
                showProgress(false, mListParties, progressBar);
                Log.e(TAG, "##Volley Server error " + error.toString());
                break;
            case ServerRequestConstants.REQUEST_REMOVE_FAV_PARTY:
                showProgress(false, mListParties, progressBar);
                Log.e(TAG, "##Volley Server error " + error.toString());
                break;
            case ServerRequestConstants.REQUEST_ADD_FAV_PARTY:
                showProgress(false, mListParties, progressBar);
                Log.e(TAG, "##Volley Server error " + error.toString());
                break;
        }
        txtErrorMsg.setVisibility(View.VISIBLE);
        mListParties.setVisibility(View.GONE);
        txtErrorMsg.setText(getString(R.string.str_err_msg));
    }

    @Override
    public void onDataErrorReceived(int errorCode, String errorMessage, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_GET_PARTY:
                showProgress(false, mListParties, progressBar);
                Log.d(TAG, "##Volley Data error " + errorMessage);
                txtErrorMsg.setVisibility(View.VISIBLE);
                mListParties.setVisibility(View.GONE);
                break;
            case ServerRequestConstants.REQUEST_LIKE_PARTY:
                showProgress(false, mListParties, progressBar);
                Log.d(TAG, "##Volley Data error " + errorMessage);
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.str_already_like), Toast.LENGTH_SHORT).show();
                break;
            case ServerRequestConstants.REQUEST_REMOVE_FAV_PARTY:
                showProgress(false, mListParties, progressBar);
                Log.d(TAG, "##Volley Data error " + errorMessage);
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.str_already_removed_fav), Toast.LENGTH_SHORT).show();
                break;
            case ServerRequestConstants.REQUEST_ADD_FAV_PARTY:
                showProgress(false, mListParties, progressBar);
                Log.d(TAG, "##Volley Data error " + errorMessage);
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.str_already_fav), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onResultReceived(@NonNull String data, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_GET_PARTY:
                partyDataDTOs = PartyDataDTO.deserializeToArray(data);
                AsyncInsertDB asyncInsertDB = new AsyncInsertDB(partyDataDTOs);
                asyncInsertDB.execute();
                Log.d(TAG, "##Volley Response" + data);
                break;
            case ServerRequestConstants.REQUEST_LIKE_PARTY:
                if (data.equals("0")) {
                    Toast.makeText(getContext(), getContext().getResources().
                            getString(R.string.party_like_success), Toast.LENGTH_SHORT).show();
                }
                break;
            case ServerRequestConstants.REQUEST_REMOVE_FAV_PARTY:
                if (data.equals("0")) {
                    Toast.makeText(getContext(), getContext().getResources().
                            getString(R.string.party_removed_fav_success), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onLikeClickListener(PartyDataDTO partyDataDTO, int position, int value) {
        if (NetworkUtils.isActiveNetworkAvailable(getContext())) {
            if (value == AppConstants.ATTENDING_PARTY) {
                Toast.makeText(getContext(), getContext().getResources().
                        getString(R.string.str_already_like), Toast.LENGTH_SHORT).show();
            } else {
                attendancePartyMark(partyDataDTO);
            }
        } else {
            Toast.makeText(getContext(), getContext().getResources().
                    getString(R.string.str_connect_internet), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onFavClickListener(PartyDataDTO partyDataDTO, int position, int value) {
        if (NetworkUtils.isActiveNetworkAvailable(getContext())) {
            if (value == AppConstants.NOT_FAV_PARTY) {
                addToFavParty(partyDataDTO);
            } else if (value == AppConstants.FAV_PARTY) {
                removeFavParty(partyDataDTO);
            }
        } else {
            Toast.makeText(getContext(), getContext().getResources().
                    getString(R.string.str_connect_internet), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemClickListener(PartyDataDTO partyDataDTO, int position) {

        PartyDetailsFragment partyDetailsFragment = new PartyDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(PartyDetailsFragment.PARTY_ID, partyDataDTO.getPartyId());
        partyDetailsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().
                replace(R.id.fragment_frame_lay, partyDetailsFragment, "PartDetails").commit();
    }

    @Override
    public void onImageClickListener(PartyDataDTO partyDataDTO, int position) {
        drawMarker(partyDataDTO.getPartyId());
    }

    private class AsyncInsertDB extends AsyncTask<Void, Void, Boolean> {

        List<PartyDataDTO> partyDatas;

        AsyncInsertDB(List<PartyDataDTO> partyDatas) {
            this.partyDatas = partyDatas;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean flagQuery = mDbRepository.insertParty(partyDatas);
            return flagQuery;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            showProgress(false, mListParties, progressBar);
            setAdapter(true);
        }
    }

    private void setAdapter(boolean netFlag) {
        mPartyAdapter = new PartyAdapter(partyDataDTOs, getContext());
        mListParties.setAdapter(mPartyAdapter);
        mPartyAdapter.setLikeOrFavClick(this);
        if (netFlag)
            drawMarker(0);
    }

    private void attendancePartyMark(PartyDataDTO partyDataDTO) {
        partyDataDTO.setAttending(AppConstants.ATTENDING_PARTY);
        mPartyAdapter.notifyDataSetChanged();
        LikePartyRequest likePartyRequest = new LikePartyRequest(mSessionManager.getUserId(), partyDataDTO.getPartyId());
        Gson gson = new Gson();
        String serializedJsonString = gson.toJson(likePartyRequest);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serializedJsonString);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_LIKE_PARTY,
                mSessionManager.getLikePartyUrl(), baseRequestDTO);
    }

    private void removeFavParty(PartyDataDTO partyDataDTO) {
        partyDataDTO.setIsFavourite(AppConstants.NOT_FAV_PARTY);
        mPartyAdapter.notifyDataSetChanged();
        LikePartyRequest likePartyRequest = new LikePartyRequest(mSessionManager.getUserId(), partyDataDTO.getPartyId());
        Gson gson = new Gson();
        String serializedJsonString = gson.toJson(likePartyRequest);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serializedJsonString);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_REMOVE_FAV_PARTY,
                mSessionManager.removeFavPartyUrl(), baseRequestDTO);
    }

    private void addToFavParty(PartyDataDTO partyDataDTO) {
        partyDataDTO.setIsFavourite(AppConstants.FAV_PARTY);
        mPartyAdapter.notifyDataSetChanged();
        LikePartyRequest likePartyRequest = new LikePartyRequest(mSessionManager.getUserId(), partyDataDTO.getPartyId());
        Gson gson = new Gson();
        String serializedJsonString = gson.toJson(likePartyRequest);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serializedJsonString);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_ADD_FAV_PARTY,
                mSessionManager.addFavPartyUrl(), baseRequestDTO);
    }
}
