package pt.fcul.lasige.sonaar.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import pt.fcul.lasige.sonaar.R;
import pt.fcul.lasige.sonaar.api.MessageHandler;
import pt.fcul.lasige.sonaar.data.Constants;


public class NotificationController {

    Context context;
    private static final String CHANNEL_ID = "1904";

    public NotificationController(Context context) {
        this.context = context;
        createChannel();
    }

    public void sendNotification(MessageHandler.SOCIAL_NETWORK socialNetwork, String altText, boolean concepts, ArrayList<String> alts) {

        // Show notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(Constants.NONE_NOTIFICATION_ID, buildNotification(socialNetwork, altText, concepts, alts));

    }

    private Notification buildNotification(MessageHandler.SOCIAL_NETWORK socialNetwork, String altText, boolean concepts, ArrayList<String> alts){

        String text = "";
        switch (socialNetwork){
            case FACEBOOK:
                if(concepts)
                    text = String.format(context.getString(R.string.notification_text_alt_not_found), "Facebook", altText);
                else
                    text = String.format(context.getString(R.string.notification_text), "Facebook", altText, context.getString(R.string.notification_text_facebook));
                break;
            case TWITTER:
                if(concepts)
                    text = String.format(context.getString(R.string.notification_text_alt_not_found), "Twitter", altText);
                else
                    text = String.format(context.getString(R.string.notification_text), "Twitter", altText, context.getString(R.string.notification_text_twitter));
                break;
            case NONE:
                if(concepts)
                    text = String.format(context.getString(R.string.notification_text_alt_not_found_none), altText);
                else
                    text = String.format(context.getString(R.string.notification_text_none), altText);
                break;
        }

        if(!concepts) {
            Intent iAction1 = new Intent(context, NotificationActionsService.class);
            iAction1.setAction(NotificationActionsService.COPY_TO_CLIPBOARD);
            iAction1.putExtra("altText", altText);
            PendingIntent piAction1 = PendingIntent.getService(context, 0, iAction1, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action1 =
                    new NotificationCompat.Action.Builder(
                            0, "Copy to clipboard",piAction1
                    ).build();

            Intent iAction2 = new Intent(context, NotificationActionsService.class);
            iAction2.setAction(NotificationActionsService.START_OVERLAY);
            iAction2.putExtra("altTextList", alts);
            PendingIntent piAction2 = PendingIntent.getService(context, 0, iAction2, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action2 =
                    new NotificationCompat.Action.Builder(
                            0, "Get more alt text", piAction2
                    ).build();

            return new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_notification_foreground)
                        .setContentTitle("Sonaar")
                        .setStyle(new
                                NotificationCompat.BigTextStyle().bigText(
                                text))
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(action1)
                        .addAction(action2)
                        .setAutoCancel(true)
                        .build();

        }else {
            return new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_notification_foreground)
                        .setContentTitle("Sonaar")
                        .setStyle(new
                                NotificationCompat.BigTextStyle().bigText(text))
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build();
        }
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
