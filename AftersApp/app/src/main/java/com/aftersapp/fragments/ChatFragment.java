package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.aftersapp.R;
import com.aftersapp.adapters.qbadapters.AttachmentPreviewAdapter;
import com.aftersapp.adapters.qbadapters.ChatAdapter;
import com.aftersapp.helper.ChatHelper;
import com.aftersapp.helper.ImagePickHelper;
import com.aftersapp.interfaces.chatinterfaces.Chat;
import com.aftersapp.interfaces.chatinterfaces.PaginationHistoryListener;
import com.aftersapp.interfaces.chatinterfaces.QBChatMessageListener;
import com.aftersapp.utils.Toaster;
import com.aftersapp.utils.qbutils.QbDialogUtils;
import com.aftersapp.utils.qbutils.VerboseQbChatConnectionListener;
import com.aftersapp.utils.qbutils.chatutils.PrivateChatImpl;
import com.aftersapp.views.chatviews.AttachmentPreviewAdapterView;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by akshay on 24-09-2016.
 */
public class ChatFragment extends ChatBaseFragment implements View.OnClickListener {

    private static final int REQUEST_CODE_ATTACHMENT = 721;
    private static final int REQUEST_CODE_SELECT_PEOPLE = 752;

    public static final String EXTRA_DIALOG = "dialog";
    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    public static final String EXTRA_MARK_READ = "markRead";
    public static final String EXTRA_DIALOG_ID = "dialogId";
    private static final String TAG = ChatFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private StickyListHeadersListView messagesListView;
    private EditText messageEditText;

    private LinearLayout attachmentPreviewContainerLayout;
    private Snackbar snackbar;

    private ChatAdapter chatAdapter;
    private AttachmentPreviewAdapter attachmentPreviewAdapter;
    private ConnectionListener chatConnectionListener;

