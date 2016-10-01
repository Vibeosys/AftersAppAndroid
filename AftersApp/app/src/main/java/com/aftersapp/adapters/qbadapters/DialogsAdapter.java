package com.aftersapp.adapters.qbadapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aftersapp.AftersAppApplication;
import com.aftersapp.R;
import com.aftersapp.utils.CustomVolleyRequestQueue;
import com.aftersapp.utils.qbutils.QbDialogUtils;
import com.aftersapp.utils.qbutils.UiUtils;
import com.aftersapp.views.NetworkRoundImageView;
import com.android.volley.toolbox.ImageLoader;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;

import java.util.List;

public class DialogsAdapter extends BaseSelectableListAdapter<QBDialog> {

    Context mContext;

    public DialogsAdapter(Context context, List<QBDialog> dialogs) {
        super(context, dialogs);
        this.mContext = context;
    }

    private ImageLoader mImageLoader;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_dialog, parent, false);

            holder = new ViewHolder();
            holder.rootLayout = (ViewGroup) convertView.findViewById(R.id.root);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.text_dialog_name);
            holder.lastMessageTextView = (TextView) convertView.findViewById(R.id.text_dialog_last_message);
            holder.profileImg = (NetworkRoundImageView) convertView.findViewById(R.id.image_dialog_icon);
            holder.unreadCounterTextView = (TextView) convertView.findViewById(R.id.text_dialog_unread_count);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QBDialog dialog = getItem(position);

        /*holder.dialogImageView.setBackgroundDrawable(UiUtils.getGreyCircleDrawable());
        holder.dialogImageView.setImageDrawable(null);*/
        mImageLoader = CustomVolleyRequestQueue.getInstance(mContext)
                .getImageLoader();

        try {
            String url = QbDialogUtils.getDialogImage(dialog);
            if (url == "null" || url.isEmpty() || url == null || url.equals("") || url == "") {
                holder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
            } else if (TextUtils.isEmpty(url)) {
                holder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
            } else if (url != null && !url.isEmpty()) {
                try {
                    mImageLoader.get(url, ImageLoader.getImageListener(holder.profileImg,
                            R.drawable.avatar_profile, R.drawable.avatar_profile));
                    holder.profileImg.setImageUrl(url, mImageLoader);
                } catch (Exception e) {
                    holder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
                }
            } else {
                holder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
            }
        } catch (NullPointerException e) {
            holder.profileImg.setDefaultImageResId(R.drawable.avatar_profile);
        }
        holder.nameTextView.setText(QbDialogUtils.getDialogName(dialog));
        if (isLastMessageAttachment(dialog)) {
            holder.lastMessageTextView.setText(R.string.chat_attachment);
        } else {
            holder.lastMessageTextView.setText(dialog.getLastMessage());
        }

        int unreadMessagesCount = dialog.getUnreadMessageCount();
        if (unreadMessagesCount == 0) {
            holder.unreadCounterTextView.setVisibility(View.GONE);
        } else {
            holder.unreadCounterTextView.setVisibility(View.VISIBLE);
            holder.unreadCounterTextView.setText(String.valueOf(unreadMessagesCount > 99 ? 99 : unreadMessagesCount));
        }

        holder.rootLayout.setBackgroundColor(isItemSelected(position) ? AftersAppApplication.getInstance().getResources().getColor(R.color.loginBackgroundTransperant) :
                AftersAppApplication.getInstance().getResources().getColor(android.R.color.transparent));

        return convertView;
    }

    private boolean isLastMessageAttachment(QBDialog dialog) {
        String lastMessage = dialog.getLastMessage();
        Integer lastMessageSenderId = dialog.getLastMessageUserId();
        return TextUtils.isEmpty(lastMessage) && lastMessageSenderId != null;
    }

    private static class ViewHolder {
        ViewGroup rootLayout;
        //ImageView dialogImageView;
        TextView nameTextView;
        TextView lastMessageTextView;
        TextView unreadCounterTextView;
        NetworkRoundImageView profileImg;
    }
}
