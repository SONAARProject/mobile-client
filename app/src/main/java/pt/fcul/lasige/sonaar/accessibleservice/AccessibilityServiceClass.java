package pt.fcul.lasige.sonaar.accessibleservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;

import pt.fcul.lasige.sonaar.Controller;

public class AccessibilityServiceClass extends AccessibilityService {

    private Controller lib;

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

        lib = Controller.getInstance();
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
