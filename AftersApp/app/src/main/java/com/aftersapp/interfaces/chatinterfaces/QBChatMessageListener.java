package com.aftersapp.interfaces.chatinterfaces;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.model.QBChatMessage;

/**
 * Created by akshay on 24-09-2016.
 */
public interface QBChatMessageListener {
    void onQBChatMessageReceived(QBChat chat, QBChatMessage message);
}
