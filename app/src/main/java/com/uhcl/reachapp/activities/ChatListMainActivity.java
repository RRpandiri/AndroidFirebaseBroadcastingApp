package com.uhcl.reachapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.adapters.UsersListAdapter;
import com.uhcl.reachapp.data_models.BroadcastPOJO;
import com.uhcl.reachapp.data_models.ContactsListPOJO;
import com.uhcl.reachapp.data_models.UserPOJO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import es.dmoral.toasty.Toasty;


public class ChatListMainActivity extends BaseActivity {

    RelativeLayout rlChatListMain;

    ListView lvChatList;
    TextView tvNoList;
    Button btStartChat;
    int totalChatThreadCount = 0;

    //    ArrayList<ContactsListPOJO> StoreContacts = new ArrayList<>();
    Cursor cursor;
    boolean IsBroadcast;
    ArrayList<ContactsListPOJO> phoneContacts;
    ArrayList<UserPOJO> regUsersList;
    ArrayList<UserPOJO> receiverUsersList;
    ArrayList<BroadcastPOJO> receiverBroadcastsList;
    UsersListAdapter mAdapter;
    int countOfRegisteredUsersInPhoneContacts;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int ACTIVITY_REQUEST_CHATTED_USER = 25;


    @Override
    public void initializeclass() {
        showProgressAnimationDialog("Loading...");
        rlChatListMain = (RelativeLayout) inflater.inflate(R.layout.activity_chatlist_main, null);
        rlMain.addView(rlChatListMain);

        init();
        disableUI();

        prepareUserList();  //Match the phone contacts with the Firebase Registered MobileNumbers!!
        closeProgressAnimationDialog();
        startChatButtonLogic();
    }

    private void init() {
        lvChatList = findViewById(R.id.lv_chatlist_chatlist);
        tvNoList = findViewById(R.id.tv_chatlist_nolist);
        btStartChat = findViewById(R.id.bt_chatlist_startchat);

        IsBroadcast = false;
        phoneContacts = new ArrayList<>();
        regUsersList = new ArrayList<>();
        receiverUsersList = new ArrayList<>();
        receiverBroadcastsList = new ArrayList<>();
        countOfRegisteredUsersInPhoneContacts = 0;
    }

    private void enableUI() {
        tvNoList.setVisibility(View.GONE);
        btStartChat.setVisibility(View.GONE);
        lvChatList.setVisibility(View.VISIBLE);
    }

    private void disableUI() {
        tvNoList.setVisibility(View.VISIBLE);
        btStartChat.setVisibility(View.VISIBLE);
        lvChatList.setVisibility(View.GONE);
    }

    private void prepareUserList() {

        getPhoneContactsIntoArrayList();
        if (phoneContacts.size() > 1)
            doFirebaseLogicForContactSync();
        else {
            closeProgressAnimationDialog();
            tvNoList.setText("Import your contact into this phone to continue!!");
        }
    }

