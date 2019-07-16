package com.uhcl.reachapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.utilities.NetworkUtils;

public class SplashActivity extends BaseActivity {

    private final String TAG = SplashActivity.class.getSimpleName();

    RelativeLayout rlSplashMain;

    @Override
    public void initializeclass() {

        int SPLASH_TIME_OUT = 2000;
        rlSplashMain = (RelativeLayout) inflater.inflate(R.layout.activity_splash, null);
        rlMain.addView(rlSplashMain);

        if (NetworkUtils.isNetworkConnectionAvailable(SplashActivity.this)) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (isSessionAvaialable()) {
                        launchNextActivity("ChatListMainActivity");
                    } else {
                        launchNextActivity("SignUpActivity");
                    }
                }
            }, SPLASH_TIME_OUT);
        } else
            showAppClosingDialog("NO NETWORK", "Oops!! Come with Internet!");
    }


    private boolean isSessionAvaialable() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void launchNextActivity(String str) {

        Intent intent = null;
        if (str.equalsIgnoreCase("ChatListMainActivity"))
            intent = new Intent(SplashActivity.this, ChatListMainActivity.class);
        else if (str.equalsIgnoreCase("SignUpActivity"))
            intent = new Intent(SplashActivity.this, SignUpUserDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}