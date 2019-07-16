package com.uhcl.reachapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.adapters.BCCAdapter;
import com.uhcl.reachapp.data_models.UserPOJO;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class BCCActivity extends BaseActivity {

    RelativeLayout rlBCCMain;
    EditText etBoradcastName;
    HorizontalScrollView hsvBCCList;
    ListView lvRegUsersList;
    TextView noUsersText, tvInfo;

    ArrayList<UserPOJO> regUsersList;
    ArrayList<UserPOJO> bccSelectedList;
    BCCAdapter mAdapter;

    public static final int RequestPermissionCode = 1;

    @Override
    public void initializeclass() {

        rlBCCMain = (RelativeLayout) inflater.inflate(R.layout.activity_bcc, null);
        rlMain.addView(rlBCCMain);

        showProgressAnimationDialog("Fetching all Contacts...");
        init();
        disableUI();

        getDataFromPreviousScreen();
        setEventListeners();
    }

    private void init() {
        etBoradcastName = findViewById(R.id.et_bcc_bccname);
        hsvBCCList = findViewById(R.id.bcc_list);
        lvRegUsersList = findViewById(R.id.usersList);
        noUsersText = findViewById(R.id.noUsersText);
        tvInfo = findViewById(R.id.tv_bcc_yourcontacts_info);

        etBoradcastName.setText("");

        regUsersList = new ArrayList<>();
        bccSelectedList = new ArrayList<>();
    }

    private void disableUI() {
        hsvBCCList.setVisibility(View.GONE);
        lvRegUsersList.setVisibility(View.GONE);
        tvInfo.setVisibility(View.GONE);
        noUsersText.setVisibility(View.VISIBLE);
    }

    private void enableUI() {
        hsvBCCList.setVisibility(View.VISIBLE);
        lvRegUsersList.setVisibility(View.VISIBLE);
        tvInfo.setVisibility(View.VISIBLE);
        noUsersText.setVisibility(View.GONE);
    }

    private void getDataFromPreviousScreen() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<UserPOJO> receivedList = extras.getParcelableArrayList("RegUserArray");
            if (receivedList.size() > 0) {
                regUsersList.addAll(receivedList);
                enableUI();
                populateListView();
            }
        }
    }

    private void populateListView() {
        mAdapter = new BCCAdapter(BCCActivity.this, R.layout.list_item_user, regUsersList);
        lvRegUsersList.setAdapter(mAdapter);
        closeProgressAnimationDialog();
    }

    private void setEventListeners() {
        lvRegUsersList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });

        lvRegUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout layout = findViewById(R.id.layout);
                LayoutInflater inflater = getLayoutInflater();
                View view1 = inflater.inflate(R.layout.list_item_user, parent, false);
                UserPOJO user = regUsersList.get(position);
                bccSelectedList.add(user);

                TextView name = view1.findViewById(R.id.tv_listitem_username);
                name.setText(user.getContactname_inphone());
                ImageView image = view1.findViewById(R.id.iv_listitem_userprofilepic);
                Glide.with(BCCActivity.this).load(user.getProfilepic_firebaseuri()).placeholder(R.drawable.baseline_person_black_24).fitCenter().into(image);
                layout.addView(view1);
                regUsersList.remove(regUsersList.get(position));

                mAdapter.notifyDataSetChanged();
            }
        });
        lvRegUsersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.broadcast, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuitem_broadcast:
                if (bccSelectedList.size() > 1 && !etBoradcastName.getText().toString().trim().equalsIgnoreCase("")) {
                    Intent intent = new Intent(BCCActivity.this, BCCGroupSendActivity.class);
                    intent.putParcelableArrayListExtra("BCCSelectedList", bccSelectedList);
                    intent.putExtra("BCCName", etBoradcastName.getText().toString().trim());
                    startActivity(intent);
                    finish();
                } else {
                    Toasty.error(BCCActivity.this, "Select atleast two people and name the broadcast to continue!!", Toast.LENGTH_LONG).show();
                }
        }
        return true;
    }
}