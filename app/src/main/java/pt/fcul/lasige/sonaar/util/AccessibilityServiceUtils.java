package pt.fcul.lasige.sonaar.util;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;

public class AccessibilityServiceUtils {
    public static void goBack(AccessibilityService service){
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideKeyboard(service);
                    }
                }, 100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showKeyboard(service);
                    }
                }, 500);
            }
        }, 150);
    }

    public static void hideKeyboard(AccessibilityService service){
        service.getSoftKeyboardController().setShowMode(AccessibilityService.SHOW_MODE_HIDDEN);
    }

    public static void showKeyboard(AccessibilityService service){
        service.getSoftKeyboardController().setShowMode(AccessibilityService.SHOW_MODE_AUTO);
    }

    public static void takeScreenshot(AccessibilityService service){
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);

    }
}
