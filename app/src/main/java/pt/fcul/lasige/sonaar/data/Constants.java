package pt.fcul.lasige.sonaar.data;

import android.os.Environment;

public class Constants {
    public static final boolean PRINT_API_RESPONSE = true;
    public static final int FACEBOOK_POST_COUNTER = 6;
    public static final int FACEBOOK_FEED_COUNTER = 4;
    public static final int FACEBOOK_ALT_TEXT_COUNTER = 4;
    public static final int TWITTER_POST_COUNTER = 4;
    public static final int TWITTER_FEED_COUNTER = 2;
    public static final int INSTAGRAM_COUNTER = 8;
    public static final int FACEBOOK_NOTIFICATION_ID = 12345;
    public static final int TWITTER_NOTIFICATION_ID = 12346;
    public static final int INSTAGRAM_NOTIFICATION_ID = 12347;
    public static final int NONE_NOTIFICATION_ID = 12348;
    public static final String FACEBOOK_PACKAGE = "com.facebook.katana";
    public static final String INSTAGRAM_PACKAGE = "com.instagram.android";
    public static final String TWITTER_PACKAGE = "com.twitter.android";
    public static final String EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String API_BASE_URL = "https://accessible-serv.lasige.di.fc.ul.pt/sonaar/clarifai/"; // change this with your url
}
