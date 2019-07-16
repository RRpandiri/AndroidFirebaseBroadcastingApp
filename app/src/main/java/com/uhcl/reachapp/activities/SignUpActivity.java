package com.uhcl.reachapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.uhcl.reachapp.R;
import com.uhcl.reachapp.data_models.UserPOJO;
import com.uhcl.reachapp.misc.CountriesExtension;


public class SignUpActivity extends BaseActivity {

    private final String TAG = SignUpActivity.class.getSimpleName();

    private Spinner spCountryCodes;
    private EditText etExtension, etPhoneNumber;
    private Button btContinue;

    private String enteredPhoneNumber;
    private UserPOJO userObject;

    @Override
    public void initializeclass() {

        RelativeLayout rlLoginMain = (RelativeLayout) inflater.inflate(R.layout.activity_signup_phonenumber, null);
        rlMain.addView(rlLoginMain);

        initializeVariables();
        validateViews();

        setSpinnerSettings();
        setPhonenumberSettings();
//        requestMultiplePermissions();
        continueButtonLogic();
    }

    private void initializeVariables() {
        spCountryCodes = findViewById(R.id.splogin_countrycode);
        etPhoneNumber = findViewById(R.id.etlogin_phonenumber);
        etExtension = findViewById(R.id.etlogin_extension);
        btContinue = findViewById(R.id.btlogin_continue);

        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, R.layout.login_spinner_textview, CountriesExtension.countryNames);
        spCountryCodes.setAdapter(spAdapter);

        //Populating UserAttributes..
        enteredPhoneNumber = null;
        userObject = new UserPOJO();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            userObject = (UserPOJO) extras.get("UserObject");
    }


    private void validateViews() {
        etExtension.setEnabled(false);
        activateContinueButton(false);
    }

    private void activateContinueButton(boolean bool) {
        btContinue.setEnabled(bool);
    }

    private void setSpinnerSettings() {
        spCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String strExytensionCode = CountriesExtension.countryAreaCodes[position];
                    etExtension.setText(strExytensionCode);
                } else
                    etExtension.setText("+");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setPhonenumberSettings() {
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 10) {
//                    BaseActivity.phoneNumber = etExtension.getText().toString() + "" + etPhoneNumber.getText().toString();
                    enteredPhoneNumber = etExtension.getText().toString() + "" + s.toString().trim();
                    hideKeyboard();
                    activateContinueButton(true);
                } else
                    activateContinueButton(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void continueButtonLogic() {

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSignUpConfirmActivity();
            }
        });
    }

//
//    private void requestMultiplePermissions() {
//        Dexter.withActivity(this)
//                .withPermissions(
//                        Manifest.permission.READ_CONTACTS,
//                        Manifest.permission.WRITE_CONTACTS,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        // check if all permissions are granted
//                        if (report.areAllPermissionsGranted()) {
//                            Toasty.success(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_LONG, true).show();
//                        }
//
//                        // check for permanent denial of any permission
//                        if (report.isAnyPermissionPermanentlyDenied()) {
//                            Toasty.warning(getApplicationContext(), "Go to Settings to Grant all required permisisons!!", Toast.LENGTH_LONG, true).show();
//                            // show alert dialog navigating to Settings
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).
//                withErrorListener(new PermissionRequestErrorListener() {
//                    @Override
//                    public void onError(DexterError error) {
//                        Toasty.error(getApplicationContext(), "Error in granting the permissions, Go to Settings!! ", Toast.LENGTH_LONG, true).show();
//                    }
//                })
//                .onSameThread()
//                .check();
//    }

    private void launchSignUpConfirmActivity() {
        userObject.setPhonenumber(enteredPhoneNumber);
        Intent intent = new Intent(SignUpActivity.this, SignUpConfirmActivity.class);
        intent.putExtra("UserObject", userObject);
//        intent.putExtra(AppConstants.PHONE_NUMBER, etExtension.getText().toString() + "" + etPhoneNumber.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgressAnimationDialog();
    }
}