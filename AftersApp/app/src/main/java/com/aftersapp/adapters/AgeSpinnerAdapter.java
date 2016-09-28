package com.aftersapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aftersapp.R;
import com.aftersapp.data.AgeData;

import java.util.List;

/**
 * Created by akshay on 28-09-2016.
 */
public class AgeSpinnerAdapter extends BaseAdapter {
    private Context mContext;
    List<AgeData> ageDatas;

    public AgeSpinnerAdapter(Context mContext, List<AgeData> ageDatas) {
        this.mContext = mContext;
        this.ageDatas = ageDatas;
    }

    @Override
    public int getCount() {
        return ageDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return ageDatas.get(position);
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

            LayoutInflater theLayoutInflater = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = theLayoutInflater.inflate(R.layout.age_spinner_child_element, null);
            viewHolder = new ViewHolder();
            viewHolder.spinnerChild = (TextView) row.findViewById(R.id.txtSpinnerChild);
            row.setTag(viewHolder);

        } else
            viewHolder = (ViewHolder) convertView.getTag();
        AgeData ageData = ageDatas.get(position);
        viewHolder.spinnerChild.setText(ageData.getName());

        return row;
    }

    private class ViewHolder {
        TextView spinnerChild;
    }
   /* public void addItem(final TypeDataDTO item) {
        mCategories.add(item);
        notifyDataSetChanged();
    }

    public void clear() {
        mCategories.clear();
    }*/
}