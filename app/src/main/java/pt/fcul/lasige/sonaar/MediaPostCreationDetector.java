package pt.fcul.lasige.sonaar;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.RequiresApi;

import java.io.File;

import pt.fcul.lasige.sonaar.api.APIClient;
import pt.fcul.lasige.sonaar.util.Constants;
import pt.fcul.lasige.sonaar.util.Counter;


public class MediaPostCreationDetector {

    private AccessibilityService service;
    private NotificationController notificationController;
    int c = 0;

    private static MediaPostCreationDetector mediaPostCreationDetector;

    public static MediaPostCreationDetector getInstance(){
        if (mediaPostCreationDetector == null)
            mediaPostCreationDetector = new MediaPostCreationDetector();

        return mediaPostCreationDetector;
    }

    private MediaPostCreationDetector() {}

    public void setService(AccessibilityService service) {
        this.service = service;
        notificationController = new NotificationController(service.getApplicationContext());
    }

    public void logNodeTree(AccessibilityNodeInfo node, int indent) {

        if (node == null) {
            return;
        }
        String indentStr = new String(new char[indent * 3]).replace('\0', ' ');
        Log.d("ARVORE", "" + String.format("%s NODE: %s", indentStr, node.toString()));
        //Log.d("ARVORE" , String.format("%s CLASS: %s", indentStr, node.getClassName()));
        //Log.d("ARVORE" , String.format("%s PACKAGE: %s", indentStr, node.getPackageName()));

        if (node.getContentDescription() != null || node.getText() != null) {
            Log.d("ARVORE", String.format("%s DESCRIPTION: %s", indentStr, node.getContentDescription()));
            Log.d("ARVORE", String.format("%s TEXT: %s", indentStr, node.getText()));

        } else {
            if (node.getParent() != null) {
                Log.d("ARVORE", String.format("%s DESCRIPTION: %s", indentStr, node.getParent().getContentDescription()));
                Log.d("ARVORE", String.format("%s TEXT: %s", indentStr, node.getParent().getText()));
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            logNodeTree(node.getChild(i), indent + 1);
        }
        node.recycle();
    }

    public void logWindows() {
        Log.e("LOGWINDOWS", "-------------------------START-----------------------------");

        for (AccessibilityWindowInfo awi : service.getWindows()) {

            Log.e("LOGWINDOWS", awi.toString());

            if (awi.getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD)
                logNodeTree(awi.getRoot(), 0);

        }

        Log.e("LOGWINDOWS", "-------------------------THE END-----------------------------");
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void detectPostMediaToSocialNetwork(AccessibilityNodeInfo rootInActiveWindow) {
        if(rootInActiveWindow == null)
            return;
        Counter counter = new Counter(0);
        if (rootInActiveWindow.getPackageName().toString().equals("com.facebook.katana")) {
            runNodeTreeFacebook(rootInActiveWindow, counter);
            Log.e("RESULTADO", ": " + counter.getNumber());
            if(counter.getNumber() == Constants.FACEBOOK_COUNTER){
                notificationController.sendNotification("Facebook");
            }
        }else if(rootInActiveWindow.getPackageName().toString().equals("com.twitter.android")){
            Rect imageBound = new Rect();
            runNodeTreeTwitter(rootInActiveWindow, counter, imageBound);

            Log.e("RESULTADO", ": " + counter.getNumber());
            if(counter.getNumber() == Constants.TWITTER_COUNTER){
                notificationController.sendNotification("Twitter");
                if(imageBound.bottom == 0 && imageBound.top == 0 && imageBound.left == 0 && imageBound.right == 0){
                    getScreenshot(-1 , -1, -1, -1);
                }else{

                    getScreenshot(imageBound.left , imageBound.top, //X,Y
                             (imageBound.right - imageBound.left), (imageBound.bottom - imageBound.top));//width, height
                }
            }
        }else if(rootInActiveWindow.getPackageName().toString().equals("com.instagram.android")){
            runNodeTreeInstagram(rootInActiveWindow, counter);
            Log.e("RESULTADO", ": " + counter.getNumber());
            if(counter.getNumber() == Constants.INSTAGRAM_COUNTER){
                notificationController.sendNotification("Instagram");
            }
        }
    }

    public void runNodeTreeFacebook(AccessibilityNodeInfo node, Counter counter) {
        if (node == null)
            return;

        if (node.getContentDescription() != null) {
            Log.d("ARVORE", String.format("DESCRIPTION: %s", node.getContentDescription()));
            if (node.getContentDescription().toString().equals("POST")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("POST")) {
                counter.inc();
            } else if (node.getContentDescription().toString().contains("Choose privacy")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Add album")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Album")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Tap to edit your photo")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Cancel Photo")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Add to your post")) {
                counter.inc();
            }
        }
        if (node.getText() != null) {
            Log.d("ARVORE", String.format("TEXT: %s", node.getText()));
            if (node.getText().toString().equals("Say something about this photo…")) {
                counter.inc();
            }
        }

        Log.e("CONTADOR", ": " + counter.getNumber());
        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeFacebook(node.getChild(i), counter);
        }
        node.recycle();
    }

    public void runNodeTreeTwitter(AccessibilityNodeInfo node, Counter counter, Rect imageBound) {
        if (node == null)
            return;

        if (node.getContentDescription() != null) {
            Log.d("ARVORE", String.format("DESCRIPTION: %s", node.getContentDescription()));
            if (node.getContentDescription().toString().equals("Navigate up")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Attached photo.")) {
                node.getBoundsInScreen(imageBound);
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Add a Tweet")) {
                counter.inc();
            } else if (node.getContentDescription().toString().contains("Tweet length")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Tap to edit your photo")) {
                counter.inc();
            } else if (node.getContentDescription().toString().contains("characters left") || node.getContentDescription().toString().contains("character left")) {
                counter.inc();
            }
        }
        if (node.getText() != null) {
            Log.d("ARVORE", String.format("TEXT: %s", node.getText()));
            if (node.getText().toString().equals("TWEET")) {
                counter.inc();
            }
            if (node.getText().toString().equals("Add a comment…")) {
                counter.inc();
            }
        }

        Log.e("CONTADOR", ": " + counter.getNumber());
        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeTwitter(node.getChild(i), counter, imageBound);
        }
        node.recycle();
    }

    public void runNodeTreeInstagram(AccessibilityNodeInfo node, Counter counter) {
        if (node == null)
            return;

        if (node.getContentDescription() != null) {
            Log.d("ARVORE", String.format("DESCRIPTION: %s", node.getContentDescription()));
            if (node.getContentDescription().toString().equals("Photo Thumbnail") || node.getContentDescription().toString().equals("Album Thumbnail Preview")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Back")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Share")) {
                counter.inc();
            }
        }
        if (node.getText() != null) {
            Log.d("ARVORE", String.format("TEXT: %s", node.getText()));
            if (node.getText().toString().equals("Tag People")) {
                counter.inc();
            } else if (node.getText().toString().equals("Write a caption…")) {
                counter.inc();
            }else if (node.getText().toString().equals("Also post to")) {
                counter.inc();
            }else if (node.getText().toString().equals("Advanced Settings")) {
                counter.inc();
            }else if (node.getText().toString().equals("New Post")) {
                counter.inc();
            }
        }

        Log.e("CONTADOR", ": " + counter.getNumber());
        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeInstagram(node.getChild(i), counter);
        }
        node.recycle();
    }

    public void runNodeTreeForScreenshot(AccessibilityNodeInfo node) {
        if (node == null || node.getPackageName() == null || !node.getPackageName().equals("com.android.systemui"))
            return;

        if (node.getText() != null) {
            Log.d("ARVORE", String.format("TEXT: %s", node.getText()));
            if (node.getText().toString().equals("Start now")) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeForScreenshot(node.getChild(i));
        }
        node.recycle();
    }

    public void goBack(){
        Log.e("CONTADOR", "SWITCHERINO");
//        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        service.getSoftKeyboardController().setShowMode(AccessibilityService.SHOW_MODE_HIDDEN);
                    }
                }, 100);
            }
        }, 150);
    }

    private void getScreenshot(int bitMapCutoutX, int bitMapCutoutY, int bitMapCutoutWidth, int bitMapCutoutHeight){
        if (c > 0)
            return;

        c++;
        Intent i = new Intent(service.getApplicationContext(), ScreenShotCapture.class);
        i.putExtra("bitMapCutoutX", bitMapCutoutX);
        i.putExtra("bitMapCutoutY", bitMapCutoutY);
        i.putExtra("bitMapCutoutWidth", bitMapCutoutWidth);
        i.putExtra("bitMapCutoutHeight", bitMapCutoutHeight);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        service.startActivity(i);
    }

    public void callAPI(){
        APIClient.searchImageFile(new File("/storage/emulated/0/screenshots/myscreen.png"));
    }

    public void callAPI(byte[] bytes){
        APIClient.searchImageFile(bytes);
    }
}
