package pt.fcul.lasige.sonaar;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import pt.fcul.lasige.sonaar.api.APIClient;
import pt.fcul.lasige.sonaar.api.MessageHandler;
import pt.fcul.lasige.sonaar.notifications.NotificationController;
import pt.fcul.lasige.sonaar.overlay.Overlay;
import pt.fcul.lasige.sonaar.util.AccessibilityServiceUtils;
import pt.fcul.lasige.sonaar.data.Constants;
import pt.fcul.lasige.sonaar.data.Counter;
import pt.fcul.lasige.sonaar.util.ImageUtils;
import pt.fcul.lasige.sonaar.util.TreeCrawlers;


public class Controller {

    private boolean canITakeScreenshot = true;
    private boolean canISetAltText = true;
    private AccessibilityService service;
    private NotificationController notificationController;
    private MessageHandler messageHandler;
    private byte[] currentImage;
    private String userAltText;
    private String sonaarAltText;
    private String postText = "";
    private TreeCrawlers treeCrawlers;

    private static Controller controller;

    public static Controller getInstance(){
        if (controller == null)
            controller = new Controller();

        return controller;
    }

    private Controller() { }

    public void setService(AccessibilityService service) {
        this.service = service;
        notificationController = new NotificationController(service.getApplicationContext());
        treeCrawlers = new TreeCrawlers();
        messageHandler = new MessageHandler(notificationController);
        Overlay.getInstance().setContext(service);
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setUserAltText(String userAltText) {
        this.userAltText = userAltText.replace(getString(R.string.alt_text_by_sonaar), "").trim();
    }

    public void setCanISetAltText(boolean canISetAltText) {
        this.canISetAltText = canISetAltText;
    }

    public boolean canISetAltText() {
        return canISetAltText;
    }

    public void setSonaarAltText(String sonaarAltText) {
        this.sonaarAltText = sonaarAltText;
    }

    public String getUserAltText() {
        return userAltText;
    }

    public String getSonaarAltText() {
        return sonaarAltText;
    }

    public void detectPostSubmission(AccessibilityNodeInfo source) {
        if(source == null ||
                source.getPackageName() == null)
            return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(service.getApplicationContext());
        String uuid = prefs.getString(getString(R.string.uuid), "null");

        switch (source.getPackageName().toString()){
            case Constants.FACEBOOK_PACKAGE:
                if(source.getClassName().toString().contains("android.widget.Button") && (source.getContentDescription().toString().equals("Guardar") || source.getContentDescription().toString().equals("Save"))){
                    if(userAltText != null && !userAltText.equals(sonaarAltText) && currentImage != null) {
                        APIClient.insertImageAndAltText(currentImage, userAltText, "", MessageHandler.SOCIAL_NETWORK.FACEBOOK, uuid);
                    }
                    cleanVariables();
                }
                break;
            case Constants.INSTAGRAM_PACKAGE:
                //TODO IMPLEMENT
                break;
            case Constants.TWITTER_PACKAGE:
                if (source.getClassName() == null || source.getText() == null)
                    return;
                if(source.getClassName().equals("android.widget.Button") && source.getText().equals("TWEET")){
                    if(userAltText != null && !userAltText.equals(sonaarAltText) && currentImage != null) {
                        APIClient.insertImageAndAltText(currentImage, userAltText, postText, MessageHandler.SOCIAL_NETWORK.TWITTER, uuid);
                    }
                    cleanVariables();
                }
                break;
        }
    }

    public void detectPostCreation(AccessibilityNodeInfo rootInActiveWindow) {
        if(rootInActiveWindow == null)
            return;
        Counter counter = new Counter(0, 0, 0);
        Rect imageBound = new Rect();

        switch (rootInActiveWindow.getPackageName().toString()){
            case Constants.FACEBOOK_PACKAGE:
                //TODO IMPLEMENT
                treeCrawlers.runNodeTreeFacebook(rootInActiveWindow, counter, imageBound);
                treeCrawlers.runNodeTreeForFacebookAltText(rootInActiveWindow);
                analyseTreeRun(Constants.FACEBOOK_PACKAGE, counter, imageBound);
                break;
            case Constants.INSTAGRAM_PACKAGE:
                //TODO IMPLEMENT
                treeCrawlers.runNodeTreeInstagram(rootInActiveWindow, counter);
                analyseTreeRun(Constants.INSTAGRAM_PACKAGE, counter, null);
                break;
            case Constants.TWITTER_PACKAGE:
                //get the View size to crop the screenshot
                treeCrawlers.runNodeTreeTwitter(rootInActiveWindow, counter, imageBound);
                treeCrawlers.runNodeTreeForTwitterAltText(rootInActiveWindow);
                treeCrawlers.runNodeTreeForTwitterPostText(rootInActiveWindow);
                analyseTreeRun(Constants.TWITTER_PACKAGE, counter, imageBound);

                break;
        }
    }

    private void analyseTreeRun(String appRun, Counter counter, Rect imageBound){

        switch (appRun){
            case Constants.FACEBOOK_PACKAGE:

                if(counter.getFeed() == Constants.FACEBOOK_FEED_COUNTER){
                    cleanVariables();
                }

                if(counter.getPost() == Constants.FACEBOOK_POST_COUNTER){
                    if(imageBound.bottom == 0 && imageBound.top == 0 && imageBound.left == 0 && imageBound.right == 0){
                        takeScreenshot(-1 , -1, -1, -1, MessageHandler.SOCIAL_NETWORK.FACEBOOK);
                    }else{
                        takeScreenshot(imageBound.left , imageBound.top, //X,Y
                                (imageBound.right - imageBound.left), (imageBound.bottom - imageBound.top), MessageHandler.SOCIAL_NETWORK.FACEBOOK);//width, height
                    }
                }

                if(counter.getAltText() == Constants.FACEBOOK_ALT_TEXT_COUNTER){
                    if(imageBound.bottom == 0 && imageBound.top == 0 && imageBound.left == 0 && imageBound.right == 0){
                        takeScreenshotNoSend(-1 , -1, -1, -1);
                    }else{
                        takeScreenshotNoSend(imageBound.left , imageBound.top, //X,Y
                                (imageBound.right - imageBound.left), (imageBound.bottom - imageBound.top));//width, height
                    }
                }

                break;

            case Constants.INSTAGRAM_PACKAGE:
                //TODO
                break;

            case Constants.TWITTER_PACKAGE:

                if(counter.getFeed() == Constants.TWITTER_FEED_COUNTER){
                    cleanVariables();
                }

                if(counter.getPost() == Constants.TWITTER_POST_COUNTER){
                    if(imageBound.bottom == 0 && imageBound.top == 0 && imageBound.left == 0 && imageBound.right == 0){
                        takeScreenshot(-1 , -1, -1, -1, MessageHandler.SOCIAL_NETWORK.TWITTER);
                    }else{
                        takeScreenshot(imageBound.left , imageBound.top, //X,Y
                                (imageBound.right - imageBound.left), (imageBound.bottom - imageBound.top), MessageHandler.SOCIAL_NETWORK.TWITTER);//width, height
                    }
                }
                break;
        }
    }

    private void takeScreenshot(int bitMapCutoutX, int bitMapCutoutY, int bitMapCutoutWidth, int bitMapCutoutHeight, MessageHandler.SOCIAL_NETWORK socialNetwork){

        if(!canITakeScreenshot)
            return;

        startScreenshotCoolDown();

        Overlay.getInstance().showScreensShotCountDown(3);
        // hide the soft keyboard if is showing
        AccessibilityServiceUtils.hideKeyboard(service);

        new CountDownTimer(1500, 500) {

            public void onTick(long millisUntilFinished) {
                Overlay.getInstance().updateScreensShotCountDown();
            }

            public void onFinish() {
                Overlay.getInstance().removeScreensShotCountDown();
                new Handler().postDelayed(() -> {
                    //give time to the keyboard disappear
                    //and take the screenshot
                    AccessibilityServiceUtils.takeScreenshot(service);

                    // activate back the keyboard
                    AccessibilityServiceUtils.showKeyboard(service);

                    Overlay.getInstance().showApiCall();
                    new CountDownTimer(7500, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            Overlay.getInstance().removeApiCall();
                            //give time to the screenshot to be written to disk
                            //crop the image and send the encoded image to our backend
                            //for searching an alt text
                            currentImage = ImageUtils.getImageToAPI(
                                    service,
                                    bitMapCutoutX,
                                    bitMapCutoutY,
                                    bitMapCutoutWidth,
                                    bitMapCutoutHeight);

                            if (currentImage == null){
                                Toast.makeText(service, service.getString(R.string.error_read_screenshot), Toast.LENGTH_SHORT).show();
                            }else {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(service.getApplicationContext());
                                String uuid = prefs.getString(getString(R.string.uuid), "null");
                                APIClient.searchImageFile(
                                        currentImage,
                                        messageHandler,
                                        socialNetwork,
                                        "suggestion",
                                        uuid);
                            }
                        }

                    }.start();

                }, 1500);
            }

        }.start();

    }

