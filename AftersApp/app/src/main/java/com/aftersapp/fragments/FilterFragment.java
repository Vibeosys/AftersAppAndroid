package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aftersapp.R;

import org.w3c.dom.Text;

/**
 * Created by akshay on 19-09-2016.
 */
public class FilterFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout layRadius, seekBarLay, layAge, seekBarAgeLay,musicGeneration,musicLayout;
    private ImageView imgArrow, imgAgeArrow,imgMusciGener;
    private SeekBar mSeekBar,mSeekBarAge;
    private int mSeekBarStep,mSeekBarMax,mSeekBarMin,mSeekBarAgeStep,mSeekBarAgeMax,mSeekBarAgeMin;
    private TextView mFilterVal,mFilterAge;
    private  int mSeekBarFlag;

    //private
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
        mSeekBarFlag = 0;
        mSeekBarStep =1;
        mSeekBarMax=20;
        mSeekBarMin=1;
        mSeekBarAgeStep =1;
        mSeekBarAgeMax = 60;
        mSeekBarAgeMin =10;
        layRadius = (LinearLayout) rootView.findViewById(R.id.layRadius);
        seekBarLay = (LinearLayout) rootView.findViewById(R.id.seekBarLay);
        imgArrow = (ImageView) rootView.findViewById(R.id.imgArrow);
        mFilterVal  =(TextView) rootView.findViewById(R.id.filterVal);

        layAge = (LinearLayout) rootView.findViewById(R.id.layAge);
        seekBarAgeLay = (LinearLayout) rootView.findViewById(R.id.seekBarAgeLay);
        imgAgeArrow = (ImageView) rootView.findViewById(R.id.imgAgeArrow);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar) ;
        musicGeneration = (LinearLayout) rootView.findViewById(R.id.musciGenerAge);
        musicLayout = (LinearLayout)rootView.findViewById(R.id.seekBarAgeLayMusic);
        imgMusciGener = (ImageView) rootView.findViewById(R.id.musciAgeArrow);
        mSeekBarAge =(SeekBar) rootView.findViewById(R.id.seekBarAge);
        mFilterAge =(TextView) rootView.findViewById(R.id.defaultAge);

        layRadius.setOnClickListener(this);
        layAge.setOnClickListener(this);
        musicGeneration.setOnClickListener(this);
        mSeekBar.setMax((mSeekBarMax - mSeekBarMin) / mSeekBarStep );
        mSeekBarAge.setMax((mSeekBarAgeMax - mSeekBarAgeMin) / mSeekBarAgeStep );
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = mSeekBarMin + (progress * mSeekBarStep);
                mFilterVal.setText("Default radius selected "+value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = mSeekBarAgeMin + (progress * mSeekBarAgeStep);
                mFilterAge.setText("Default age selected "+value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layRadius:
                if (seekBarLay.getVisibility() == View.GONE) {
                    seekBarLay.setVisibility(View.VISIBLE);
                    imgArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    mSeekBarFlag=1;
                } else if (seekBarLay.getVisibility() == View.VISIBLE) {
                    seekBarLay.setVisibility(View.GONE);
                    imgArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    mSeekBarFlag=0;
                }
                break;
            case R.id.layAge:
                if (seekBarAgeLay.getVisibility() == View.GONE) {
                    seekBarAgeLay.setVisibility(View.VISIBLE);
                    imgAgeArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    mSeekBarFlag=2;
                } else if (seekBarAgeLay.getVisibility() == View.VISIBLE) {
                    seekBarAgeLay.setVisibility(View.GONE);
                    imgAgeArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    mSeekBarFlag=0;
                }
                break;
                case R.id.musciGenerAge:
                if(musicLayout.getVisibility() == View.GONE)
                {
                    musicLayout.setVisibility(View.VISIBLE);
                    imgMusciGener.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }else if(musicLayout.getVisibility() == View.VISIBLE)
                {
                    musicLayout.setVisibility(View.GONE);
                    imgMusciGener.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
                break;
        }
    }
}
