package pt.fcul.lasige.sonaar.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import pt.fcul.lasige.sonaar.overlay.Overlay;

public class NotificationActionsService extends IntentService {
    public static final String COPY_TO_CLIPBOARD = "COPY_TO_CLIPBOARD";
    public static final String START_OVERLAY = "START_OVERLAY";

    public NotificationActionsService() {
        super("NotificationActionsService");
    }

    public NotificationActionsService(String name) {
        super(name);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final String action = intent.getAction();
                if (COPY_TO_CLIPBOARD.equals(action)) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("altText", intent.getStringExtra("altText"));
                    clipboard.setPrimaryClip(clip);
                } else if (START_OVERLAY.equals(action)) {
                    Overlay.getInstance().showAltTextList(intent.getStringArrayListExtra("altTextList"),
                            intent.getStringArrayListExtra("conceptsList"),
                            intent.getStringArrayListExtra("textList"));
                } else {
                    throw new IllegalArgumentException("Unsupported action: " + action);
                }

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                Looper.loop();
            }
        }).start();
    }
}