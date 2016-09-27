package com.aftersapp.fragments;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.R;
import com.aftersapp.adapters.qbadapters.DialogsAdapter;
import com.aftersapp.helper.ChatHelper;
import com.aftersapp.helper.GooglePlayServicesHelper;
import com.aftersapp.interfaces.GcmConsts;
import com.aftersapp.utils.QuickBlocsConst;
import com.aftersapp.utils.qbutils.QbDialogHolder;
import com.aftersapp.utils.qbutils.SharedPreferencesUtil;
import com.aftersapp.utils.qbutils.VerboseQbChatConnectionListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by akshay on 23-09-2016.
 */
public class ChatsUsersList extends ChatBaseFragment {
    private static final String TAG = ChatsUsersList.class.getSimpleName();
    private GooglePlayServicesHelper googlePlayServicesHelper;
    private BroadcastReceiver pushBroadcastReceiver;
    private QBPrivateChatManagerListener privateChatManagerListener;
    private ConnectionListener chatConnectionListener;
    private CoordinatorLayout corCoordinatorLayout;
    private QBRequestGetBuilder requestBuilder;
    private int skipRecords = 0;
    private static final int REQUEST_SELECT_PEOPLE = 174;
    private static final int REQUEST_MARK_READ = 165;
    private int READ_PH_STATE_PERMISSION = 19;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private ActionMode currentActionMode;
    private SwipyRefreshLayout setOnRefreshListener;
    private DialogsAdapter dialogsAdapter;
    private boolean isActivityForeground;
    private static final String HOME_FRAGMENT = "home";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String email = mSessionManager.getEmail();
        String password = mSessionManager.getEmail() + mSessionManager.getUserId();
        final QBUser user = new QBUser(email, password);
        QBUsers.signIn(user, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
        onRequestMessagePermission();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void onRequestMessagePermission() {
        requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},
                READ_PH_STATE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PH_STATE_PERMISSION && grantResults[0] == 0) {
        } else {
            Toast toast = Toast.makeText(getContext(),
                    getResources().getString(R.string.str_per_accept), Toast.LENGTH_SHORT);
            toast.show();
            HomeFragment homeFragment = new HomeFragment();
            getActivity().getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, homeFragment, HOME_FRAGMENT).commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_user_list, container, false);
        googlePlayServicesHelper = new GooglePlayServicesHelper(mSessionManager);
        if (googlePlayServicesHelper.checkPlayServicesAvailable(getActivity())) {
            googlePlayServicesHelper.registerForGcm(QuickBlocsConst.GCM_SENDER_ID);
        }
        pushBroadcastReceiver = new PushBroadcastReceiver();
        corCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.layout_root);
        privateChatManagerListener = new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(QBPrivateChat qbPrivateChat, boolean createdLocally) {
                if (!createdLocally) {
                    qbPrivateChat.addMessageListener(privateChatMessageListener);
                }
            }
        };

        chatConnectionListener = new VerboseQbChatConnectionListener(corCoordinatorLayout) {

            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();

                requestBuilder.setSkip(skipRecords = 0);
                loadDialogsFromQbInUiThread(true);
            }
        };

        initUi(rootView);
        return rootView;
    }

    @Override
    public void onSessionCreated(boolean success) {
        if (success) {
            QBUser currentUser = ChatHelper.getCurrentUser();
            if (currentUser != null) {
                //setActionBarTitle(getString(R.string.dialogs_logged_in_as, currentUser.getFullName()));
            }

            registerQbChatListeners();
            if (QbDialogHolder.getInstance().getDialogList().size() > 0) {
                loadDialogsFromQb(true, true);
            } else {
                loadDialogsFromQb();
            }
        }
    }

    private void registerQbChatListeners() {
        QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        if (privateChatManager != null) {
            privateChatManager.addPrivateChatManagerListener(privateChatManagerListener);
        }

       /* if (groupChatManager != null) {
            groupChatManager.addGroupChatManagerListener(groupChatManagerListener);
        }*/
    }

    private void unregisterQbChatListeners() {
        QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        if (privateChatManager != null) {
            privateChatManager.removePrivateChatManagerListener(privateChatManagerListener);
        }

       /* if (groupChatManager != null) {
            groupChatManager.removeGroupChatManagerListener(groupChatManagerListener);
        }*/
    }

    private class PushBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(GcmConsts.EXTRA_GCM_MESSAGE);
            Log.i(TAG, "Received broadcast " + intent.getAction() + " with data: " + message);
            loadDialogsFromQb(true, true);
        }
    }

    private void loadDialogsFromQbInUiThread(final boolean silentUpdate) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadDialogsFromQb(silentUpdate, true);
            }
        });
    }

    private void initUi(View rootView) {
        LinearLayout emptyHintLayout = (LinearLayout) rootView.findViewById(R.id.layout_chat_empty);
        ListView dialogsListView = (ListView) rootView.findViewById(R.id.list_dialogs_chats);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_dialogs);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        setOnRefreshListener = (SwipyRefreshLayout) rootView.findViewById(R.id.swipy_refresh_layout);

        dialogsAdapter = new DialogsAdapter(getContext(), QbDialogHolder.getInstance().getDialogList());

        /*TextView listHeader = (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.include_list_hint_header, dialogsListView, false);
        listHeader.setText(R.string.dialogs_list_hint);*/
        dialogsListView.setEmptyView(emptyHintLayout);
        //dialogsListView.addHeaderView(listHeader, null, false);

        dialogsListView.setAdapter(dialogsAdapter);

        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog) parent.getItemAtPosition(position);
                if (QBChatService.getInstance().getPrivateChatManager() == null) {
                    signInChat(selectedDialog);
                } else {
                    if (currentActionMode == null) {
                        ChatFragment chatFragment = new ChatFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ChatFragment.EXTRA_DIALOG, selectedDialog);
                        chatFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().
                                replace(R.id.fragment_frame_lay, chatFragment, "ChatFragment").commit();
                    } else {
                        dialogsAdapter.toggleSelection(selectedDialog);
                    }
                }
            }
        });
        dialogsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog) parent.getItemAtPosition(position);
                //startSupportActionMode(new DeleteActionModeCallback());
                dialogsAdapter.selectItem(selectedDialog);
                return true;
            }
        });
        requestBuilder = new QBRequestGetBuilder();

        setOnRefreshListener.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                requestBuilder.setSkip(skipRecords += ChatHelper.DIALOG_ITEMS_PER_PAGE);
                loadDialogsFromQb(true, false);
            }
        });
    }

    public void signInChat(final QBDialog selectedDialog) {
        progressDialog.show();
        final QBChatService chatService = QBChatService.getInstance();
        QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
        chatService.setDebugEnabled(true);
        chatService.setDefaultPacketReplyTimeout(150000); //add this
        chatService.setDefaultConnectionTimeout(150000); //add this
        chatService.setUseStreamManagement(true);
        //chatService.addConnectionListener(chatConnectionListener);
        final QBUser user = new QBUser(mSessionManager.getEmail(), mSessionManager.getEmail() + mSessionManager.getUserId());
        QBAuth.createSession(user, new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success, login to chat

                user.setId(session.getUserId());

                chatService.login(user, new QBEntityCallback() {

                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        progressDialog.dismiss();
                        ChatFragment chatFragment = new ChatFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable(ChatFragment.EXTRA_DIALOG, selectedDialog);
                        chatFragment.setArguments(bundle1);
                        getFragmentManager().beginTransaction().
                                replace(R.id.fragment_frame_lay, chatFragment, "ChatFragment").commit();
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e("UserList", errors.getMessage());
                        progressDialog.dismiss();
                        ChatFragment chatFragment = new ChatFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable(ChatFragment.EXTRA_DIALOG, selectedDialog);
                        chatFragment.setArguments(bundle1);
                        getFragmentManager().beginTransaction().
                                replace(R.id.fragment_frame_lay, chatFragment, "ChatFragment").commit();
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }


    QBMessageListener<QBPrivateChat> privateChatMessageListener = new QBMessageListener<QBPrivateChat>() {
        @Override
        public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {
            requestBuilder.setSkip(skipRecords = 0);
            if (isActivityForeground) {
                loadDialogsFromQbInUiThread(true);
            }
        }

        @Override
        public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {

        }
    };

    /*  public ActionMode startSupportActionMode(ActionMode.Callback callback) {
          currentActionMode = getActivity().startSupportActionMode(callback);
          return currentActionMode;
      }*/
    @Override
    public void onResume() {
        super.onResume();
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
        isActivityForeground = true;
        googlePlayServicesHelper.checkPlayServicesAvailable(getActivity());

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pushBroadcastReceiver,
                new IntentFilter(GcmConsts.ACTION_NEW_GCM_EVENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
        isActivityForeground = false;
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pushBroadcastReceiver);
    }

    private void loadDialogsFromQb() {
        loadDialogsFromQb(false, true);
    }

    private void loadDialogsFromQb(final boolean silentUpdate, final boolean clearDialogHolder) {
        if (!silentUpdate) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ChatHelper.getInstance().getDialogs(requestBuilder, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogs, Bundle bundle) {
                progressBar.setVisibility(View.GONE);
                setOnRefreshListener.setRefreshing(false);

                if (clearDialogHolder) {
                    QbDialogHolder.getInstance().clear();
                }
                QbDialogHolder.getInstance().addDialogs(dialogs);
                dialogsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                setOnRefreshListener.setRefreshing(false);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isAppSessionActive) {
            unregisterQbChatListeners();
        }
    }

    private class DeleteActionModeCallback implements ActionMode.Callback {

        public DeleteActionModeCallback() {
            fab.hide();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_mode_dialogs, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_dialogs_action_delete:
                    deleteSelectedDialogs();
                    if (currentActionMode != null) {
                        currentActionMode.finish();
                    }
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            currentActionMode = null;
            dialogsAdapter.clearSelection();
            fab.show();
        }

        private void deleteSelectedDialogs() {
            final Collection<QBDialog> selectedDialogs = dialogsAdapter.getSelectedItems();
            ChatHelper.getInstance().deleteDialogs(selectedDialogs, new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    QbDialogHolder.getInstance().deleteDialogs(selectedDialogs);
                }

                @Override
                public void onError(QBResponseException e) {
                    /*showErrorSnackbar(R.string.dialogs_deletion_error, e,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteSelectedDialogs();
                                }
                            });*/
                }
            });
        }
    }
}
