package com.aftersapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aftersapp.R;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by akshay on 23-09-2016.
 */
public class UserListAdapter extends BaseListAdapter<QBUser> {

    private Context mContext;
    private List<QBUser> qbUsersList;

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

            row.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();
        QBUser qbUser = getItem(position);
        viewHolder.txtName.setText(qbUser.getFullName());
        viewHolder.txtEmail.setText(qbUser.getEmail());
        return row;
    }

    public static class ViewHolder {
        TextView txtName, txtEmail;
    }
}