    private Chat chat;
    private QBDialog qbDialog;
    private ArrayList<String> chatMessageIds;
    private ArrayList<QBChatMessage> unShownMessages;
    private int skipPagination = 0;
    private ImageButton imgSend, imgAttach;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            qbDialog = (QBDialog) getArguments().getSerializable(EXTRA_DIALOG);

        }
        chatMessageIds = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_history_fragment, container, false);

        initChatConnectionListener();

        initViews(rootView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (qbDialog != null) {
            outState.putSerializable(EXTRA_DIALOG, qbDialog);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (qbDialog == null) {
            qbDialog = (QBDialog) savedInstanceState.getSerializable(EXTRA_DIALOG);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
    }

    private void initViews(View rootView) {

        messagesListView = (StickyListHeadersListView) rootView.findViewById(R.id.list_chat_messages);
        messageEditText = (EditText) rootView.findViewById(R.id.edit_chat_message);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_chat);
        attachmentPreviewContainerLayout = (LinearLayout) rootView.findViewById(R.id.layout_attachment_preview_container);
        imgSend = (ImageButton) rootView.findViewById(R.id.button_chat_send);
        imgAttach = (ImageButton) rootView.findViewById(R.id.button_chat_attachment);

        imgSend.setOnClickListener(this);
        imgAttach.setOnClickListener(this);
        attachmentPreviewAdapter = new AttachmentPreviewAdapter(getContext(),
                new AttachmentPreviewAdapter.OnAttachmentCountChangedListener() {
                    @Override
                    public void onAttachmentCountChanged(int count) {
                        attachmentPreviewContainerLayout.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
                    }
                }, new AttachmentPreviewAdapter.OnAttachmentUploadErrorListener() {
            @Override
            public void onAttachmentUploadError(QBResponseException e) {

            }
        });
        AttachmentPreviewAdapterView previewAdapterView = (AttachmentPreviewAdapterView) rootView.findViewById(R.id.adapter_view_attachment_preview);
        previewAdapterView.setAdapter(attachmentPreviewAdapter);
    }

    private void initChatConnectionListener() {
        chatConnectionListener = new VerboseQbChatConnectionListener(messagesListView) {
            @Override
            public void connectionClosedOnError(final Exception e) {
                super.connectionClosedOnError(e);

            }

            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();
                skipPagination = 0;
                chatAdapter = null;
                switch (qbDialog.getType()) {
                    case PRIVATE:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadChatHistory();
                            }
                        });
                        break;
                    case GROUP:
                        // Join active room if we're in Group Chat
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Group chat is on");
                                // joinGroupChat();
                            }
                        });
                        break;
                }
            }
        };
    }

    private QBChatMessageListener chatMessageListener = new QBChatMessageListener() {
        @Override
        public void onQBChatMessageReceived(QBChat chat, QBChatMessage message) {
            chatMessageIds.add(message.getId());
            showMessage(message);
        }
    };

    private void loadChatHistory() {
        ChatHelper.getInstance().loadChatHistory(qbDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                // The newest messages should be in the end of list,
                // so we need to reverse list to show messages in the right order
                Collections.reverse(messages);
                if (chatAdapter == null) {
                    chatAdapter = new ChatAdapter(getContext(), messages);
                    chatAdapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                        @Override
                        public void downloadMore() {
                            loadChatHistory();
                        }
                    });
                    chatAdapter.setOnItemInfoExpandedListener(new ChatAdapter.OnItemInfoExpandedListener() {
                        @Override
                        public void onItemInfoExpanded(final int position) {
                            if (isLastItem(position)) {
                                // HACK need to allow info textview visibility change so posting it via handler
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messagesListView.setSelection(position);
                                    }
                                });
                            } else {
                                messagesListView.smoothScrollToPosition(position);
                            }
                        }

                        private boolean isLastItem(int position) {
                            return position == chatAdapter.getCount() - 1;
                        }
                    });
                    if (unShownMessages != null && !unShownMessages.isEmpty()) {
                        List<QBChatMessage> chatList = chatAdapter.getList();
                        for (QBChatMessage message : unShownMessages) {
                            if (!chatList.contains(message)) {
                                chatAdapter.add(message);
                            }
                        }
                    }
                    messagesListView.setAdapter(chatAdapter);
                    messagesListView.setAreHeadersSticky(false);
                    messagesListView.setDivider(null);
                    progressBar.setVisibility(View.GONE);
                } else {
                    chatAdapter.addList(messages);
                    messagesListView.setSelection(messages.size());
                }
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                skipPagination -= ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
                //snackbar = showErrorSnackbar(R.string.connection_error, e, null);
            }
        });
        skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
    }

    public void showMessage(QBChatMessage message) {
        if (chatAdapter != null) {
            chatAdapter.add(message);
            scrollMessageListDown();
        } else {
            if (unShownMessages == null) {
                unShownMessages = new ArrayList<>();
            }
            unShownMessages.add(message);
        }
    }

    private void scrollMessageListDown() {
        messagesListView.setSelection(messagesListView.getCount() - 1);
    }

    @Override
    public void onSessionCreated(boolean success) {
        if (success) {
            initChat();
        }
    }

    private void initChat() {
        switch (qbDialog.getType()) {
            case GROUP:
            case PUBLIC_GROUP:
                /*chat = new GroupChatImpl(chatMessageListener);
                joinGroupChat();*/
                break;

            case PRIVATE:
                chat = new PrivateChatImpl(chatMessageListener, QbDialogUtils.getOpponentIdForPrivateDialog(qbDialog));
                loadDialogUsers();
                break;

            default:
                //Toaster.shortToast(String.format("%s %s", getString(R.string.chat_unsupported_type), qbDialog.getType().name()));
                //finish();
                break;
        }
    }

    private void loadDialogUsers() {
        ChatHelper.getInstance().getUsersFromDialog(qbDialog, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                setChatNameToActionBar();
                loadChatHistory();
            }

            @Override
            public void onError(QBResponseException e) {
               /* showErrorSnackbar(R.string.chat_load_users_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadDialogUsers();
                            }
                        });*/
            }
        });
    }

    private void setChatNameToActionBar() {
        String chatName = QbDialogUtils.getDialogName(qbDialog);
        /*getActivity().setTitle(chatName);*/
       /* getActivity().setDisplayHomeAsUpEnabled(true);
        getActivity().setHomeButtonEnabled(true);*/

    }

    private void releaseChat() {
        try {
            if (chat != null) {
                chat.release();
            }
        } catch (XMPPException e) {
            Log.e(TAG, "Failed to release chat", e);
        }
    }

    private void updateDialog(final ArrayList<QBUser> selectedUsers) {
        ChatHelper.getInstance().updateDialogUsers(qbDialog, selectedUsers,
                new QBEntityCallback<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {
                        qbDialog = dialog;
                        loadDialogUsers();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                      /*  showErrorSnackbar(R.string.chat_info_add_people_error, e,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateDialog(selectedUsers);
                                    }
                                });*/
                    }
                }
        );
    }

    public void onSendChatClick() {
        int totalAttachmentsCount = attachmentPreviewAdapter.getCount();
        Collection<QBAttachment> uploadedAttachments = attachmentPreviewAdapter.getUploadedAttachments();
        if (!uploadedAttachments.isEmpty()) {
            if (uploadedAttachments.size() == totalAttachmentsCount) {
                for (QBAttachment attachment : uploadedAttachments) {
                    sendChatMessage(null, attachment);
                }
            } else {
                Toaster.shortToast(R.string.chat_wait_for_attachments_to_upload);
            }
        }

        String text = messageEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            sendChatMessage(text, null);
        }
    }

    public void onAttachmentsClick() {
        new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_chat_attachment:
                onAttachmentsClick();
                break;
            case R.id.button_chat_send:
                onSendChatClick();
                break;
        }
    }

    private void sendChatMessage(String text, QBAttachment attachment) {
        QBChatMessage chatMessage = new QBChatMessage();
        if (attachment != null) {
            chatMessage.addAttachment(attachment);
        } else {
            chatMessage.setBody(text);
        }
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);

        try {
            chat.sendMessage(chatMessage);

            if (qbDialog.getType() == QBDialogType.PRIVATE) {
                showMessage(chatMessage);
            }

            if (attachment != null) {
                attachmentPreviewAdapter.remove(attachment);
            } else {
                messageEditText.setText("");
            }
        } catch (XMPPException | SmackException e) {
            Log.e(TAG, "Failed to send a message", e);
            Toaster.shortToast(R.string.chat_send_message_error);
        }
    }
}
