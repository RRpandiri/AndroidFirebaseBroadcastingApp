package com.uhcl.reachapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.uhcl.reachapp.R;
import com.uhcl.reachapp.adapters.UsersListAdapter;
import com.uhcl.reachapp.data_models.ContactsListPOJO;
import com.uhcl.reachapp.data_models.UserPOJO;

import java.util.ArrayList;
import java.util.Locale;

import static com.uhcl.reachapp.adapters.UsersListAdapter.ACTIVITY_REQUEST_CHATSCREEN;

public class NewChatActivity extends BaseActivity {

    RelativeLayout rlNewChat;
    EditText etSearch;
    ListView lvRegisteredNumbersInContacts;
    TextView tvNoUsers;

    ArrayList<ContactsListPOJO> phoneContacts;
    ArrayList<UserPOJO> usersList;
    UsersListAdapter mAdapter;

    boolean IsMessageSend;
    UserPOJO sendBackUserInfo;

    @Override
    public void initializeclass() {
        rlNewChat = (RelativeLayout) inflater.inflate(R.layout.activity_newchat, null);
        rlMain.addView(rlNewChat);

        showProgressAnimationDialog("Fetching Registered Phone numbers...");
        init();
        disableUI();

        getDataFromPreviousScreen();
        setListenersToViews();
    }

    private void init() {
        etSearch = findViewById(R.id.et_newchat_search);
        lvRegisteredNumbersInContacts = findViewById(R.id.lv_newchat_regnumbersincontacts);
        tvNoUsers = findViewById(R.id.tv_newchat_nousers);
        disableUI();

        phoneContacts = new ArrayList<>();
        usersList = new ArrayList<>();

        IsMessageSend = false;
        sendBackUserInfo = new UserPOJO();
    }

    private void disableUI() {
        etSearch.setVisibility(View.GONE);
        lvRegisteredNumbersInContacts.setVisibility(View.GONE);
        tvNoUsers.setVisibility(View.VISIBLE);
    }

    private void enableUI() {
        etSearch.setVisibility(View.VISIBLE);
        lvRegisteredNumbersInContacts.setVisibility(View.VISIBLE);
        tvNoUsers.setVisibility(View.GONE);
    }

    private void getDataFromPreviousScreen() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<UserPOJO> receivedList = extras.getParcelableArrayList("RegUserArray");
            if (receivedList.size() > 0) {
                usersList.addAll(receivedList);
                enableUI();
                populateListView();
            }
        }
    }

    private void populateListView() {
        mAdapter = new UsersListAdapter(NewChatActivity.this, R.layout.list_item_user, usersList);
        lvRegisteredNumbersInContacts.setAdapter(mAdapter);
        closeProgressAnimationDialog();
    }

    private void setListenersToViews() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = etSearch.getText().toString().trim().toLowerCase(Locale.getDefault());
                mAdapter.filter(searchText);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        NewChatActivity.this.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_CHATSCREEN && resultCode == RESULT_OK) {
            showProgressAnimationDialog("Syncing App with Firebase..");
            IsMessageSend = true;
            sendBackUserInfo = (UserPOJO) data.getParcelableExtra("user");
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        sendDataBack();
    }

    public void sendDataBack() {
        Intent BackIntent = new Intent();
        if (IsMessageSend) {
            BackIntent.putExtra("user", sendBackUserInfo);
            setResult(RESULT_OK, BackIntent);
        } else
            setResult(RESULT_CANCELED, BackIntent);
        closeProgressAnimationDialog();
        finish();
    }
}
