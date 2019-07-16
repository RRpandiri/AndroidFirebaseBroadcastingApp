package com.uhcl.reachapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.data_models.UserPOJO;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import es.dmoral.toasty.Toasty;


public class SignUpUserDetailsActivity extends BaseActivity {

    private final String TAG = SignUpUserDetailsActivity.class.getSimpleName();

    private ImageView ivprofilePicture;
    private EditText etUsername;
    private Button btSave;

    private String enteredUserName = "";
    private Bitmap imageBitmap = null;

    private int GALLERY = 1, CAMERA = 2;

    @Override
    public void initializeclass() {

        RelativeLayout rlUserDeatilsMain = (RelativeLayout) inflater.inflate(R.layout.activity_signup_userdetails, null);
        rlMain.addView(rlUserDeatilsMain);

        initializeVariables();
        validateViews();

        requestMultiplePermissions();

        setImageViewLogic();
        setUserNameSettings();
        continueButtonLogic();
    }

    private void initializeVariables() {
        ivprofilePicture = findViewById(R.id.ivdetails_picture);
        etUsername = findViewById(R.id.etdetails_username);
        btSave = findViewById(R.id.btdetails_save);
    }

    private void validateViews() {
        activateContinueButton(false);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toasty.success(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_LONG, true).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toasty.warning(getApplicationContext(), "Go to Settings to Grant all required permisisons!!", Toast.LENGTH_LONG, true).show();
                            // show alert dialog navigating to Settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toasty.error(getApplicationContext(), "Error in granting the permissions, Go to Settings!! ", Toast.LENGTH_LONG, true).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void setImageViewLogic() {
        ivprofilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            Toasty.error(SignUpUserDetailsActivity.this, "Retake the Image!!", Toast.LENGTH_LONG, true).show();
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    ivprofilePicture.setImageBitmap(bitmap);
                    imageBitmap = bitmap;
                    Toasty.success(SignUpUserDetailsActivity.this, "Image Saved successfully!", Toast.LENGTH_LONG, true).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toasty.error(SignUpUserDetailsActivity.this, "Retake the Image!!", Toast.LENGTH_LONG, true).show();
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                try {
                Uri filePath = data.getData();
                    Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    ivprofilePicture.setImageBitmap(thumbnail);
                    imageBitmap = thumbnail;
//                imagePath = saveImage(thumbnail);
                    Toasty.success(SignUpUserDetailsActivity.this, "Image Saved successfully!", Toast.LENGTH_LONG, true).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toasty.error(SignUpUserDetailsActivity.this, "Retake the Image!!", Toast.LENGTH_LONG, true).show();
                }
            }
        }
    }

    private void activateContinueButton(boolean bool) {
        btSave.setEnabled(bool);
    }

    private void setUserNameSettings() {
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    etUsername.setHint("");
                    enteredUserName = s.toString().trim();
                    activateContinueButton(true);
                } else {
                    etUsername.setHint("enter your username");
                    activateContinueButton(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void continueButtonLogic() {

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressAnimationDialog("Getting Image and UserName...");

                UserPOJO userObj = new UserPOJO();
                userObj.setUsername(enteredUserName);
                if (imageBitmap != null) {

                    userObj.setProfilepic_phoneuri(saveTempImageToInternalStorage(imageBitmap, UUID.randomUUID().toString()).toString());

//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    userObj.setProfilePicByteArray(byteArray);
//                    convertImageToBase64String(imagePath);
                }

//                BaseActivity.preferedUserName = etUsername.getText().toString();
                gotoSignUpPhoneNumberActivity(userObj);
            }
        });
    }

    private void gotoSignUpPhoneNumberActivity(UserPOJO userObj) {
        Intent intent = new Intent(SignUpUserDetailsActivity.this, SignUpActivity.class);
        intent.putExtra("UserObject", userObj);
        closeProgressAnimationDialog();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgressAnimationDialog();
    }
}