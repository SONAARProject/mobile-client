package pt.fcul.lasige.sonaar.util;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import pt.fcul.lasige.sonaar.Controller;
import pt.fcul.lasige.sonaar.data.Counter;

public class TreeCrawlers {
    public void runNodeTreeFacebook(AccessibilityNodeInfo node, Counter counter, Rect imageBound) {
        if (node == null)
            return;

        if (node.getContentDescription() != null) {
            if (node.getContentDescription().toString().equals("Photo") || node.getContentDescription().toString().equals("Foto")) {
                node.getBoundsInScreen(imageBound);
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Tap to edit your photo") || node.getContentDescription().toString().equals("Toca para editares a tua foto")) {
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Cancel Photo") || node.getContentDescription().toString().equals("Remover foto")) {
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Add to your post") || node.getContentDescription().toString().equals("Adicionar à tua publicação")) {
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Live") || node.getContentDescription().toString().equals("Direto")) {
                counter.incFeed();
            } else if (node.getContentDescription().toString().equals("Photo") || node.getContentDescription().toString().equals("Foto")) {
                counter.incFeed();
            } else if (node.getContentDescription().toString().equals("Room")) {
                counter.incFeed();
            }
        }
        if (node.getText() != null) {
            Log.d("PTPTPTPT", node.getText().toString());
            if (node.getText().toString().equals("Say something about this photo…") || node.getText().toString().equals("Diz algo sobre esta foto..")) {
                counter.incPost();
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeFacebook(node.getChild(i), counter, imageBound);
        }
//        node.recycle();
    }

    public void runNodeTreeTwitter(AccessibilityNodeInfo node, Counter counter, Rect imageBound) {
        if (node == null)
            return;

        if (node.getViewIdResourceName() != null) {
            if (node.getViewIdResourceName().equals("com.twitter.android:id/media_attachments")) {
                node.getChild(0).getChild(0).getBoundsInScreen(imageBound);
                counter.incPost();
            }else if (node.getViewIdResourceName().equals("com.twitter.android:id/composer_add_tweet")) {
                counter.incPost();
            }else if (node.getViewIdResourceName().equals("com.twitter.android:id/found_media")) {
                counter.incPost();
            }else if (node.getViewIdResourceName().equals("com.twitter.android:id/gallery")) {
                counter.incPost();
            }else if (node.getViewIdResourceName().equals("com.twitter.android:id/drawer_layout")) {
                counter.incFeed();
            }else if (node.getViewIdResourceName().equals("com.twitter.android:id/composer_write")) {
                counter.incFeed();
            }
        }
//        if (node.getContentDescription() != null) {
//            if (node.getContentDescription().toString().equals("Navigate up")) {
//                counter.inc();
//            } else if (node.getContentDescription().toString().equals("Attached photo.")) {
//                node.getBoundsInScreen(imageBound);
//                counter.inc();
//            } else if (node.getContentDescription().toString().equals("Add a Tweet")) {
//                counter.inc();
//            } else if (node.getContentDescription().toString().contains("Tweet length")) {
//                counter.inc();
//            } else if (node.getContentDescription().toString().equals("Tap to edit your photo")) {
//                counter.inc();
//            } else if (node.getContentDescription().toString().contains("characters left") || node.getContentDescription().toString().contains("character left")) {
//                counter.inc();
//            }
//        }
//        if (node.getText() != null) {
//            if (node.getText().toString().equals("TWEET")) {
//                counter.inc();
//            }
//            if (node.getText().toString().equals("Add a comment…")) {
//                counter.inc();
//            }
//        }

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
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Back")) {
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Share")) {
                counter.incPost();
            }
        }
        if (node.getText() != null) {
            if (node.getText().toString().equals("Tag People")) {
                counter.incPost();
            } else if (node.getText().toString().equals("Write a caption…")) {
                counter.incPost();
            }else if (node.getText().toString().equals("Also post to")) {
                counter.incPost();
            }else if (node.getText().toString().equals("Advanced Settings")) {
                counter.incPost();
            }else if (node.getText().toString().equals("New Post")) {
                counter.incPost();
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
                if(Controller.getInstance().getSonaarAltText() != null &&
                        !Controller.getInstance().getSonaarAltText().isEmpty() &&
                        Controller.getInstance().canISetAltText()
                ){
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Controller.getInstance().getSonaarAltText());
                    node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    Controller.getInstance().setCanISetAltText(false);
                }
                Controller.getInstance().setUserAltText(node.getText().toString().substring(0, node.getText().toString().indexOf("\n")));
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeForTwitterAltText(node.getChild(i));
        }
//        node.recycle();
    }

    public void runNodeTreeForTwitterPostText(AccessibilityNodeInfo node) {
        if (node == null)
            return;

        if (node.getViewIdResourceName() != null) {
            if (node.getViewIdResourceName().equals("com.twitter.android:id/tweet_text")) {
                Controller.getInstance().setPostText(node.getText().toString());
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeForTwitterPostText(node.getChild(i));
        }
//        node.recycle();
    }
}
