package com.uhcl.reachapp.common;

public class AppConstants {

    public static final String IMAGE_DIRECTORY = "/demonuts";

    public static final String PHONE_NUMBER_ALREADY_EXIST = "An account with the given phone_number already exists.";

    //Preference keys..
    public static final String MYPREFERENCES = "MyPrefs";
    public static final String PREF_LOGIN_STATUS = "LoginStatus";

    //USERPOOL KEY NAMES..
    public static final String PHONE_NUMBER = "Phone Number";
    public static final String USERNAME = "Preferred Username";
    public static final String PICTURE = "Picture";
    public static final String PHONE_NUMBER_VERIFIED = "Phone Number Verified";
    public static final String PASSWORD = "123456ab";

    //COGNITO USERPOOL ATTRIBUTES..
    public static final String COGNITO_PHONE_NUMBER = "phone_number";
    public static final String COGNITO_USERNAME = "preferred_username";
    public static final String COGNITO_PICTURE = "picture";
    public static final String COGNITO_PHONE_NUMBER_VERIFIED = "phone_number_verified";

    //PREFERENCES...
    public static final String PREFS_NAME = "REACH_PREFS";
    public static final String PREFS_PHONENUMBER = "PhoneNumber";
    public static final String PREFS_USERNAME = "UserName";
    public static final String PREFS_IMAGEPATHURL = "ImagePathURL";
    public static final String PREFS_IMAGE_FIREBASEURL = "FirebaseUrl";
}
