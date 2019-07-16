package com.uhcl.reachapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.data_models.ContactsListPOJO;
import com.uhcl.reachapp.data_models.UserPOJO;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class BCCGroupSendActivity extends BaseActivity {
    LinearLayout layout;
    RelativeLayout layout_2, rlBCCGroupMain;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase refBroadcastInfo, reference2, refMessagesFromUserToBroadcast;
    SimpleDateFormat sdf;

    SharedPreferences sharedPref;
    String phone, BCCTreeName;
    UserPOJO fromUser;
    ArrayList<ContactsListPOJO> contactsLists;
    ArrayList<UserPOJO> bccSelectedList;

    @Override
    public void initializeclass() {

        rlBCCGroupMain = (RelativeLayout) inflater.inflate(R.layout.activity_chatscreen, null);
        rlMain.addView(rlBCCGroupMain);

        sdf = new SimpleDateFormat("EEE, MMM d 'AT' HH:mm a");

        showProgressAnimationDialog("Preparing screen to chat...");
        init();

        setEventListeners();
    }


    private void init() {

        fromUser = new UserPOJO();
        bccSelectedList = new ArrayList<>();
        Firebase.setAndroidContext(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<UserPOJO> receivedList = extras.getParcelableArrayList("BCCSelectedList");
            if (receivedList.size() > 0) {
                bccSelectedList.addAll(receivedList);

//                getToUserObject(extras); //We got this from above line..
                getFromUserObject(extras);
                BCCTreeName = fromUser.getPhonenumber().trim() + "_" + fromUser.getChatWith().trim() + "_" + UUID.randomUUID().toString();

                //Firebase Stuff...
                refBroadcastInfo = new Firebase("https://reachapp-a9310.firebaseio.com/broadcast_info");
                refMessagesFromUserToBroadcast = new Firebase("https://reachapp-a9310.firebaseio.com/messages/" + BCCTreeName);

                createBCCTreeInFirebase();
            }
        }

        //UI Views Stuff...
        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void getFromUserObject(Bundle bundle) {
        fromUser = new UserPOJO();
        fromUser.setChatWith(bundle.getString("BCCName"));
        fromUser.setPhonenumber(getUserNameFromPrefs());
        fromUser.setUsername(getUserNameFromPrefs());
        fromUser.setProfilepic_firebaseuri(getProfilePicFirebaseURLInPrefs());
    }


    private void createBCCTreeInFirebase() {

        final String url = "https://reachapp-a9310.firebaseio.com/broadcast_info.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                if (s.equals("null")) {
                    saveBCCTreeInFirebase();
                    Toasty.success(BCCGroupSendActivity.this, "Group Created successfully", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);
                        Iterator i = obj.keys();
                        String key = "";
                        while (i.hasNext()) {
                            if (!i.equals(BCCTreeName)) {
                                saveBCCTreeInFirebase();
                                Toasty.success(BCCGroupSendActivity.this, "Group Created successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toasty.info(BCCGroupSendActivity.this, "Group already exist, retrieved your group! ", Toast.LENGTH_LONG).show();
                            }
//                            key = i.next().toString();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void saveBCCTreeInFirebase() {

                //Group Members..
                String grpMembersString = "";
                for (UserPOJO user : bccSelectedList) {
                    if (!grpMembersString.equalsIgnoreCase(""))
                        grpMembersString = grpMembersString + ",";
                    grpMembersString = grpMembersString + user.getPhonenumber();
                }

                refBroadcastInfo.child(BCCTreeName).child("owner").setValue(fromUser.getPhonenumber());
                refBroadcastInfo.child(BCCTreeName).child("group_name").setValue(fromUser.getChatWith());
                refBroadcastInfo.child(BCCTreeName).child("members").setValue(grpMembersString);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(BCCGroupSendActivity.this);
        rQueue.add(request);
    }

    private void setEventListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
//                    for (int i = 0; i < bccSelectedList.size(); i++) {
//                        UserPOJO user = bccSelectedList.get(i);

                        Map<String, String> map = new HashMap<String, String>();
                        String currentDateandTime = sdf.format(new Date());
                        map.put("message", messageText);
                        map.put("sender", fromUser.getPhonenumber());
                        map.put("time", currentDateandTime);
                        refMessagesFromUserToBroadcast.push().setValue(map);
                        messageArea.setText("");
//                        if (i == contactsLists.size()) {
//                            reference2.push().setValue(map);
//                        }
//                    }
                }
            }
        });

        refMessagesFromUserToBroadcast.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                String time = map.get("time").toString();

                if (userName.equals(phone)) {
                    addMessageBox("You ", message, time, 1);
                } else {
                    addMessageBox(fromUser.getChatWith(), message, time, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
//
//        refBroadcastInfo.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                Map map = dataSnapshot.getValue(Map.class);
//                String message = map.get("owner").toString();
//                String userName = map.get("group_name").toString();
//                String time = map.get("members").toString();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
    }

    public void addMessageBox(String name, String message, String time, int type) {

        TextView textmsg = new TextView(BCCGroupSendActivity.this);
        TextView textname = new TextView(BCCGroupSendActivity.this);
        TextView texttime = new TextView(BCCGroupSendActivity.this);

        textname.setText(name);
        textname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        textmsg.setText(message);
        texttime.setText(time);
        texttime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp1.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;
            lp3.gravity = Gravity.RIGHT;
            textmsg.setBackgroundResource(R.drawable.text_in);

        } else {
            lp1.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
            lp3.gravity = Gravity.LEFT;
            textmsg.setBackgroundResource(R.drawable.text_out);
        }


        textname.setLayoutParams(lp1);
        textmsg.setLayoutParams(lp2);
        texttime.setLayoutParams(lp3);

        layout.addView(textname);
        layout.addView(textmsg);
        layout.addView(texttime);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

}
