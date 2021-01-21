package pt.fcul.lasige.sonaar.accessibleservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import pt.fcul.lasige.sonaar.MediaPostCreationDetector;

public class AccessibilityServiceClass extends AccessibilityService {

    private MediaPostCreationDetector lib;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT |
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS |
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);

        lib = MediaPostCreationDetector.getInstance();
        lib.setService(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            lib.detectPostCreation(getRootInActiveWindow());
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            lib.detectPostSubmission(event.getSource());
        }
    }

    @Override
    public void onInterrupt() {

    }

}
