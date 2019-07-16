package com.uhcl.reachapp.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.uhcl.reachapp.R;
import com.uhcl.reachapp.data_models.UserPOJO;
import com.uhcl.reachapp.utilities.NetworkUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.uhcl.reachapp.common.AppConstants.PREFS_IMAGEPATHURL;
import static com.uhcl.reachapp.common.AppConstants.PREFS_IMAGE_FIREBASEURL;
import static com.uhcl.reachapp.common.AppConstants.PREFS_NAME;
import static com.uhcl.reachapp.common.AppConstants.PREFS_PHONENUMBER;
import static com.uhcl.reachapp.common.AppConstants.PREFS_USERNAME;


public abstract class BaseActivity extends AppCompatActivity {

    private final String TAG = BaseActivity.class.getSimpleName();

    RelativeLayout rlMain;

    LayoutInflater inflater;
    private static AlertDialog alertDialog;

    IntentFilter filter = new IntentFilter();

    //    public static String phoneNumber = "";
    public static String imageProfileString = "";
    public static String preferedUserName;
    public static String userID;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (!NetworkUtils.isNetworkConnectionAvailable(context)) {
                    showAppClosingDialog("OFFLINE!!", "Oops! Come with Internet");
                    Log.e("Reach", "Online Connect Intenet ");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initializeBaseClass();
        initializeclass();
    }

    public abstract void initializeclass();

    private void initializeBaseClass() {
        rlMain = findViewById(R.id.rlbase_main);

        inflater = getLayoutInflater();
        prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUserDetailsInPrefs(UserPOJO userObj) {
        editor.putString(PREFS_PHONENUMBER, userObj.getPhonenumber());
        editor.putString(PREFS_USERNAME, userObj.getPhonenumber());
        editor.putString(PREFS_IMAGEPATHURL, userObj.getPhonenumber());
        editor.apply();
    }

    public void saveProfilePicFirebaseURLInPrefs(String urlString) {
        editor.putString(PREFS_IMAGE_FIREBASEURL, urlString);
        editor.apply();
    }

    public String getProfilePicFirebaseURLInPrefs() {
        return prefs.getString(PREFS_IMAGE_FIREBASEURL, "");
    }

    public String getUserNameFromPrefs() {
        return prefs.getString(PREFS_USERNAME, "");
    }

    public String getPhonenumberFromPrefs() {
        return prefs.getString(PREFS_PHONENUMBER, "");
    }

    public void showAppClosingDialog(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                    finishAffinity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void closeAlertDialog() {
        try {
            if (alertDialog.isShowing())
                alertDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                Log.w(TAG, "Tried to unregister the reciver when it's not registered");
            } else {
                e.printStackTrace();
            }
        }
    }

    public void showProgressAnimationDialog(String textProgress) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        LayoutInflater factory = LayoutInflater.from(BaseActivity.this);
        final View view = factory.inflate(R.layout.dialog_animation, null);
        TextView tvMessage = view.findViewById(R.id.tvdialog_progresstext);
        tvMessage.setText(textProgress);
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void closeProgressAnimationDialog() {
        closeAlertDialog();
    }

    public Uri saveTempImageToInternalStorage(Bitmap bitmapImage, String imageName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/temp_images
        File directory = cw.getDir("temp_images", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, imageName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.parse(mypath.getAbsolutePath());
    }


    public Uri saveImageToInternalStorage(Bitmap bitmapImage, String imageName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/profile_pics
        File directory = cw.getDir("profile_pics", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, imageName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.parse(mypath.getAbsolutePath());
    }

    public Bitmap loadBitmapFromStorage(String path) {
        try {
            File f = new File(path);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteTempImageDirectory() {
        getApplicationContext().deleteFile("temp_images");
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}