package pt.fcul.lasige.sonaar.util;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import pt.fcul.lasige.sonaar.Controller;
import pt.fcul.lasige.sonaar.R;
import pt.fcul.lasige.sonaar.data.Counter;

public class TreeCrawlers {
    public void runNodeTreeFacebook(AccessibilityNodeInfo node, Counter counter, Rect imageBound) {
        if (node == null)
            return;

        if (node.getParent() != null && node.getParent().getParent() != null && node.getClassName() != null && node.getParent().getClassName() != null && node.getParent().getParent().getClassName() != null) {
            if (node.getClassName().toString().contains("android.widget.Button") && node.getContentDescription() == null && node.getParent().getClassName().toString().contains("android.view.ViewGroup") && node.getParent().getParent().getClassName().toString().contains("android.widget.ScrollView")) {
                node.getBoundsInScreen(imageBound);
            }
        }

        if (node.getContentDescription() != null) {
            if (node.getContentDescription().toString().equals("Photo") || node.getContentDescription().toString().equals("Foto")) {
                node.getBoundsInScreen(imageBound);
            } else if (node.getContentDescription().toString().equals("Add More") || node.getContentDescription().toString().equals("Adicionar mais")) {
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Remove Photo") || node.getContentDescription().toString().equals("Remover foto")) {
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Add to your post") || node.getContentDescription().toString().equals("Adicionar à tua publicação")) {
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Edit Photo") || node.getContentDescription().toString().equals("Editar foto")) {
                counter.incPost();
            } else if (node.getContentDescription().toString().equals("Live") || node.getContentDescription().toString().equals("Direto")) {
                counter.incFeed();
            } else if (node.getContentDescription().toString().equals("Photo") || node.getContentDescription().toString().equals("Foto")) {
                counter.incFeed();
            } else if (node.getContentDescription().toString().equals("Room")) {
                counter.incFeed();
            } else if (node.getContentDescription().toString().equals("Edit Alt Text") || node.getContentDescription().toString().equals("Editar o texto alternativo")) {
                counter.incAltText();
            } else if (node.getContentDescription().toString().equals("Add alternative text that describes the contents of the photo for people with visual impairments.") || node.getContentDescription().toString().equals("Adiciona texto alternativo que descreva os conteúdos da foto para pessoas com deficiência visual.")) {
                counter.incAltText();
            } else if (node.getContentDescription().toString().equals("Save") || node.getContentDescription().toString().equals("Guardar")) {
                counter.incAltText();
            }
        }

        if (node.getText() != null) {
            if (node.getText().toString().contains("Say something about this photo") || node.getText().toString().contains("Diz algo sobre esta foto")) {
                counter.incPost();
            } else if (node.getText().toString().equals("Create Post") || node.getText().toString().equals("Criar publicação")) {
                counter.incPost();
            } else if (node.getText().toString().contains("Write photo alternative text") || node.getText().toString().contains("Escreve o texto alternativo da foto")) {
                counter.incAltText();
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
                if (node.getChild(0).getChild(0) == null)
                    node.getChild(0).getBoundsInScreen(imageBound);
                else
                    node.getChild(0).getChild(0).getBoundsInScreen(imageBound);
                counter.incPost();
            } else if (node.getViewIdResourceName().equals("com.twitter.android:id/composer_add_tweet")) {
                counter.incPost();
            } else if (node.getViewIdResourceName().equals("com.twitter.android:id/found_media")) {
                counter.incPost();
            } else if (node.getViewIdResourceName().equals("com.twitter.android:id/gallery")) {
                counter.incPost();
            } else if (node.getViewIdResourceName().equals("com.twitter.android:id/drawer_layout")) {
                counter.incFeed();
            } else if (node.getViewIdResourceName().equals("com.twitter.android:id/composer_write")) {
                counter.incFeed();
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
            } else if (node.getText().toString().equals("Also post to")) {
                counter.incPost();
            } else if (node.getText().toString().equals("Advanced Settings")) {
                counter.incPost();
            } else if (node.getText().toString().equals("New Post")) {
                counter.incPost();
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeInstagram(node.getChild(i), counter);
        }
//        node.recycle();
    }

    public void runNodeTreeForFacebookAltText(AccessibilityNodeInfo node) {
        if (node == null)
            return;

        if (node.getClassName() != null && node.getClassName().toString().contains("android.widget.EditText")) {
            if (Controller.getInstance().getSonaarAltText() != null &&
                    !Controller.getInstance().getSonaarAltText().isEmpty() &&
                    Controller.getInstance().canISetAltText()
            ) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo
                        .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Controller.getInstance().getSonaarAltText() + " " + Controller.getInstance().getString(R.string.alt_text_by_sonaar));
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                Controller.getInstance().setCanISetAltText(false);
            }
            Controller.getInstance().setUserAltText(node.getText().toString());

        }

        for (int i = 0; i < node.getChildCount(); i++) {
            runNodeTreeForFacebookAltText(node.getChild(i));
        }
//        node.recycle();
    }

    public void runNodeTreeForTwitterAltText(AccessibilityNodeInfo node) {
        if (node == null)
            return;

        if (node.getViewIdResourceName() != null) {
            if (node.getViewIdResourceName().equals("com.twitter.android:id/alt_text_edit")) {
                if (Controller.getInstance().getSonaarAltText() != null &&
                        !Controller.getInstance().getSonaarAltText().isEmpty() &&
                        Controller.getInstance().canISetAltText()
                ) {
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Controller.getInstance().getSonaarAltText() + " " + Controller.getInstance().getString(R.string.alt_text_by_sonaar));
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
