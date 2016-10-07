package com.aftersapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.aftersapp.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by akshay on 19-09-2016.
 */
public class FilterFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout layRadius, seekBarLay, layAge, seekBarAgeLay, musicGeneration,
            musicLayout, layDate, innerDateLay;
    private ImageView imgArrow, imgAgeArrow, imgMusciGener, imgDateArrow, imgCalender, imgCalenderClear;
    private SeekBar mSeekBar;
    private int mSeekBarStep = 1, mSeekBarMax, mSeekBarMin;
    private TextView mFilterVal, mFilterAge, mSelectMusicSelect, filterDateVal;
    private Spinner mSpnAge, mMusicGenre;
    private AgeSpinnerAdapter ageSpinnerAdapter;
    private ArrayAdapter<String> musicGenreAdapter;
    private int selectedAge = AppConstants.DEFAULT_AGE_VALUE;
    private int selectedRadius = AppConstants.DEFAULT_RADIUS_VALUE;
    private String musicGenre = "";
    private Button btnShowResult, btnResetFilter;
    private EditText mTxtDateSelected;
    private Calendar mFilterCalender = Calendar.getInstance();
    private DateUtils dateUtils = new DateUtils();
    private String formattedDate = "";

    //private
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAllValues();
        mFilterCalender.set(Calendar.DAY_OF_MONTH, mFilterCalender.get(Calendar.DAY_OF_MONTH));
    }

    private void loadAllValues() {
        selectedAge = mSessionManager.getAge();
        selectedRadius = mSessionManager.getRadius();
        musicGenre = mSessionManager.getMusicGenre();
        formattedDate = mSessionManager.getFilteredDate();
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
        layDate = (LinearLayout) rootView.findViewById(R.id.layDate);
        innerDateLay = (LinearLayout) rootView.findViewById(R.id.dateLay);
        seekBarAgeLay = (LinearLayout) rootView.findViewById(R.id.seekBarAgeLay);
        imgAgeArrow = (ImageView) rootView.findViewById(R.id.imgAgeArrow);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        musicGeneration = (LinearLayout) rootView.findViewById(R.id.musciGenerAge);
        musicLayout = (LinearLayout) rootView.findViewById(R.id.seekBarAgeLayMusic);
        imgMusciGener = (ImageView) rootView.findViewById(R.id.musciAgeArrow);
        imgDateArrow = (ImageView) rootView.findViewById(R.id.imgDateArrow);
        imgCalender = (ImageView) rootView.findViewById(R.id.imgCalender);
        imgCalenderClear = (ImageView) rootView.findViewById(R.id.imgCalenderClear);
        mFilterAge = (TextView) rootView.findViewById(R.id.defaultAge);
        filterDateVal = (TextView) rootView.findViewById(R.id.filterDateVal);
        mSelectMusicSelect = (TextView) rootView.findViewById(R.id.editMusicSetText);
        mSpnAge = (Spinner) rootView.findViewById(R.id.spnAge);
        mMusicGenre = (Spinner) rootView.findViewById(R.id.musicGenre);
        btnShowResult = (Button) rootView.findViewById(R.id.btnShowResult);
        btnResetFilter = (Button) rootView.findViewById(R.id.btnResetAllFilters);
        mTxtDateSelected = (EditText) rootView.findViewById(R.id.txtDateSelected);

        setAgeSpinner();
        setMusicSpinner();
        layRadius.setOnClickListener(this);
        layAge.setOnClickListener(this);
        layDate.setOnClickListener(this);
        btnShowResult.setOnClickListener(this);
        btnResetFilter.setOnClickListener(this);
        musicGeneration.setOnClickListener(this);
        imgCalender.setOnClickListener(this);
        imgCalenderClear.setOnClickListener(this);
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
        mMusicGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    musicGenre = "";
                    mSelectMusicSelect.setText("No music Selected");
                } else {
                    musicGenre = musicGenreAdapter.getItem(position);
                    mSelectMusicSelect.setText(musicGenre + " music Selected");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    private void setMusicSpinner() {
        List<String> mGenre = new ArrayList<>();
        mGenre.add(getResources().getString(R.string.str_no_music_selected));
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
        musicGenreAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, mGenre);
        musicGenreAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        mMusicGenre.setAdapter(musicGenreAdapter);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mFilterCalender.set(Calendar.YEAR, year);
            mFilterCalender.set(Calendar.MONTH, monthOfYear);
            mFilterCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        formattedDate = dateUtils.getSwedishOnlyDateFormat(mFilterCalender.getTime());
        mTxtDateSelected.setText(formattedDate);
        mSessionManager.setFilterDate(formattedDate);
        filterDateVal.setText(formattedDate + " selected");
    }

    private void setResetValues() {
        mSeekBar.setProgress(selectedRadius);
        mFilterVal.setText("" + selectedRadius + "-" + getResources().getString(R.string.raduis_selected));
        if (selectedAge != 0)
            mFilterAge.setText("" + selectedAge + "+ age selected");
        else {
            mFilterAge.setText("Age not selected");
        }
        if (!TextUtils.isEmpty(musicGenre))
            mSelectMusicSelect.setText(musicGenre + " music Selected");
        else {
            mSelectMusicSelect.setText("No music Selected");
        }
        if (!TextUtils.isEmpty(formattedDate))
            filterDateVal.setText(formattedDate + " Selected");
        else {
            filterDateVal.setText("Date not Selected");
        }
    }

    private void setAgeSpinner() {
        List<AgeData> ageDataList = new ArrayList<>();
        ageDataList.add(new AgeData("select Age", 0));
        ageDataList.add(new AgeData("18+", 18));
        ageDataList.add(new AgeData("25+", 25));
        ageDataList.add(new AgeData("35+", 35));
        ageDataList.add(new AgeData("45+", 45));
        ageDataList.add(new AgeData("55+", 55));
        ageDataList.add(new AgeData("65+", 65));
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
            case R.id.layDate:
                if (innerDateLay.getVisibility() == View.GONE) {
                    innerDateLay.setVisibility(View.VISIBLE);
                    imgDateArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else if (innerDateLay.getVisibility() == View.VISIBLE) {
                    innerDateLay.setVisibility(View.GONE);
                    imgDateArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
                break;
            case R.id.btnShowResult: {
                mSessionManager.setMusicGenre(musicGenre);
                mSessionManager.setRadius(selectedRadius);
                mSessionManager.setAge(selectedAge);
                FindPartyFragment findPartyFragment = new AllPartiesFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, findPartyFragment, "FindPArty").commit();
                Toast.makeText(getContext(), getResources().getString(R.string.str_filter_apply), Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.imgCalender: {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), date, mFilterCalender
                        .get(Calendar.YEAR), mFilterCalender.get(Calendar.MONTH),
                        mFilterCalender.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(mFilterCalender.getTimeInMillis());
                dialog.show();
            }
            break;
            case R.id.imgCalenderClear: {
                mSessionManager.setFilterDate(null);
                filterDateVal.setText("Date not Selected");
                mTxtDateSelected.setText("");
            }

            break;
            case R.id.btnResetAllFilters: {
                mSessionManager.setMusicGenre(null);
                mSessionManager.setFilterDate(null);
                mSessionManager.setRadius(AppConstants.DEFAULT_RADIUS_VALUE);
                mSessionManager.setAge(AppConstants.DEFAULT_AGE_VALUE);
                loadAllValues();
                setResetValues();
                FindPartyFragment findPartyFragment = new AllPartiesFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, findPartyFragment, "FindPArty").commit();
                Toast.makeText(getContext(), getResources().getString(R.string.str_filter_reset), Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }
}
