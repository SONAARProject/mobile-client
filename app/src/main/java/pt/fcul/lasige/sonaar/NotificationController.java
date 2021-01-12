package pt.fcul.lasige.sonaar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import pt.fcul.lasige.sonaar.util.Constants;

public class NotificationController {

    Context context;
    private static final String CHANNEL_ID = "1904";
    private static final int NOTIFICATION_COOL_DOWN = 2000; //ms
    private boolean canISendNotifications = true;

    public NotificationController(Context context) {
        this.context = context;
        createChannel();
    }

    public void sendNotification(String socialNetworkName) {

        // Show notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        switch (socialNetworkName){
            case "Facebook":
                if(!isNotificationLive(Constants.FACEBOOK_NOTIFICATION_ID)) {
                    if(!canISendNotifications)
                        return;

                    manager.notify(Constants.FACEBOOK_NOTIFICATION_ID, buildNotification(socialNetworkName));
                    coolDownNotifications();
                }
            case "Twitter":
                if(!isNotificationLive(Constants.TWITTER_NOTIFICATION_ID)) {
                    if(!canISendNotifications)
                        return;

                    manager.notify(Constants.TWITTER_NOTIFICATION_ID, buildNotification(socialNetworkName));
                    coolDownNotifications();
                }
            case "Instagram":
                if(!isNotificationLive(Constants.INSTAGRAM_NOTIFICATION_ID)) {
                    if(!canISendNotifications)
                        return;

                    manager.notify(Constants.INSTAGRAM_NOTIFICATION_ID, buildNotification(socialNetworkName));
                    coolDownNotifications();
                }
        }
    }

    private Notification buildNotification(String socialNetworkName){
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Sonaar")
                .setStyle(new
                        NotificationCompat.BigTextStyle().bigText("We detected that you are posting a image to " + socialNetworkName +
                        " please consider add an alternative text to your image post. Thank you :)"))
                .setContentText("We detected that you are posting a image to " + socialNetworkName +
                        " please consider add an alternative text to your image post. Thank you :)")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    private void coolDownNotifications(){

        canISendNotifications = false;

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                canISendNotifications = true;
            }
        }, NOTIFICATION_COOL_DOWN);
    }

    private boolean isNotificationLive(int id){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == id) {
                return true;
            }
        }

        return false;
    }

    public void cancelNotifications(){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void createChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(CHANNEL_ID, "Tasks", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            mChannel.setDescription("This channel shows notifications for remind you to add a alt text to your media");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);

            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}
