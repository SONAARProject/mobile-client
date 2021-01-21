package pt.fcul.lasige.sonaar;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import pt.fcul.lasige.sonaar.api.APIClient;
import pt.fcul.lasige.sonaar.api.APIMessageHandler;
import pt.fcul.lasige.sonaar.util.AccessibilityServiceUtils;
import pt.fcul.lasige.sonaar.data.Constants;
import pt.fcul.lasige.sonaar.data.Counter;
import pt.fcul.lasige.sonaar.util.ImageUtils;
import pt.fcul.lasige.sonaar.util.TreeCrawlers;


public class MediaPostCreationDetector {

    private boolean canITakeScreenshot = true;
    private boolean sendAltTextToAPI = false;
    private AccessibilityService service;
    private NotificationController notificationController;
    private APIMessageHandler messageHandler;
    private byte[] currentImage;
    private String altText;
    private TreeCrawlers treeCrawlers;

    private static MediaPostCreationDetector mediaPostCreationDetector;

    public static MediaPostCreationDetector getInstance(){
        if (mediaPostCreationDetector == null)
            mediaPostCreationDetector = new MediaPostCreationDetector();

        return mediaPostCreationDetector;
    }

    private MediaPostCreationDetector() { }

    public void setService(AccessibilityService service) {
        this.service = service;
        notificationController = new NotificationController(service.getApplicationContext());
        treeCrawlers = new TreeCrawlers();
        messageHandler = new APIMessageHandler(notificationController);
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public void setSendAltTextToAPI(boolean sendAltTextToAPI) {
        this.sendAltTextToAPI = sendAltTextToAPI;
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
                    if(sendAltTextToAPI && currentImage != null) {
                        APIClient.insertImageAndAltText(currentImage, altText);
                    }
                    cleanVariables();
                }
                break;
        }
    }

    public void detectPostCreation(AccessibilityNodeInfo rootInActiveWindow) {
        if(rootInActiveWindow == null)
            return;
        Counter counter = new Counter(0);
        switch (rootInActiveWindow.getPackageName().toString()){
            case Constants.FACEBOOK_PACKAGE:
                //TODO IMPLEMENT
                treeCrawlers.runNodeTreeFacebook(rootInActiveWindow, counter);
                analyseTreeRun(Constants.FACEBOOK_PACKAGE, counter, null);
                break;
            case Constants.INSTAGRAM_PACKAGE:
                //TODO IMPLEMENT
                treeCrawlers.runNodeTreeInstagram(rootInActiveWindow, counter);
                analyseTreeRun(Constants.INSTAGRAM_PACKAGE, counter, null);
                break;
            case Constants.TWITTER_PACKAGE:
                //get the View size to crop the screenshot
                Rect imageBound = new Rect();
                treeCrawlers.runNodeTreeTwitter(rootInActiveWindow, counter, imageBound);
                treeCrawlers.runNodeTreeForTwitterAltText(rootInActiveWindow);
                analyseTreeRun(Constants.TWITTER_PACKAGE, counter, imageBound);

                break;
        }
    }

    private void analyseTreeRun(String appRun, Counter counter, Rect imageBound){

        switch (appRun){
            case Constants.FACEBOOK_PACKAGE:
                //TODO
                break;

            case Constants.INSTAGRAM_PACKAGE:
                //TODO
                break;

            case Constants.TWITTER_PACKAGE:
                if(counter.getNumber() == Constants.TWITTER_COUNTER){
                    if(imageBound.bottom == 0 && imageBound.top == 0 && imageBound.left == 0 && imageBound.right == 0){
                        takeScreenshot(-1 , -1, -1, -1);
                    }else{
                        takeScreenshot(imageBound.left , imageBound.top, //X,Y
                                (imageBound.right - imageBound.left), (imageBound.bottom - imageBound.top));//width, height
                    }
                }
                break;
        }
    }

    private void takeScreenshot(int bitMapCutoutX, int bitMapCutoutY, int bitMapCutoutWidth, int bitMapCutoutHeight){

        if(!canITakeScreenshot || altText != null)
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
                    bitMapCutoutX,
                    bitMapCutoutY,
                    bitMapCutoutWidth,
                    bitMapCutoutHeight);

            APIClient.searchImageFile(
                    currentImage,
                    messageHandler);

        }, 5000);
    }

    private void cleanVariables() {
        currentImage = null;
        altText = null;
        canITakeScreenshot = true;
    }

    private void startScreenshotCoolDown(){

        canITakeScreenshot = false;

        new Handler().postDelayed(() -> canITakeScreenshot = true, Constants.SCREENSHOT_COOL_DOWN);
    }

}
