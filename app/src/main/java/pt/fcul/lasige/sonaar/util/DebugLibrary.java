package pt.fcul.lasige.sonaar.util;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

public class DebugLibrary {
    public static void logNodeTree(AccessibilityNodeInfo node, int indent) {

        if (node == null) {
            return;
        }
        String indentStr = new String(new char[indent * 3]).replace('\0', ' ');
        Log.d("LOGTREE", "" + String.format("%s NODE: %s", indentStr, node.toString()));
        Log.d("LOGTREE" , String.format("%s CLASS: %s", indentStr, node.getClassName()));
        Log.d("LOGTREE" , String.format("%s PACKAGE: %s", indentStr, node.getPackageName()));

        if (node.getContentDescription() != null || node.getText() != null) {
            Log.d("LOGTREE", String.format("%s DESCRIPTION: %s", indentStr, node.getContentDescription()));
            Log.d("LOGTREE", String.format("%s TEXT: %s", indentStr, node.getText()));

        } else {
            if (node.getParent() != null) {
                Log.d("LOGTREE", String.format("%s DESCRIPTION: %s", indentStr, node.getParent().getContentDescription()));
                Log.d("LOGTREE", String.format("%s TEXT: %s", indentStr, node.getParent().getText()));
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            logNodeTree(node.getChild(i), indent + 1);
        }
        node.recycle();
    }

    public static void logWindows(AccessibilityService service) {
        Log.e("LOGWINDOWS", "-------------------------START-----------------------------");

        for (AccessibilityWindowInfo awi : service.getWindows()) {

            Log.e("LOGWINDOWS", awi.toString());

            if (awi.getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD)
                logNodeTree(awi.getRoot(), 0);

        }

        Log.e("LOGWINDOWS", "-------------------------THE END-----------------------------");
    }
}