    public void getPhoneContactsIntoArrayList() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                ContactsListPOJO cl = new ContactsListPOJO();
                cl.setContact_name(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                cl.setPhone(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                phoneContacts.add(cl);
            }
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoneContactsIntoArrayList();
            } else {
                Toasty.error(this, "Until you grant the permissions,\n we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void doFirebaseLogicForContactSync() {
        final String url = "https://reachapp-a9310.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj = new JSONObject(s);

                    Iterator i = obj.keys();
                    String key = "";
                    while (i.hasNext()) {
                        key = i.next().toString();

                        if (!key.equals(getPhonenumberFromPrefs())) {
                            for (ContactsListPOJO cl : phoneContacts) {
                                if (key.equals((cl.getPhone()).replaceAll("[-.,()\\s]", "")) || key.substring(2).equals((cl.getPhone()).replaceAll("[-.,()\\s+]", ""))) {
                                    JSONObject userObj = obj.getJSONObject(key);
                                    final UserPOJO user = new UserPOJO();
                                    user.setPhonenumber(userObj.getString("phonenumber"));
                                    user.setUsername(userObj.getString("username"));
                                    user.setProfilepic_firebaseuri(userObj.getString("profilepic_url"));
                                    user.setContactname_inphone(cl.getContact_name());
                                    regUsersList.add(user);
                                    countOfRegisteredUsersInPhoneContacts++;
                                    break;
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (regUsersList.size() > 0) {
                    enableUI();
                    doBusinessLogicOfChatlist();
                }
                closeProgressAnimationDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatListMainActivity.this);
        rQueue.add(request);
    }

    public void doBusinessLogicOfChatlist() {
        String url = "https://reachapp-a9310.firebaseio.com/messages.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                displayChatListInfoFromFirebase(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatListMainActivity.this);
        rQueue.add(request);
    }

    public void displayChatListInfoFromFirebase(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";
            while (i.hasNext()) {
                key = i.next().toString();

                String[] parts = key.split("_");
                if (parts.length == 2) {
                    IsBroadcast = false;
                } else {
                    IsBroadcast = true;
                }

                String sender = parts[0];
                String receiver = parts[1];

                sender = "+" + sender.trim();
                receiver = "+" + receiver.trim();

                //TODO: here add one more if to implement BCC!!
                if (sender.equals(getPhonenumberFromPrefs()) && !IsBroadcast) {
                    UserPOJO receiverUser = new UserPOJO();
                    for (UserPOJO user : regUsersList) {
                        if (user.getPhonenumber().equals(receiver)) {
                            receiverUser = user;
                            receiverUsersList.add(receiverUser);
                            totalChatThreadCount++;
                        }
                    }
                } else if (sender.equals(getPhonenumberFromPrefs()) && IsBroadcast) {

                    BroadcastPOJO receiverBroadcast = new BroadcastPOJO();
                    for (BroadcastPOJO broadcast : receiverBroadcastsList) {
                        if (broadcast.getGroup_name().equals(receiver)) {
                            UserPOJO user = new UserPOJO();
                            user.setIsBroadcast("YES");
                            user.setSingle_group_name(broadcast.getGroup_name());
                            user.setSingle_owner(broadcast.getOwner());
                            user.setSingleMembers(broadcast.getMembers());
                            receiverUsersList.add(user);
                            totalChatThreadCount++;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (receiverUsersList.size() > 0) {
            enableUI();
            populateListView(receiverUsersList);
        } else {
            disableUI();
        }

        closeProgressAnimationDialog();
    }

    private void populateListView(ArrayList<UserPOJO> _receiverUsersList) {
        mAdapter = new UsersListAdapter(ChatListMainActivity.this, R.layout.list_item_user, _receiverUsersList);
        lvChatList.setAdapter(mAdapter);
        closeProgressAnimationDialog();
    }


    private void startChatButtonLogic() {
        btStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewChat();
            }
        });
    }

    private void startNewChat() {
        Intent intent = new Intent(ChatListMainActivity.this, NewChatActivity.class);
        intent.putParcelableArrayListExtra("RegUserArray", regUsersList);
        startActivityForResult(intent, ACTIVITY_REQUEST_CHATTED_USER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuitem_single:
                Intent singleIntent = new Intent(ChatListMainActivity.this, NewChatActivity.class);
                singleIntent.putParcelableArrayListExtra("RegUserArray", regUsersList);
//                startActivity(singleIntent);
                startActivityForResult(singleIntent, ACTIVITY_REQUEST_CHATTED_USER);
                break;

            case R.id.menuitem_broadcast:
                Intent broadcastIntent = new Intent(ChatListMainActivity.this, BCCActivity.class);
                broadcastIntent.putParcelableArrayListExtra("RegUserArray", regUsersList);
                startActivity(broadcastIntent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_CHATTED_USER && resultCode == RESULT_OK) {
            showProgressAnimationDialog("Preparing the updated List!!");
            if (data.getExtras() != null) {
                UserPOJO user = data.getParcelableExtra("user");
                if (!receiverUsersList.contains(user)) {
                    receiverUsersList.add(user);
                    populateListView(receiverUsersList);
                }
            }
        }
    }
}