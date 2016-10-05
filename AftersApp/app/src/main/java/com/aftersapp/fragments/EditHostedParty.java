package com.aftersapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.R;
import com.aftersapp.data.PartyDataDTO;
import com.aftersapp.utils.DateUtils;
import com.aftersapp.utils.NetworkUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by shrinivas on 05-10-2016.
 */
public class EditHostedParty extends BaseFragment implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private String mParam1;
    private String mParam2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    PartyDataDTO partyDataDTO = null;
    private Spinner mSpinner, mMusicGenre, mAgeSpinner;
    private TextView mGoogleMapTextView, mPartyAddress;
    private ImageView mUserPartyPhoto;
    private EditText mSearchEditText, mPartyTitle, mPartyDescription, mPartyDatePicker, mPartyTimePicker;
    private Button mSearchBtn, mapOkBtn, mapCancelBtn, mHostParty, mRemoveImg, mCancelPartyBtn;
    ProgressDialog dialog;
    private Calendar mCalendar;
    private String spnMusicGnenr;
    private boolean addressFlag = false;
    private GoogleApiClient mGoogleApiClient;
    MapView mMapView;
    GoogleMap mGoogleMap;
    double mFinalLatititude, mFinalLongitude;
    private String mFinalAddress, mImageUri, imgDecodableString;
    private double GpsLatitude, GpsLongitude;
    private LocationRequest mLocationRequest;
    Location mLastLocation;
    private int EDIT_LOCATION_PERMISSION_CODE = 29;
    private int EDIT_SELECT_IMAGE = 30;
    private int EDIT_PROFILE_MEDIA_PERMISSION_CODE = 39;
    Bitmap convertedImg = null;
    DateUtils dateUtils = new DateUtils();

    /*public static EditHostedParty newInstance(String param1, String param2) {
        EditHostedParty fragment = new EditHostedParty();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = this.getArguments();
            Long Val = bundle.getLong("party_id");
            String strPartyId = String.valueOf(Val);
            partyDataDTO = mDbRepository.getPartyData(Val);
            Log.d("TAG", "TAG");
            Log.d("TAG", "TAG");
            mParam1 = getArguments().getString("party_id");
            Log.d("TAG", "TAG");
            Log.d("TAG", "TAG");
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }

        onRequestGpsPermission();
        //mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_host_party, container, false);
        mSpinner = (Spinner) rootView.findViewById(R.id.Agespinner);
        mMusicGenre = (Spinner) rootView.findViewById(R.id.musicGenre);

        mGoogleMapTextView = (TextView) rootView.findViewById(R.id.partyAddressTextView);
        mUserPartyPhoto = (ImageView) rootView.findViewById(R.id.userPartyPhoto);
        mHostParty = (Button) rootView.findViewById(R.id.updateParty);
        mPartyTitle = (EditText) rootView.findViewById(R.id.partyTitle);
        mPartyDescription = (EditText) rootView.findViewById(R.id.partyDescription);
        // mMusicGeneration = (EditText) rootView.findViewById(R.id.musicGenerationDesc);
        mAgeSpinner = (Spinner) rootView.findViewById(R.id.Agespinner);
        mPartyAddress = (TextView) rootView.findViewById(R.id.partyAddressTextView);
        mRemoveImg = (Button) rootView.findViewById(R.id.removeImage);
        mCancelPartyBtn = (Button) rootView.findViewById(R.id.cancelParty);
        mPartyDatePicker = (EditText) rootView.findViewById(R.id.partyDatePicker);
        mPartyTimePicker = (EditText) rootView.findViewById(R.id.partyTimePicker);

        mPartyDatePicker.setInputType(InputType.TYPE_NULL);
        mPartyTimePicker.setInputType(InputType.TYPE_NULL);

        mPartyDatePicker.setInputType(InputType.TYPE_NULL);
        mPartyTimePicker.setInputType(InputType.TYPE_NULL);

        mCalendar = Calendar.getInstance();

        dialog = new ProgressDialog(getContext()); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        List<String> mGenre = new ArrayList<>();
        mGenre.add(getResources().getString(R.string.str_music_validation));
        mGenre.add(getResources().getString(R.string.str_music_alter));
        mGenre.add(getResources().getString(R.string.str_music_blues));
        mGenre.add(getResources().getString(R.string.str_music_classical));
        mGenre.add(getResources().getString(R.string.str_music_country));
        mGenre.add(getResources().getString(R.string.str_music_dance));
        mGenre.add(getResources().getString(R.string.str_music_easy));
        mGenre.add(getResources().getString(R.string.str_music_electronic));
        mGenre.add(getResources().getString(R.string.str_music_house));
        mGenre.add(getResources().getString(R.string.str_music_hip_hop));
        mGenre.add(getResources().getString(R.string.str_music_indie));
        mGenre.add(getResources().getString(R.string.str_music_jazz));
        mGenre.add(getResources().getString(R.string.str_music_latin));
        mGenre.add(getResources().getString(R.string.str_music_new_age));
        mGenre.add(getResources().getString(R.string.str_music_opera));
        mGenre.add(getResources().getString(R.string.str_music_pop));
        mGenre.add(getResources().getString(R.string.str_music_r_n_b));
        mGenre.add(getResources().getString(R.string.str_music_reggae));
        mGenre.add(getResources().getString(R.string.str_music_rock));
        mGenre.add(getResources().getString(R.string.str_music_techno));
        mGenre.add(getResources().getString(R.string.str_music_beat));
        ArrayAdapter<String> musicGenreAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item, mGenre);
        musicGenreAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        mMusicGenre.setAdapter(musicGenreAdapter);
        if (!TextUtils.isEmpty(partyDataDTO.getMusic())) {
            spnMusicGnenr = partyDataDTO.getMusic();
            if (!spnMusicGnenr.equals(null)) {
                //  int spineerPosition = musicGenreAdapter.getPosition(spnMusicGnenr);
                int spineerPosition = ((ArrayAdapter<String>) mMusicGenre.getAdapter()).getPosition(spnMusicGnenr);
                mMusicGenre.setSelection(spineerPosition);
            }

        }

        List<String> spineerData = new ArrayList<>();
        spineerData.add("--Please select Age--");
        spineerData.add("18+");
        spineerData.add("25+");
        spineerData.add("35+");
        spineerData.add("45+");
        spineerData.add("55+");
        spineerData.add("65+");
        /*mServerSyncManager.setOnStringErrorReceived(this);
        mServerSyncManager.setOnStringResultReceived(this);*/
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item, spineerData);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(dataAdapter);
        if (TextUtils.isEmpty(partyDataDTO.getAge())) {
            int spineerPosition = dataAdapter.getPosition(partyDataDTO.getAge());
            mSpinner.setSelection(spineerPosition);
        }

        if (partyDataDTO != null) {
            mPartyTitle.setText("" + partyDataDTO.getTitle());
            mPartyDescription.setText("" + partyDataDTO.getDesc());
            mGoogleMapTextView.setText("" + partyDataDTO.getLocation());
            String date = partyDataDTO.getDateOfParty();
            String onlyDate = dateUtils.convertOnlyDate(date);
            String onlyTime = dateUtils.convertOnlyTime(date);
            long test = partyDataDTO.getCreatedDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM", Locale.US);
            try {
                Date dateTest = dateFormat.parse(date);
                int dat = dateTest.getDate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(partyDataDTO.getImage())) {
                DownloadImage downloadImage = new DownloadImage();
                downloadImage.execute(partyDataDTO.getImage());
            }
            // partyDate(partyDataDTO.getDateOfParty());


        }
        mMusicGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinner.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                boolean result = callToValidation();
                if (result == true) {
                    if (NetworkUtils.isActiveNetworkAvailable(getContext())) {
                        dialog.show();
                        //callTToWebService();
                    } else if (!NetworkUtils.isActiveNetworkAvailable(getContext())) {
                        createAlertDialog("AftersApp", "Internet connection is not available");
                    }
                }

            }
        });
        mCancelPartyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, homeFragment, "Cancel_Edit_party").commit();
            }
        });

        /*Google Map Text view click event */
        mGoogleMapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    } else {
                        if (NetworkUtils.isActiveNetworkAvailable(getContext())) {
                            showTakeawayDialog(savedInstanceState);
                        } else if (!NetworkUtils.isActiveNetworkAvailable(getContext())) {
                            createAlertDialog("AftersApp", "Internet connection is not available");
                        }
                    }

                }

            }
        });

        return rootView;
    }


    private boolean callToValidation() {
        if (mPartyTitle.getText().toString().trim().length() == 0) {
            mPartyTitle.requestFocus();
            mPartyTitle.setError("Please enter party title");
            return false;
        } else if (mPartyDescription.getText().toString().trim().length() == 0) {
            mPartyDescription.requestFocus();
            mPartyDescription.setError("Please enter party description");

            return false;
        } else if (mMusicGenre.getSelectedItemPosition() == 0) {
            createAlertDialog("AftersApp", "Please select Music genre");

            return false;
        } else if (mAgeSpinner.getSelectedItemPosition() == 0) {
            createAlertDialog("AftersApp", "Please select Age Limit");

            return false;
        } else if (addressFlag != true) {
            mPartyAddress.setError("Please click here to get address");

            return false;
        } else if (TextUtils.isEmpty(mPartyDatePicker.getText().toString().trim())) {
            mPartyDatePicker.setError("Please select Party date");

            return false;
        } else if (TextUtils.isEmpty(mPartyTimePicker.getText().toString().trim())) {
            mPartyTimePicker.setError("Please select Party time");

            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (mLastLocation != null) {
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lon = String.valueOf(mLastLocation.getLongitude());
            GpsLatitude = Double.parseDouble(lat);
            GpsLongitude = Double.parseDouble(lon);

        } else {
            final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }
            //buildAlertMessageNoGps();
        }
        //updateUI();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("TAG", "## ");
        Log.d("TAG", "## ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    synchronized private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        callToConnect();
    }

    private void callToConnect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Log.d("TAG", "TAG");
    }

    /*Dialog activity is here*/

    private void showTakeawayDialog(Bundle savedInstanceState) {
        double temp1 = GpsLatitude;
        double temp2 = GpsLongitude;
        mFinalLongitude = GpsLongitude;
        mFinalLatititude = GpsLatitude;

        if (GpsLatitude == 0.0 || GpsLongitude == 0.0) {
            createAlertDialog("AfterApp", "Cannot able to find location");
            buildGoogleApiClient();
        } else {
            final Dialog dlg = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            View view = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_google_map, null);
            dlg.setContentView(view);
            addressFlag = false;
            dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            mSearchBtn = (Button) dlg.findViewById(R.id.searchBtn);
            mMapView = (MapView) dlg.findViewById(R.id.mapViewParty);
            mapOkBtn = (Button) dlg.findViewById(R.id.savePartyMap);
            mapCancelBtn = (Button) dlg.findViewById(R.id.cancelPartyMap);
            mSearchEditText = (EditText) dlg.findViewById(R.id.searchAddress);
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
                    try {
                        mGoogleMap.setMyLocationEnabled(true);
                    } catch (SecurityException e) {

                    }

                    LatLng selectedLocation = new LatLng(GpsLatitude, GpsLongitude);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(selectedLocation).zoom(13).build();
                    mGoogleMap.clear();
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                    String completeAddress = "";
                    try {

                        addresses = geocoder.getFromLocation(GpsLatitude, GpsLongitude, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        int addressLine = addresses.get(0).getMaxAddressLineIndex();

                        for (int i = 0; i <= addressLine; i++) {

                            String address = addresses.get(0).getAddressLine(i);
                            completeAddress = completeAddress + "\t" + address + "\t";
                            Log.d("TAG", "TAG");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(selectedLocation).title(completeAddress).draggable(false));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {

                            double lat = latLng.latitude;
                            double log = latLng.longitude;
                            String mSendAddress = "";
                            mGoogleMap.clear();

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getContext(), Locale.getDefault());

                            try {

                                addresses = geocoder.getFromLocation(lat, log, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                int addressLine = addresses.get(0).getMaxAddressLineIndex();
                                String completeAddress = "";
                                for (int i = 0; i <= addressLine; i++) {

                                    String address = addresses.get(0).getAddressLine(i);
                                    completeAddress = completeAddress + "\t" + address;
                                    Log.d("TAG", "TAG");
                                }
                                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(completeAddress).draggable(false));

                                if (lat != 0.0 || log != 0.0) {
                                    if (!completeAddress.equals("")) {
                                        setResult(completeAddress, lat, log);
                                    }
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                    mSearchBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mSearchEditText.getText().toString())) {
                                String completeAddress = "";
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(),
                                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                String userAddress = mSearchEditText.getText().toString();
                                Geocoder coder = new Geocoder(getContext());
                                try {
                                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(userAddress, 15);
                                    String mSendAddress = "";
                                    double sendLatitude = 0.0;
                                    double sendLongitude = 0.0;
                                    for (Address add : adresses) {
                                        // if (statement) {//Controls to ensure it is right address such as country etc.
                                        double longitude = add.getLongitude();
                                        double latitude = add.getLatitude();
                                        sendLatitude = latitude;
                                        sendLongitude = longitude;
                                        LatLng DemoLatLong = new LatLng(latitude, longitude);
                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(DemoLatLong).zoom(12).build();
                                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                        mGoogleMap.clear();
                                        Geocoder geocoder;
                                        List<Address> addresses;
                                        geocoder = new Geocoder(getContext(), Locale.getDefault());
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                        int addressLine = add.getMaxAddressLineIndex();

                                        for (int i = 0; i <= addressLine; i++) {

                                            String address = add.getAddressLine(i);
                                            completeAddress = completeAddress + "\t" + address;
                                            Log.d("TAG", "TAG");
                                        }

                                        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(DemoLatLong).title("" + completeAddress));


                                    }
                                    if (sendLongitude != 0.0 || sendLongitude != 0.0) {
                                        if (!completeAddress.equals("")) {
                                            setResult(completeAddress, sendLatitude, sendLongitude);
                                        }
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }

            });
            mapCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addressFlag = false;
                    setResult(0.0, 0.0);
                    dlg.dismiss();
                    //

                }
            });
            mapOkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // if (!TextUtils.isEmpty(mFinalAddress)) {
                    if (mFinalLatititude != 0.0 || mFinalLongitude != 0.0) {
                        addressFlag = true;
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {

                            addresses = geocoder.getFromLocation(mFinalLatititude, mFinalLongitude, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            int addressLine = addresses.get(0).getMaxAddressLineIndex();
                            String completeAddress = "";
                            for (int i = 0; i <= addressLine; i++) {

                                String address = addresses.get(0).getAddressLine(i);
                                completeAddress = completeAddress + "\t" + address;
                                Log.d("TAG", "TAG");
                            }
                            if (mFinalLatititude != 0.0 || mFinalLongitude != 0.0) {
                                if (!completeAddress.equals("")) {
                                    setResult(completeAddress, mFinalLatititude, mFinalLongitude);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                        dlg.dismiss();
                    }
                    // }


                }
            });

            dlg.show();


        }
    }

    public void setResult(String address, double latitude, double longitude) {

        String trimString = address.trim();
        mGoogleMapTextView.setText("" + trimString);
        mFinalAddress = trimString;
        mFinalLatititude = latitude;
        mFinalLongitude = longitude;
    }

    public void setResult(double latitude, double longitude) {



        /*mFinalAddress = address;*/
        mGoogleMapTextView.setText("");
        mGoogleMapTextView.setHint("Click here for party address");
        mFinalLatititude = latitude;
        mFinalLongitude = longitude;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    private void onRequestGpsPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                EDIT_LOCATION_PERMISSION_CODE);
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
                    mImageUri = imgDecodableString.toString();
                    mUserPartyPhoto.setImageBitmap(mBitmapString);
                    mUserPartyPhoto.setTag("ImageSet");
                    mRemoveImg.setVisibility(View.VISIBLE);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //  Bitmap convertedImg = null;
                    options.inJustDecodeBounds = true;
                    convertedImg = BitmapFactory.decodeFile(mImageUri, options);
                    options.inSampleSize = calculateInSampleSize(options, 250, 250);
                    options.inJustDecodeBounds = false;
                    convertedImg = BitmapFactory.decodeFile(mImageUri, options);
                    System.gc();
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

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = null;
        try {
            System.gc();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageBytes = baos.toByteArray();

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.d("TAG", "## ");
        }

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }


    /*Down load image class*/
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(input);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mUserPartyPhoto.setImageBitmap(result);
            mUserPartyPhoto.setTag("ImageSet");
        }
    }

    private void requestGrantPermission() {

        requestPermissions(new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                EDIT_PROFILE_MEDIA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EDIT_PROFILE_MEDIA_PERMISSION_CODE && grantResults[1] == 0) {
            openGallery();
        }
        if (requestCode == EDIT_LOCATION_PERMISSION_CODE && grantResults[0] == 0) {
            buildGoogleApiClient();
        } else if (requestCode == EDIT_PROFILE_MEDIA_PERMISSION_CODE && grantResults[1] != 0 || requestCode == EDIT_LOCATION_PERMISSION_CODE && grantResults[0] != 0) {
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

    public void partyDate(long date) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date currenTimeZone = new java.util.Date(date * 1000);
        Toast.makeText(getContext(), sdf.format(currenTimeZone), Toast.LENGTH_SHORT).show();
    }
}
