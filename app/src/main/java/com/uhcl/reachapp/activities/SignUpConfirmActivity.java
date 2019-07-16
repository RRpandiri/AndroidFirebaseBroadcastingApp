package com.uhcl.reachapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.data_models.UserPOJO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class SignUpConfirmActivity extends BaseActivity {

    private final String TAG = SignUpConfirmActivity.class.getSimpleName();

    //FireBase variables...
    private FirebaseAuth mAuth;
    private String verificationId;
    private FirebaseStorage storage;
    private StorageReference storageReference, profilePicReference;

    //Layout Variables...
    RelativeLayout rlSignupConfirm;
    private EditText etUserPhoneNumber;
    private EditText etSMSCode;
    private Button btConfirm;
    private TextView screenSubtext, reqCode;

    //    private String enteredPhoneNumber;
    private UserPOJO userObject;

    @Override
    public void initializeclass() {

        rlSignupConfirm = (RelativeLayout) inflater.inflate(R.layout.activity_signup_confirm, null);
        rlMain.addView(rlSignupConfirm);

        mAuth = FirebaseAuth.getInstance();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        enteredPhoneNumber = getIntent().getStringExtra(AppConstants.PHONE_NUMBER);
        //Populating UserAttributes..
        userObject = new UserPOJO();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            userObject = (UserPOJO) extras.get("UserObject");

        initializeViews();
        displayPhoneNumber();
        sendVerificationCode(userObject.getPhonenumber());
        setListenersToViews();
    }

    private void initializeViews() {
        etUserPhoneNumber = findViewById(R.id.editTextConfirmUserId);
        etSMSCode = findViewById(R.id.editTextConfirmCode);
        screenSubtext = findViewById(R.id.textViewConfirmSubtext_1);
        btConfirm = findViewById(R.id.confirm_button);
        reqCode = findViewById(R.id.resend_confirm_req);

        btConfirm.setEnabled(false);
    }

    private void displayPhoneNumber() {
        etUserPhoneNumber.setText(userObject.getPhonenumber());
        etUserPhoneNumber.setEnabled(false);
        screenSubtext.setText("A confirmation code was sent to " + userObject.getPhonenumber() + " via SMS");
    }

    private void sendVerificationCode(String number) {
        showProgressAnimationDialog("Requesting verification code!!");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private void setListenersToViews() {

        etSMSCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText(etSMSCode.getHint());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.textViewConfirmCodeMessage);
                label.setText(" ");
                if (etSMSCode.getText().length() == 6) {
                    hideKeyboard();
                    btConfirm.setEnabled(true);
                } else
                    btConfirm.setEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText("");
                }
            }
        });

        reqCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqConfCode();
            }
        });
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAccount();
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            closeProgressAnimationDialog();
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            closeProgressAnimationDialog();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                showProgressAnimationDialog("Verifying your received code!!");
                etSMSCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            closeProgressAnimationDialog();
            Toasty.error(SignUpConfirmActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void storeUserInFirebase(final UserPOJO userObject) {

        final String url = "https://reachapp-a9310.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://reachapp-a9310.firebaseio.com/users");

                if (s.equals("null")) {
                    saveUserInFirebase(reference);
                    Toasty.success(SignUpConfirmActivity.this, "Account Created successfully", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(userObject.getPhonenumber())) {
                            saveUserInFirebase(reference);
                            Toasty.success(SignUpConfirmActivity.this, "Account Created successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toasty.info(SignUpConfirmActivity.this, "Phone number already exist, retrieved your account! ", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void saveUserInFirebase(Firebase reference) {

                String imageName = userObject.getUsername() + "_" + userObject.getPhonenumber();
                Bitmap imageBitmap = loadBitmapFromStorage(userObject.getProfilepic_phoneuri().toString());
                deleteTempImageDirectory();
                userObject.setProfilepic_phoneuri(saveImageToInternalStorage(imageBitmap, imageName).toString());
                uploadImageToFirebase(Uri.parse(userObject.getProfilepic_phoneuri()), imageName, reference);

                reference.child(userObject.getPhonenumber()).child("username").setValue(userObject.getUsername());
                reference.child(userObject.getPhonenumber()).child("phonenumber").setValue(userObject.getPhonenumber());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(SignUpConfirmActivity.this);
        rQueue.add(request);
    }

//    private void storeReg_NumbersInFirebase(final String verifiedNumber) {
//
//        final String url = "https://reachapp-a9310.firebaseio.com/reg_numbers.json";
//
//        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                Firebase refBroadcastInfo = new Firebase("https://reachapp-a9310.firebaseio.com/reg_numbers");
//
//                if (s.equals("null")) {
//                    refBroadcastInfo.child(verifiedNumber).setValue("Registered");
//                    Log.i(TAG, "Registered number saved successfully!!!");
//                } else {
//                    try {
//                        JSONObject obj = new JSONObject(s);
//
//                        if (!obj.has(verifiedNumber)) {
//                            refBroadcastInfo.child(verifiedNumber).setValue("Registered");
//                            Log.i(TAG, "Registered number saved successfully!!!");
//                        } else {
//                            Log.e(TAG, "Verified number already exists in this table!!!");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                System.out.println("" + volleyError);
//            }
//        });
//        RequestQueue rQueue = Volley.newRequestQueue(SignUpConfirmActivity.this);
//        rQueue.add(request);
//    }

    private void reqConfCode() {
        showProgressAnimationDialog("Requesting Code again...");
        if (userObject.getPhonenumber() == null || userObject.getPhonenumber().length() < 1) {
            TextView label = findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(etUserPhoneNumber.getHint() + " cannot be empty");
            return;
        }
        sendVerificationCode(userObject.getPhonenumber());
    }

    private void confirmAccount() {
        showProgressAnimationDialog("Verifying your received code!!");
        String confirmCode = "";
        confirmCode = etSMSCode.getText().toString();

        if (userObject.getPhonenumber() == null || userObject.getPhonenumber().length() < 1) {
            TextView label = findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(etUserPhoneNumber.getHint() + " cannot be empty");
            return;
        }

        if (confirmCode.equalsIgnoreCase("") || confirmCode.length() < 1) {
            TextView label = findViewById(R.id.textViewConfirmCodeMessage);
            label.setText(etSMSCode.getHint() + " cannot be empty");
            return;
        }
        verifyCode(confirmCode);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            showProgressAnimationDialog("Success!! Phone number verified!!");
                            closeProgressAnimationDialog();
                            showProgressAnimationDialog("Success!! Creating your account!!");
                            storeUserInFirebase(userObject);
//                            storeReg_NumbersInFirebase(userObject.getPhonenumber());
                            saveUserDetailsInPrefs(userObject);

                            launchChatActivity();
                        } else {
                            try {
                                closeProgressAnimationDialog();
                                Toasty.error(SignUpConfirmActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    public void uploadImageToFirebase(Uri image_phoneuri, String imageName, final Firebase reference) {

        if (image_phoneuri != null) {

            Uri uploadUri = Uri.fromFile(new File(image_phoneuri.toString()));

            profilePicReference = storageReference.child("profile_pics/" + imageName);
            UploadTask uploadTask = profilePicReference.putFile(uploadUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SignUpConfirmActivity.this, "Pic Uploaded", Toast.LENGTH_SHORT).show();
                    profilePicReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.child(userObject.getPhonenumber()).child("profilepic_url").setValue(uri.toString());
                            saveProfilePicFirebaseURLInPrefs(uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpConfirmActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        }
    }


    private void launchChatActivity() {
        Intent intent = new Intent(SignUpConfirmActivity.this, ChatListMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        closeProgressAnimationDialog();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgressAnimationDialog();
    }
}