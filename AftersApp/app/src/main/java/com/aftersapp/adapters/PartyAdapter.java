package com.aftersapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aftersapp.R;
import com.aftersapp.data.PartyDataDTO;
import com.aftersapp.utils.AppConstants;
import com.aftersapp.utils.CustomVolleyRequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by akshay on 16-09-2016.
 */
public class PartyAdapter extends BaseAdapter {

    private ArrayList<PartyDataDTO> mData;
    private Context mContext;
    private ImageLoader mImageLoader;
    private OnLikeOrFavClick likeOrFavClick;

    public PartyAdapter(ArrayList<PartyDataDTO> mData, Context mContext) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            viewHolder.imgLike = (ImageView) row.findViewById(R.id.imgLike);
            viewHolder.imgFav = (ImageView) row.findViewById(R.id.imgFav);
            viewHolder.layoutDetails = (LinearLayout) row.findViewById(R.id.linearDetails);
            row.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();
        final PartyDataDTO partyData = mData.get(position);
        String partyName = partyData.getTitle();
        String partyDesc = partyData.getDesc();
        String age = partyData.getAge();
        int attendance = partyData.getAttending();
        int isFav = partyData.getIsFavourite();
        int isLike = partyData.getIsLike();
        viewHolder.txtPartyName.setText(partyName);
        viewHolder.txtPartyDesc.setText(partyDesc);
        viewHolder.txtAgeLimit.setText(""+age);
        viewHolder.txtAttending.setText(""+attendance);
        mImageLoader = CustomVolleyRequestQueue.getInstance(mContext)
                .getImageLoader();

        if (isFav == AppConstants.FAV_PARTY) {
            viewHolder.imgFav.setImageResource(R.drawable.heart_colour);
        } else if (isFav == AppConstants.NOT_FAV_PARTY) {
            viewHolder.imgFav.setImageResource(R.drawable.not_favouirte);
        }

        if (isLike == AppConstants.ATTENDING_PARTY) {
            viewHolder.imgLike.setImageResource(R.drawable.thumb_color);
        } else if (isLike == AppConstants.NOT_ATTENDING_PARTY) {
            viewHolder.imgLike.setImageResource(R.drawable.party_not_attending);
        }

        final String url = partyData.getImage();
        if (url == "null" || url.isEmpty() || url == null || url.equals("") || url == "") {
            viewHolder.networkImageView.setDefaultImageResId(R.drawable.default_party_image);
        } else if (TextUtils.isEmpty(url)) {
            viewHolder.networkImageView.setImageResource(R.drawable.default_party_image);
        } else if (url != null && !url.isEmpty()) {
            try {
                mImageLoader.get(url, ImageLoader.getImageListener(viewHolder.networkImageView,
                        R.drawable.default_party_image, R.drawable.default_party_image));
                viewHolder.networkImageView.setImageUrl(url, mImageLoader);
            } catch (Exception e) {
                viewHolder.networkImageView.setImageResource(R.drawable.default_party_image);
            }
        } else {
            viewHolder.networkImageView.setImageResource(R.drawable.default_party_image);
        }
        viewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeOrFavClick != null) {
                    likeOrFavClick.onLikeClickListener(partyData, position, partyData.getIsLike());
                }
            }
        });
        viewHolder.imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeOrFavClick != null) {
                    likeOrFavClick.onFavClickListener(partyData, position, partyData.getIsFavourite());
                }
            }
        });
        viewHolder.layoutDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeOrFavClick != null) {
                    likeOrFavClick.onItemClickListener(partyData, position);
                }
            }
        });

        viewHolder.networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeOrFavClick != null) {
                    likeOrFavClick.onImageClickListener(partyData, position);
                }
            }
        });
        return row;
    }

    private class ViewHolder {
        TextView txtPartyName, txtPartyDesc, txtAgeLimit, txtAttending;
        NetworkImageView networkImageView;
        ImageView imgLike, imgFav;
        LinearLayout layoutDetails;
    }

    public void setLikeOrFavClick(OnLikeOrFavClick listener) {
        this.likeOrFavClick = listener;
    }

    public interface OnLikeOrFavClick {
        public void onLikeClickListener(PartyDataDTO partyDataDTO, int position, int value);

        public void onFavClickListener(PartyDataDTO partyDataDTO, int position, int value);

        public void onItemClickListener(PartyDataDTO partyDataDTO, int position);

        public void onImageClickListener(PartyDataDTO partyDataDTO, int position);
    }
}
