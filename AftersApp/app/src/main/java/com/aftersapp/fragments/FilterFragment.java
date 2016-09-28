package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.R;
import com.aftersapp.adapters.AgeSpinnerAdapter;
import com.aftersapp.data.AgeData;
import com.aftersapp.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay on 19-09-2016.
 */
public class FilterFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout layRadius, seekBarLay, layAge, seekBarAgeLay, musicGeneration, musicLayout;
    private ImageView imgArrow, imgAgeArrow, imgMusciGener;
    private SeekBar mSeekBar;
    private int mSeekBarStep = 1, mSeekBarMax, mSeekBarMin;
    private TextView mFilterVal, mFilterAge, mSelectMusicSelect;
    private EditText mUserMusicGenre;
    private Spinner mSpnAge;
    private AgeSpinnerAdapter ageSpinnerAdapter;
    private int selectedAge = AppConstants.DEFAULT_AGE_VALUE;
    private int selectedRadius = AppConstants.DEFAULT_RADIUS_VALUE;
    private String musicGenre = "";
    private Button btnShowResult, btnResetFilter;

    //private
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedAge = mSessionManager.getAge();
        selectedRadius = mSessionManager.getRadius();
        musicGenre = mSessionManager.getMusicGenre();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
        mSeekBarMax = AppConstants.MAX_RADIUS_VALUE;
        mSeekBarMin = AppConstants.MIN_RADIUS_VALUE;
        layRadius = (LinearLayout) rootView.findViewById(R.id.layRadius);
        seekBarLay = (LinearLayout) rootView.findViewById(R.id.seekBarLay);
        imgArrow = (ImageView) rootView.findViewById(R.id.imgArrow);
        mFilterVal = (TextView) rootView.findViewById(R.id.filterVal);
        layAge = (LinearLayout) rootView.findViewById(R.id.layAge);
        seekBarAgeLay = (LinearLayout) rootView.findViewById(R.id.seekBarAgeLay);
        imgAgeArrow = (ImageView) rootView.findViewById(R.id.imgAgeArrow);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        musicGeneration = (LinearLayout) rootView.findViewById(R.id.musciGenerAge);
        musicLayout = (LinearLayout) rootView.findViewById(R.id.seekBarAgeLayMusic);
        imgMusciGener = (ImageView) rootView.findViewById(R.id.musciAgeArrow);
        mFilterAge = (TextView) rootView.findViewById(R.id.defaultAge);
        mSelectMusicSelect = (TextView) rootView.findViewById(R.id.editMusicSetText);
        mUserMusicGenre = (EditText) rootView.findViewById(R.id.MusicGenreEdit);
        mSpnAge = (Spinner) rootView.findViewById(R.id.spnAge);
        btnShowResult = (Button) rootView.findViewById(R.id.btnShowResult);
        btnResetFilter = (Button) rootView.findViewById(R.id.btnResetAllFilters);

        setSpinner();
        layRadius.setOnClickListener(this);
        layAge.setOnClickListener(this);
        btnShowResult.setOnClickListener(this);
        btnResetFilter.setOnClickListener(this);
        musicGeneration.setOnClickListener(this);
        mSeekBar.setMax((mSeekBarMax - mSeekBarMin) / mSeekBarStep);
        setResetValues();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedRadius = mSeekBarMin + (progress * mSeekBarStep);
                mFilterVal.setText("" + selectedRadius + "-" + getResources().getString(R.string.raduis_selected));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSpnAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AgeData ageData = (AgeData) ageSpinnerAdapter.getItem(position);
                selectedAge = ageData.getValue();
                mFilterAge.setText("" + selectedAge + "+ age selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mUserMusicGenre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()))
                    mSelectMusicSelect.setText(s.toString() + " Music Selected");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    private void setResetValues() {
        mSeekBar.setProgress(selectedRadius);
        mFilterVal.setText("" + selectedRadius + "-" + getResources().getString(R.string.raduis_selected));
        if (selectedAge != 0)
            mFilterAge.setText("" + selectedAge + "+ age selected");
        if (!TextUtils.isEmpty(musicGenre))
            mSelectMusicSelect.setText(musicGenre + " Music Selected");
    }

    private void setSpinner() {
        List<AgeData> ageDataList = new ArrayList<>();
        ageDataList.add(new AgeData("10+", 10));
        ageDataList.add(new AgeData("20+", 20));
        ageDataList.add(new AgeData("30+", 30));
        ageDataList.add(new AgeData("40+", 40));
        ageDataList.add(new AgeData("50+", 50));
        ageDataList.add(new AgeData("60+", 60));
        ageSpinnerAdapter = new AgeSpinnerAdapter(getContext(), ageDataList);
        mSpnAge.setAdapter(ageSpinnerAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layRadius:
                if (seekBarLay.getVisibility() == View.GONE) {
                    seekBarLay.setVisibility(View.VISIBLE);
                    imgArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else if (seekBarLay.getVisibility() == View.VISIBLE) {
                    seekBarLay.setVisibility(View.GONE);
                    imgArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
                break;
            case R.id.layAge:
                if (seekBarAgeLay.getVisibility() == View.GONE) {
                    seekBarAgeLay.setVisibility(View.VISIBLE);
                    imgAgeArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else if (seekBarAgeLay.getVisibility() == View.VISIBLE) {
                    seekBarAgeLay.setVisibility(View.GONE);
                    imgAgeArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
                break;
            case R.id.musciGenerAge:
                if (musicLayout.getVisibility() == View.GONE) {
                    musicLayout.setVisibility(View.VISIBLE);
                    imgMusciGener.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else if (musicLayout.getVisibility() == View.VISIBLE) {
                    musicLayout.setVisibility(View.GONE);
                    imgMusciGener.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
                break;
            case R.id.btnShowResult: {
                musicGenre = mUserMusicGenre.getText().toString();
                mSessionManager.setMusicGenre(musicGenre);
                mSessionManager.setRadius(selectedRadius);
                mSessionManager.setAge(selectedAge);
                FindPartyFragment findPartyFragment = new FindPartyFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, findPartyFragment, "FindPArty").commit();
                Toast.makeText(getContext(), getResources().getString(R.string.str_filter_apply), Toast.LENGTH_SHORT).show();
            }

            break;
            case R.id.btnResetAllFilters: {
                mSessionManager.setMusicGenre(null);
                mSessionManager.setRadius(AppConstants.DEFAULT_RADIUS_VALUE);
                mSessionManager.setAge(AppConstants.DEFAULT_AGE_VALUE);
                FindPartyFragment findPartyFragment = new FindPartyFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, findPartyFragment, "FindPArty").commit();
                Toast.makeText(getContext(), getResources().getString(R.string.str_filter_reset), Toast.LENGTH_SHORT).show();
            }


            break;
        }
    }
}
