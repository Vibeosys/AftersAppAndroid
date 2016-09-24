package com.aftersapp.interfaces.chatinterfaces;

import com.quickblox.chat.model.QBChatMessage;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by akshay on 24-09-2016.
 */
public interface Chat {

    void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException;

    void release() throws XMPPException;
}
