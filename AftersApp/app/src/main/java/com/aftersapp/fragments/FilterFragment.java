package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aftersapp.R;

/**
 * Created by akshay on 19-09-2016.
 */
public class FilterFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout layRadius, seekBarLay, layAge, seekBarAgeLay;
    private ImageView imgArrow, imgAgeArrow;

    //private
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
        layRadius = (LinearLayout) rootView.findViewById(R.id.layRadius);
        seekBarLay = (LinearLayout) rootView.findViewById(R.id.seekBarLay);
        imgArrow = (ImageView) rootView.findViewById(R.id.imgArrow);

        layAge = (LinearLayout) rootView.findViewById(R.id.layAge);
        seekBarAgeLay = (LinearLayout) rootView.findViewById(R.id.seekBarAgeLay);
        imgAgeArrow = (ImageView) rootView.findViewById(R.id.imgAgeArrow);

        layRadius.setOnClickListener(this);
        layAge.setOnClickListener(this);
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
        }
    }
}
