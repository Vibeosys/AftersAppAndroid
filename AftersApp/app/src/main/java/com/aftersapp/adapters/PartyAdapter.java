package com.aftersapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aftersapp.R;
import com.aftersapp.data.PartyData;
import com.aftersapp.data.responsedata.PartyResponseDTO;
import com.aftersapp.utils.CustomVolleyRequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by akshay on 16-09-2016.
 */
public class PartyAdapter extends BaseAdapter {

    private ArrayList<PartyResponseDTO> mData;
    private Context mContext;
    private ImageLoader mImageLoader;

    public PartyAdapter(ArrayList<PartyResponseDTO> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder = null;
        if (row == null) {

            LayoutInflater theLayoutInflator = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = theLayoutInflator.inflate(R.layout.row_party_list, null);
            viewHolder = new ViewHolder();
            viewHolder.txtPartyName = (TextView) row.findViewById(R.id.txtPartyName);
            viewHolder.txtPartyDesc = (TextView) row.findViewById(R.id.txtDesc);
            viewHolder.txtAgeLimit = (TextView) row.findViewById(R.id.txtAgeLimit);
            viewHolder.txtAttending = (TextView) row.findViewById(R.id.txtAttending);
            viewHolder.networkImageView = (NetworkImageView) row.findViewById(R.id.imgPartyImage);
            row.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();
        PartyResponseDTO partyData = mData.get(position);
        String partyName = partyData.getTitle();
        String partyDesc = partyData.getDesc();
        String age = partyData.getAge();
        int attendance = partyData.getAttending();

        viewHolder.txtPartyName.setText(partyName);
        viewHolder.txtPartyDesc.setText(partyDesc);
        viewHolder.txtAgeLimit.setText(age);
        viewHolder.txtAttending.setText(attendance + "+");
        mImageLoader = CustomVolleyRequestQueue.getInstance(mContext)
                .getImageLoader();
        final String url = partyData.getImage();
        if (url != null && !url.isEmpty()) {
            try {
                mImageLoader.get(url, ImageLoader.getImageListener(viewHolder.networkImageView,
                        R.drawable.party1, R.drawable.party1));
                viewHolder.networkImageView.setImageUrl(url, mImageLoader);
            } catch (Exception e) {
                viewHolder.networkImageView.setImageResource(R.drawable.party1);
            }
        } else {
            viewHolder.networkImageView.setImageResource(R.drawable.party1);
        }
        return row;
    }

    private class ViewHolder {
        TextView txtPartyName, txtPartyDesc, txtAgeLimit, txtAttending;
        NetworkImageView networkImageView;
    }
}
