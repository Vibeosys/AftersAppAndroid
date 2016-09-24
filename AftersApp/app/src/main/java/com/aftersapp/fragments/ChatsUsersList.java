package com.aftersapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by akshay on 23-09-2016.
 */
public class ChatsUsersList extends BaseFragment {
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

    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private ActionMode currentActionMode;
    private SwipyRefreshLayout setOnRefreshListener;
    private DialogsAdapter dialogsAdapter;
    private boolean isActivityForeground;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_user_list, container, false);
        googlePlayServicesHelper = new GooglePlayServicesHelper();
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
                if (currentActionMode == null) {
                    //ChatActivity.startForResult(getContext(), REQUEST_MARK_READ, selectedDialog);
                } else {
                    dialogsAdapter.toggleSelection(selectedDialog);
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