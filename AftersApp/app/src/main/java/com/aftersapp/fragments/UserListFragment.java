package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aftersapp.R;
import com.aftersapp.adapters.UserListAdapter;
import com.aftersapp.helper.DataHolder;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

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

                .inflate(R.layout.include_list_header, usersListView, false);
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
}
