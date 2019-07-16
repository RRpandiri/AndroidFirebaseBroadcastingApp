package com.uhcl.reachapp.activities;

import android.content.Intent;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.data_models.UserPOJO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ChatScreenActivity extends BaseActivity {
    LinearLayout layout;
    RelativeLayout layout_2, rlChatMain;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    SimpleDateFormat sdf;
    UserPOJO TOuserObject, FROMuserObject;

    boolean IsMessageSend;

    @Override
    public void initializeclass() {

        rlChatMain = (RelativeLayout) inflater.inflate(R.layout.activity_chatscreen, null);
        rlMain.addView(rlChatMain);

        sdf = new SimpleDateFormat("EEE, MMM d 'AT' HH:mm a");

        init();
        setListenersToViews();
    }

    private void init() {

        //Getting Intent from Previous screens...
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            getToUserObject(extras);
            getFromUserObject(extras);

            IsMessageSend = false;
        }

        //UI Views Stuff...
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.fullScroll(View.FOCUS_DOWN);

        //Firebase Stuff...
        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://reachapp-a9310.firebaseio.com/messages/" + FROMuserObject.getPhonenumber() + "_" + FROMuserObject.getChatWith());
        reference2 = new Firebase("https://reachapp-a9310.firebaseio.com/messages/" + FROMuserObject.getChatWith() + "_" + FROMuserObject.getPhonenumber());
    }

    private void getToUserObject(Bundle bundle) {
        TOuserObject = (UserPOJO) bundle.get("ChatWithUserObj");
    }

    private void getFromUserObject(Bundle bundle) {
        FROMuserObject = new UserPOJO();
        FROMuserObject.setChatWith(((UserPOJO) bundle.get("ChatWithUserObj")).getPhonenumber());
        FROMuserObject.setPhonenumber(getUserNameFromPrefs());
        FROMuserObject.setUsername(getUserNameFromPrefs());
        FROMuserObject.setProfilepic_firebaseuri(getProfilePicFirebaseURLInPrefs());
    }

    private void setListenersToViews() {

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    String currentDateandTime = sdf.format(new Date());
                    map.put("message", messageText);
                    map.put("sender", FROMuserObject.getPhonenumber());
                    map.put("time", currentDateandTime);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                    IsMessageSend = true;
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String sender = map.get("sender").toString();
                String time = map.get("time").toString();

                if (sender.equals(FROMuserObject.getPhonenumber())) {
                    addMessageBox("You ", message, time, 1);
                } else {
                    addMessageBox(TOuserObject.getContactname_inphone(), message, time, 2);
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
    }

    public void addMessageBox(String name, String message, String time, int type) {

        TextView textmsg = new TextView(ChatScreenActivity.this);
        TextView textname = new TextView(ChatScreenActivity.this);
        TextView texttime = new TextView(ChatScreenActivity.this);

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

    @Override
    public void onBackPressed() {
        sendDataBack();
//        super.onBackPressed();
    }

    public void sendDataBack() {
        showProgressAnimationDialog("Saving Chat..");
        Intent BackIntent = new Intent();
        if (IsMessageSend) {
            BackIntent.putExtra("user", TOuserObject);
            setResult(RESULT_OK, BackIntent);
        } else
            setResult(RESULT_CANCELED, BackIntent);
        closeProgressAnimationDialog();
        finish();
    }
}
