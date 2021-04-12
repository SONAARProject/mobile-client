package pt.fcul.lasige.sonaar;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import pt.fcul.lasige.sonaar.api.APIClient;
import pt.fcul.lasige.sonaar.api.APIMessageHandler;
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
    private APIMessageHandler messageHandler;
    private byte[] currentImage;
    private String userAltText;
    private String sonaarAltText;
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
        messageHandler = new APIMessageHandler(notificationController);
        Overlay.getInstance().setContext(service);
    }

    public void setUserAltText(String userAltText) {
        this.userAltText = userAltText;
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
                source.getPackageName() == null ||
                source.getClassName() == null ||
                source.getText() == null)
            return;

        switch (source.getPackageName().toString()){
            case Constants.FACEBOOK_PACKAGE:
                //TODO IMPLEMENT
                break;
            case Constants.INSTAGRAM_PACKAGE:
                //TODO IMPLEMENT
                break;
            case Constants.TWITTER_PACKAGE:
                if(source.getClassName().equals("android.widget.Button") && source.getText().equals("TWEET")){
                    if(userAltText != null && !userAltText.equals(sonaarAltText) && currentImage != null) {
                        APIClient.insertImageAndAltText(currentImage, userAltText);
                    }
                    cleanVariables();
                }
                break;
        }
    }

    public void detectPostCreation(AccessibilityNodeInfo rootInActiveWindow) {
        if(rootInActiveWindow == null)
            return;
        Counter counter = new Counter(0, 0);
        Rect imageBound = new Rect();
        switch (rootInActiveWindow.getPackageName().toString()){
            case Constants.FACEBOOK_PACKAGE:
                //TODO IMPLEMENT
                treeCrawlers.runNodeTreeFacebook(rootInActiveWindow, counter, imageBound);
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
                analyseTreeRun(Constants.TWITTER_PACKAGE, counter, imageBound);

                break;
        }
    }

    private void analyseTreeRun(String appRun, Counter counter, Rect imageBound){

        switch (appRun){
            case Constants.FACEBOOK_PACKAGE:

                Log.d("FEED", " " + counter.getFeed());
                if(counter.getFeed() == Constants.FACEBOOK_FEED_COUNTER){
                    cleanVariables();
                }

                if(counter.getPost() == Constants.FACEBOOK_POST_COUNTER){
                    if(imageBound.bottom == 0 && imageBound.top == 0 && imageBound.left == 0 && imageBound.right == 0){
                        takeScreenshot(-1 , -1, -1, -1, APIMessageHandler.SOCIAL_NETWORK.FACEBOOK);
                    }else{
                        takeScreenshot(imageBound.left , imageBound.top, //X,Y
                                (imageBound.right - imageBound.left), (imageBound.bottom - imageBound.top), APIMessageHandler.SOCIAL_NETWORK.FACEBOOK);//width, height
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
                        takeScreenshot(-1 , -1, -1, -1, APIMessageHandler.SOCIAL_NETWORK.TWITTER);
                    }else{
                        takeScreenshot(imageBound.left , imageBound.top, //X,Y
                                (imageBound.right - imageBound.left), (imageBound.bottom - imageBound.top), APIMessageHandler.SOCIAL_NETWORK.TWITTER);//width, height
                    }
                }
                break;
        }
    }

    private void takeScreenshot(int bitMapCutoutX, int bitMapCutoutY, int bitMapCutoutWidth, int bitMapCutoutHeight, APIMessageHandler.SOCIAL_NETWORK socialNetwork){

        if(!canITakeScreenshot)
            return;

        startScreenshotCoolDown();

        // hide the soft keyboard if is showing
        AccessibilityServiceUtils.hideKeyboard(service);
        new Handler().postDelayed(() -> {
            //give time to the keyboard disappear
            //and take the screenshot
            AccessibilityServiceUtils.takeScreenshot(service);

            // activate back the keyboard
            AccessibilityServiceUtils.showKeyboard(service);
        }, 1000);
        new Handler().postDelayed(() -> {

            //give time to the screenshot to be written to disk
            //crop the image and send the encoded image to our backend
            //for searching an alt text
            currentImage = ImageUtils.getImageToAPI(
                    service,
                    bitMapCutoutX,
                    bitMapCutoutY,
                    bitMapCutoutWidth,
                    bitMapCutoutHeight);

            APIClient.searchImageFile(
                    currentImage,
                    messageHandler,
                    socialNetwork);

        }, 5000);
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

}
