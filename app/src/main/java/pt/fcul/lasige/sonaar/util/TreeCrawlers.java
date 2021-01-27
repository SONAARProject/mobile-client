package pt.fcul.lasige.sonaar.util;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import pt.fcul.lasige.sonaar.MediaPostCreationDetector;
import pt.fcul.lasige.sonaar.data.Counter;

public class TreeCrawlers {
    public void runNodeTreeFacebook(AccessibilityNodeInfo node, Counter counter) {
        if (node == null)
            return;

        if (node.getContentDescription() != null) {
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
            if (node.getText().toString().equals("Say something about this photo…")) {
                counter.inc();
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeFacebook(node.getChild(i), counter);
        }
//        node.recycle();
    }

    public void runNodeTreeTwitter(AccessibilityNodeInfo node, Counter counter, Rect imageBound) {
        if (node == null)
            return;

        if (node.getContentDescription() != null) {
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
            if (node.getText().toString().equals("TWEET")) {
                counter.inc();
            }
            if (node.getText().toString().equals("Add a comment…")) {
                counter.inc();
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeTwitter(node.getChild(i), counter, imageBound);
        }
//        node.recycle();
    }

    public void runNodeTreeInstagram(AccessibilityNodeInfo node, Counter counter) {
        if (node == null)
            return;

        if (node.getContentDescription() != null) {
            if (node.getContentDescription().toString().equals("Photo Thumbnail") || node.getContentDescription().toString().equals("Album Thumbnail Preview")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Back")) {
                counter.inc();
            } else if (node.getContentDescription().toString().equals("Share")) {
                counter.inc();
            }
        }
        if (node.getText() != null) {
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

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeInstagram(node.getChild(i), counter);
        }
//        node.recycle();
    }

    public void runNodeTreeForTwitterAltText(AccessibilityNodeInfo node) {
        if (node == null)
            return;

        if (node.getViewIdResourceName() != null) {
            if (node.getViewIdResourceName().equals("com.twitter.android:id/alt_text_edit")) {
                MediaPostCreationDetector.getInstance().setUserAltText(node.getText().toString().substring(0, node.getText().toString().indexOf("\n")));
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeForTwitterAltText(node.getChild(i));
        }
//        node.recycle();
    }
}