    private void takeScreenshotNoSend(int bitMapCutoutX, int bitMapCutoutY, int bitMapCutoutWidth, int bitMapCutoutHeight){

        if(!canITakeScreenshot)
            return;

        startScreenshotCoolDown();

        Overlay.getInstance().showScreensShotCountDown(3);
        // hide the soft keyboard if is showing
        AccessibilityServiceUtils.hideKeyboard(service);

        new CountDownTimer(1500, 500) {

            public void onTick(long millisUntilFinished) {
                Overlay.getInstance().updateScreensShotCountDown();
            }

            public void onFinish() {
                Overlay.getInstance().removeScreensShotCountDown();
                new Handler().postDelayed(() -> {
                    //give time to the keyboard disappear
                    //and take the screenshot
                    AccessibilityServiceUtils.takeScreenshot(service);

                    // activate back the keyboard
                    AccessibilityServiceUtils.showKeyboard(service);

                    Overlay.getInstance().showApiCall();
                    new CountDownTimer(5000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            Overlay.getInstance().removeApiCall();
                            //give time to the screenshot to be written to disk
                            //crop the image and send the encoded image to our backend
                            //for searching an alt text
                            currentImage = ImageUtils.getImageToAPI(
                                    service,
                                    bitMapCutoutX,
                                    bitMapCutoutY,
                                    bitMapCutoutWidth,
                                    bitMapCutoutHeight);
                        }

                    }.start();
                }, 500);
            }

        }.start();

    }

    private void cleanVariables() {
        currentImage = null;
        userAltText = null;
        canITakeScreenshot = true;
        canISetAltText = true;
    }

    private void startScreenshotCoolDown(){
        canITakeScreenshot = false;
    }

    public String getString(int resId) {
        return service.getString(resId);
    }
}
