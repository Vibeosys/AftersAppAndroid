package com.aftersapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aftersapp.R;
import com.aftersapp.utils.CustomVolleyRequestQueue;
import com.aftersapp.views.NetworkRoundImageView;
import com.android.volley.toolbox.ImageLoader;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by akshay on 23-09-2016.
 */
public class UserListAdapter extends BaseListAdapter<QBUser> {

    private Context mContext;
    private List<QBUser> qbUsersList;
    private ImageLoader mImageLoader;

    public UserListAdapter(Context context, List<QBUser> qbUsersList) {
        super(context, qbUsersList);
        this.mContext = context;
        this.qbUsersList = qbUsersList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder = null;
        if (row == null) {

            LayoutInflater theLayoutInflator = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = theLayoutInflator.inflate(R.layout.list_item_user, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) row.findViewById(R.id.user_name_text_item_view);
            viewHolder.txtEmail = (TextView) row.findViewById(R.id.full_name_text_item_view);
            viewHolder.profileImg = (NetworkRoundImageView) row.findViewById(R.id.profileImg);
            row.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();
        QBUser qbUser = getItem(position);
        viewHolder.txtName.setText(qbUser.getFullName());
        viewHolder.txtEmail.setText(qbUser.getEmail());
        mImageLoader = CustomVolleyRequestQueue.getInstance(mContext)
                .getImageLoader();
        try {
            final String url = qbUser.getCustomData();
            if (url == "null" || url.isEmpty() || url == null || url.equals("") || url == "") {
                viewHolder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
            } else if (TextUtils.isEmpty(url)) {
                viewHolder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
            } else if (url != null && !url.isEmpty()) {
                try {
                    mImageLoader.get(url, ImageLoader.getImageListener(viewHolder.profileImg,
                            R.drawable.avatar_profile, R.drawable.avatar_profile));
                    viewHolder.profileImg.setImageUrl(url, mImageLoader);
                } catch (Exception e) {
                    viewHolder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
                }
            } else {
                viewHolder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
            }
        } catch (NullPointerException e) {
            viewHolder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
        }

        return row;
    }

    public static class ViewHolder {
        TextView txtName, txtEmail;
        NetworkRoundImageView profileImg;
    }

}
