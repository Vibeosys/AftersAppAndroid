package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aftersapp.R;
import com.aftersapp.adapters.PartyAdapter;
import com.aftersapp.data.PartyData;
import com.aftersapp.data.PartyLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by akshay on 16-09-2016.
 */
public class FindPartyFragment extends BaseFragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private PartyAdapter mPartyAdapter;
    private ArrayList<PartyData> partyDatas = new ArrayList<>();
    private ListView mListParties;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        partyDatas.add(new PartyData(1, "Chilled House Party", "Join us for a relaxed house party with " +
                "some friends feel free to bring some more winel", 21, 4, new PartyLocation(18.5527173, 73.7889688)));
        partyDatas.add(new PartyData(2, "Chilled House Party 1", "Join us for a relaxed house party with " +
                "some friends feel free to bring some more winel", 21, 4, new PartyLocation(18.5570181, 73.7742083)));
        partyDatas.add(new PartyData(3, "Chilled House Party 2", "Join us for a relaxed house party with " +
                "some friends feel free to bring some more winel", 21, 4, new PartyLocation(18.5558057, 73.7774548)));
        partyDatas.add(new PartyData(4, "Chilled House Party 3", "Join us for a relaxed house party with " +
                "some friends feel free to bring some more winel", 21, 4, new PartyLocation(18.5609341, 73.7780928)));
        partyDatas.add(new PartyData(5, "Chilled House Party 4", "Join us for a relaxed house party with " +
                "some friends feel free to bring some more winel", 21, 4, new PartyLocation(18.5575678, 73.8014221)));
        partyDatas.add(new PartyData(6, "Chilled House Party 5", "Join us for a relaxed house party with " +
                "some friends feel free to bring some more winel", 21, 4, new PartyLocation(18.5575678, 73.8014221)));

        mPartyAdapter = new PartyAdapter(partyDatas, getContext());
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
        mListParties.setAdapter(mPartyAdapter);
        mListParties.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PartyData party = (PartyData) mPartyAdapter.getItem(position);
                drawMarker(party.getId());
            }
        });
        return rootView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        // For showing a move to my location button
        mGoogleMap.setMyLocationEnabled(true);
        drawMarker(0);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(18.5536022, 73.7942691)).zoom(13).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void drawMarker(long selectedId) {
        // Creating an instance of MarkerOptions
        mGoogleMap.clear();
        for (int i = 0; i < partyDatas.size(); i++) {
            PartyData partyData = partyDatas.get(i);
            LatLng location = partyData.getPartyLocation().getPosition();
            String partyName = partyData.getPartyName();
            if (partyData.getId() == selectedId) {
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

    }

}
