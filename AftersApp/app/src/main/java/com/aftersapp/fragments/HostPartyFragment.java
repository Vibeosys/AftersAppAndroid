package com.aftersapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.R;
import com.aftersapp.adapters.PartyAdapter;
import com.aftersapp.data.PartyDataDTO;
import com.aftersapp.data.requestdata.BaseRequestDTO;
import com.aftersapp.data.responsedata.HostPartyDTO;
import com.aftersapp.utils.ServerRequestConstants;
import com.aftersapp.utils.ServerSyncManager;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HostPartyFragment extends BaseFragment implements AdapterView.OnItemSelectedListener,OnMapReadyCallback,ServerSyncManager.OnSuccessResultReceived, ServerSyncManager.OnErrorResultReceived  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner mSpinner;
    private TextView mGoogleMapTextView;
    private Button mSearchBtn,mapOkBtn,mapCancelBtn,mHostParty;
    private EditText mSearchEditText;
    private ImageView mUserPartyPhoto;
    MapView mMapView;
    GoogleMap mGoogleMap;
    private int EDIT_PROFILE_MEDIA_PERMISSION_CODE = 19;
    private int EDIT_SELECT_IMAGE=20;
    private String mImageUri,imgDecodableString;
    double mFinalLatititude,mFinalLongitude;
    private String mFinalAddress;


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
        mUserPartyPhoto = (ImageView) rootView.findViewById(R.id.userPartyPhoto);
        mHostParty =(Button) rootView.findViewById(R.id.saveParty);
        List<String> spineerData =  new ArrayList<>();
        spineerData.add("10+");
        spineerData.add("20+");
        spineerData.add("30+");
        spineerData.add("40+");
        spineerData.add("50+");
        spineerData.add("60+");
        mServerSyncManager.setOnStringErrorReceived(this);
        mServerSyncManager.setOnStringResultReceived(this);
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
        mUserPartyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestGrantPermission();
            }
        });
        mHostParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTToWebService();
            }
        });
        return rootView;
    }

    private void callTToWebService() {

        Gson gson = new Gson();
        HostPartyDTO hostPartyDTO = new HostPartyDTO("New Test ThinkPad",
                "This is Testing Part",Double.parseDouble("18.5081477"),Double.parseDouble("73.8361482"),"This is testing Address","Music Generation","10+","0","0","0","2","1474354108");
        String serlize =gson.toJson(hostPartyDTO);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serlize);

        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_POST_PART,
                mSessionManager.getHostPartyUrl(), baseRequestDTO);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == EDIT_SELECT_IMAGE && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                try {
                    Bitmap mBitmapString = BitmapFactory.decodeFile(imgDecodableString);
                    mImageUri=imgDecodableString.toString();
                    mUserPartyPhoto.setImageBitmap(mBitmapString);
                } catch (Exception e) {
                    e.toString();
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getActivity(),
                            "Memory Error cannot upload picture", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void showTakeawayDialog(Bundle savedInstanceState) {
        final Dialog dlg = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        View view = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_google_map, null);
        dlg.setContentView(view);

        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mSearchBtn =(Button) dlg.findViewById(R.id.searchBtn);
        mMapView = (MapView)dlg.findViewById(R.id.mapViewParty);
        mapOkBtn = (Button) dlg.findViewById(R.id.savePartyMap);
        mapCancelBtn = (Button) dlg.findViewById(R.id.cancelPartyMap);
        mSearchEditText =(EditText) dlg.findViewById(R.id.searchAddress);
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
                LatLng DemoLatLong =  new LatLng(35.0958634, 33.338747);
                // For showing a move to my location button
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {

                        CameraPosition cameraPosition1 = mGoogleMap.getCameraPosition();
                        double mLatLng = cameraPosition.target.latitude;
                        double mLat = cameraPosition.target.latitude;
                        LatLng Demo = new LatLng(mLatLng, mLat);

                        //mGoogleMap.addMarker(new MarkerOptions().position(Demo)).setDraggable(true);

                        Toast toast = Toast.makeText(getContext(),"Camera is changed "+cameraPosition1,Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                      //  Marker marker =mGoogleMap.addMarker(new MarkerOptions().position(test));

                    }
                });

               /* CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(35.0958634, 33.338747)).zoom(12).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                Marker marker =mGoogleMap.addMarker(new MarkerOptions().position(DemoLatLong).title("Latsia,cyprus"));*/
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        double lat = latLng.latitude;
                        double log = latLng.longitude;
                        mGoogleMap.clear();
                        Marker marker =mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(false));
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(getContext(), Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(lat, log, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            setResult(address,city,lat,log);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
                mSearchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(mSearchEditText.getText().toString()))
                        {
                            String userAddress = mSearchEditText.getText().toString();
                            Geocoder coder = new Geocoder(getContext());
                            try {
                                ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(userAddress, 50);
                                for(Address add : adresses){
                                   // if (statement) {//Controls to ensure it is right address such as country etc.
                                        double longitude = add.getLongitude();
                                        double latitude = add.getLatitude();
                                    LatLng DemoLatLong = new LatLng(latitude, longitude);
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(DemoLatLong).zoom(12).build();
                                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    Marker marker =mGoogleMap.addMarker(new MarkerOptions().position(DemoLatLong).title("Latsia,cyprus"));
                                    Toast toast = Toast.makeText(getContext(),"lat "+latitude+"long "+longitude,Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();
                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();
                                    setResult(address,city,latitude,longitude);
                                   // }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                       /* setResult();
                        dlg.dismiss();
                   */ }
                });
                mapCancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.dismiss();
                    }
                });
                mapOkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.dismiss();
                    }
                });
            }

        });

       /* mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dlg.cancel();
                Toast toast = Toast.makeText(getContext(),"Search Btn is clicked",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });*/
        dlg.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng DemoLatLong =  new LatLng(35.1264, 33.4299);
        mGoogleMap.setMyLocationEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(35.1264, 33.4299)).zoom(13).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

       // Marker marker =mGoogleMap.addMarker(new MarkerOptions().position(DemoLatLong).title("cyprus"));
        //mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public  void setResult(String address,String city,double latitude,double longitude)
    {
        String test = address+city;
        mGoogleMapTextView.setText(""+test);
        mFinalAddress = test;
        mFinalLatititude = latitude;
        mFinalLongitude = longitude;
    }
    private void requestGrantPermission() {

        requestPermissions(new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                EDIT_PROFILE_MEDIA_PERMISSION_CODE);
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EDIT_PROFILE_MEDIA_PERMISSION_CODE && grantResults[1] == 0) {
            openGallery();
        } else {
            Toast toast = Toast.makeText(getActivity(),
                    "User denied permission", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EDIT_PROFILE_MEDIA_PERMISSION_CODE && grantResults[1] == 0) {
            openGallery();
        } else {
            Toast toast = Toast.makeText(getActivity(),
                    "User denied permission", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, EDIT_SELECT_IMAGE);
    }

    @Override
    public void onVolleyErrorReceived(@NonNull VolleyError error, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_POST_PART:
                Log.e("TAG", "##Volley Server error " + error.toString());
                break;
        }
    }

    @Override
    public void onDataErrorReceived(int errorCode, String errorMessage, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_POST_PART:
                Log.d("TAG", "##Volley Data error " + errorMessage);
                break;
        }
    }

    @Override
    public void onResultReceived(@NonNull String data, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_POST_PART:

                break;
        }
    }
}
