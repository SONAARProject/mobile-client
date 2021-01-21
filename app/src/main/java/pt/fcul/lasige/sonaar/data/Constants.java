package pt.fcul.lasige.sonaar.data;

import android.os.Environment;

public class Constants {
    public static final boolean PRINT_API_RESPONSE = true;
    public static final int FACEBOOK_COUNTER = 9;
    public static final int TWITTER_COUNTER = 7;
    public static final int INSTAGRAM_COUNTER = 8;
    public static final int FACEBOOK_NOTIFICATION_ID = 12345;
    public static final int TWITTER_NOTIFICATION_ID = 12346;
    public static final int INSTAGRAM_NOTIFICATION_ID = 12347;
    public static final int SCREENSHOT_COOL_DOWN = 60 * 1000; //60 seconds
    public static final String FACEBOOK_PACKAGE = "com.facebook.katana";
    public static final String INSTAGRAM_PACKAGE = "com.instagram.android";
    public static final String TWITTER_PACKAGE = "com.twitter.android";
    public static final String EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();

}
