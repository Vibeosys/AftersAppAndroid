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

import com.aftersapp.R;
import com.aftersapp.adapters.PartyAdapter;
import com.aftersapp.data.PartyData;
import com.aftersapp.data.PartyLocation;
import com.aftersapp.data.requestdata.BaseRequestDTO;
import com.aftersapp.data.requestdata.GetPartyDTO;
import com.aftersapp.data.responsedata.PartyResponseDTO;
import com.aftersapp.utils.ServerRequestConstants;
import com.aftersapp.utils.ServerSyncManager;
import com.android.volley.VolleyError;
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
public class FindPartyFragment extends BaseFragment implements OnMapReadyCallback, ServerSyncManager.OnSuccessResultReceived, ServerSyncManager.OnErrorResultReceived {

    private static final String TAG = FindPartyFragment.class.getSimpleName();
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private PartyAdapter mPartyAdapter;
    private ListView mListParties;
    private ArrayList<PartyResponseDTO> partyResponseDTOs = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.find_party_fragment, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mListParties = (ListView) rootView.findViewById(R.id.listParties);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        mListParties.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* PartyData party = (PartyData) mPartyAdapter.getItem(position);
                drawMarker(party.getId());*/
                PartyDetailsFragment partyDetailsFragment = new PartyDetailsFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, partyDetailsFragment, "PartDetails").commit();
            }
        });
        mServerSyncManager.setOnStringErrorReceived(this);
        mServerSyncManager.setOnStringResultReceived(this);
        GetPartyDTO getPartyDTO = new GetPartyDTO(2, 18.520430, 73.856744);
        Gson gson = new Gson();
        String serializedJsonString = gson.toJson(getPartyDTO);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serializedJsonString);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_GET_PARTY,
                mSessionManager.getPartyUrl(), baseRequestDTO);
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
        for (int i = 0; i < partyResponseDTOs.size(); i++) {
            PartyResponseDTO partyData = partyResponseDTOs.get(i);
            LatLng location = new LatLng(partyData.getLatitude(), partyData.getLongitude());
            String partyName = partyData.getTitle();
            if (partyData.getPartyId() == selectedId) {
                MarkerOptions markerOptions = new MarkerOptions().
                        position(location).title(partyName)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));


                mGoogleMap.addMarker(markerOptions);
            } else {
                MarkerOptions markerOptions = new MarkerOptions().
                        position(location).title(partyName);
                mGoogleMap.addMarker(markerOptions);
            }

        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(18.5536022, 73.7942691)).zoom(13).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onVolleyErrorReceived(@NonNull VolleyError error, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_GET_PARTY:
                Log.e(TAG, "##Volley Server error " + error.toString());
                break;
        }
    }

    @Override
    public void onDataErrorReceived(int errorCode, String errorMessage, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_GET_PARTY:
                Log.d(TAG, "##Volley Data error " + errorMessage);
                break;
        }
    }

    @Override
    public void onResultReceived(@NonNull String data, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_GET_PARTY:
                partyResponseDTOs = PartyResponseDTO.deserializeToArray(data);
                mPartyAdapter = new PartyAdapter(partyResponseDTOs, getContext());
                mListParties.setAdapter(mPartyAdapter);
                drawMarker(0);
                AsyncInsertDB asyncInsertDB = new AsyncInsertDB(partyResponseDTOs);
                asyncInsertDB.execute();
                Log.d(TAG, "##Volley Response" + data);
                break;
        }
    }

    private class AsyncInsertDB extends AsyncTask<Void, Void, Boolean> {

        List<PartyResponseDTO> partyResponseDTOs;

        AsyncInsertDB(List<PartyResponseDTO> partyResponseDTOs) {
            this.partyResponseDTOs = partyResponseDTOs;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean flagQuery = mDbRepository.insertParty(partyResponseDTOs);
            return flagQuery;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {


            }
        }
    }
}
