package com.aftersapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.MainActivity;
import com.aftersapp.R;
import com.aftersapp.adapters.UserListAdapter;
import com.aftersapp.helper.ChatHelper;
import com.aftersapp.helper.DataHolder;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay on 17-09-2016.
 */
public class UserListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String CHAT_HOST_FRAGMENT = "Chat";
    private SwipyRefreshLayout setOnRefreshListener;
    private List<QBUser> qbUsersList;
    private UserListAdapter usersListAdapter;
    private QBPagedRequestBuilder qbPagedBuilder;
    private static final int LIMIT_USERS = 50;
    private static final int REQUEST_CODE_SIGN_UP = 100;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc date created_at";
    private int currentPage = 1;
    private QBUser meUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_list_fragment, container, false);
        DataHolder.getInstance().clear();
        initUI(rootView);
        getAllUsers(true);
        return rootView;
    }

    private void initUI(View rootView) {
        ListView usersListView = (ListView) rootView.findViewById(R.id.users_listview);
        setOnRefreshListener = (SwipyRefreshLayout) rootView.findViewById(R.id.swipy_refresh_layout);

      /*  TextView listHeader = (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.include_list_header, usersListView, false);
*/
        //usersListView.addHeaderView(listHeader, null, false);
        qbUsersList = DataHolder.getInstance().getQBUsers();
        usersListAdapter = new UserListAdapter(getContext(), qbUsersList);
        usersListView.setAdapter(usersListAdapter);
        usersListView.setOnItemClickListener(this);

        setQBPagedBuilder();
        setOnRefreshListener.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                qbPagedBuilder.setPage(++currentPage);
                getAllUsers(false);
            }
        });
    }

    private void setQBPagedBuilder() {
        qbPagedBuilder = new QBPagedRequestBuilder();
        GenericQueryRule genericQueryRule = new GenericQueryRule(ORDER_RULE, ORDER_VALUE);

        ArrayList<GenericQueryRule> rule = new ArrayList<>();
        rule.add(genericQueryRule);

        qbPagedBuilder.setPerPage(LIMIT_USERS);
        qbPagedBuilder.setRules(rule);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ArrayList<QBUser> selectedUsers = new ArrayList<>();
        selectedUsers.add(meUser);
        selectedUsers.add(usersListAdapter.getItem(position));

        signInChat(usersListAdapter.getItem(position));
    }

    private void getAllUsers(boolean showProgress) {
        if (showProgress) {
            progressDialog.show();
        }

        QBUsers.getUsers(qbPagedBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                setOnRefreshListener.setEnabled(true);
                setOnRefreshListener.setRefreshing(false);

                DataHolder.getInstance().addQbUsers(qbUsers);
                qbUsersList = DataHolder.getInstance().getQBUsers();
                progressDialog.dismiss();
                for (int i = 0; i < qbUsersList.size(); i++) {
                    QBUser qbUser = qbUsersList.get(i);
                    if (qbUser.getEmail().equals(mSessionManager.getEmail())) {
                        qbUsersList.remove(qbUser);
                        meUser = qbUser;
                    }

                }
                usersListAdapter.updateList(qbUsersList);
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                setOnRefreshListener.setEnabled(false);
                setOnRefreshListener.setRefreshing(false);

                /*View rootLayout = findViewById(R.id.swipy_refresh_layout);
                showSnackbarError(rootLayout, R.string.errors, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAllUsers(false);
                    }
                });*/
            }
        });
    }

    private void createDialog(final QBUser selectedUsers) {
        progressDialog.show();
        ChatHelper.getInstance().createDialogWithSelectedUser(selectedUsers,
                new QBEntityCallback<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {
                        progressDialog.dismiss();
                        ChatFragment chatFragment = new ChatFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ChatFragment.EXTRA_DIALOG, dialog);
                        chatFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().
                                replace(R.id.fragment_frame_lay, chatFragment, "ChatFragment").commit();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.msg_err),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void signInChat(final QBUser selectedUsers) {
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
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                createDialog(selectedUsers);
                            }
                        });
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e("UserList", errors.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }
}
