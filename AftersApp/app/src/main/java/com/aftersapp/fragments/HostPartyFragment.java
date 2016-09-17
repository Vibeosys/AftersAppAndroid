package com.aftersapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;


public class HostPartyFragment extends BaseFragment implements AdapterView.OnItemSelectedListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner mSpinner;

    private TextView mGoogleMapTextView;
     MapView mMapView;
    GoogleMap mGoogleMap;

    public HostPartyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HostPartyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HostPartyFragment newInstance(String param1, String param2) {
        HostPartyFragment fragment = new HostPartyFragment();
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
                            final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_host_party, container, false);
        mSpinner =(Spinner) rootView.findViewById(R.id.Agespinner);

        mGoogleMapTextView = (TextView) rootView.findViewById(R.id.partyAddressTextView);

        List<String> spineerData =  new ArrayList<>();
        spineerData.add("10+");
        spineerData.add("20+");
        spineerData.add("30+");
        spineerData.add("40+");
        spineerData.add("50+");
        spineerData.add("60+");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,spineerData);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(dataAdapter);
        mGoogleMapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTakeawayDialog(savedInstanceState);
            }
        });
        return rootView;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showTakeawayDialog(Bundle savedInstanceState) {
        final Dialog dlg = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        View view = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_google_map, null);
        dlg.setContentView(view);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mMapView = (MapView)dlg.findViewById(R.id.mapViewParty);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                // For showing a move to my location button
                mGoogleMap.setMyLocationEnabled(true);
            }
        });
        dlg.show();

    }
}
