package pt.fcul.lasige.sonaar.accessibleservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import pt.fcul.lasige.sonaar.MediaPostCreationDetector;

public class AccessibilityServiceClass extends AccessibilityService {

    private WindowManager windowManager;
    private ViewGroup initialView;
    private LayoutInflater li;
    private MediaPostCreationDetector lib;
    private int index = 0;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v("SERVICE", "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT |
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS |
                AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY |
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);

        this.li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        lib = MediaPostCreationDetector.getInstance();
        lib.setService(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                lib.detectPostMediaToSocialNetwork(getRootInActiveWindow());
                lib.runNodeTreeForScreenshot(getRootInActiveWindow());
                break;
        }

    }

    @Override
    public void onInterrupt() {

    }

}
