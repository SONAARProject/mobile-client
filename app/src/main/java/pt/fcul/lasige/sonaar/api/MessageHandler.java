package pt.fcul.lasige.sonaar.api;

import android.util.Log;

import java.util.ArrayList;

import pt.fcul.lasige.sonaar.notifications.NotificationController;
import pt.fcul.lasige.sonaar.api.pojo.Message;
import pt.fcul.lasige.sonaar.data.Constants;

public class MessageHandler implements IMessageHandler {

    private NotificationController notificationController;

    public enum SOCIAL_NETWORK{TWITTER, FACEBOOK, INSTAGRAM, NONE}

    public MessageHandler(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public void onSearchResponseMessage(Message message, SOCIAL_NETWORK socialNetwork){

        if(message != null){
            if(Constants.PRINT_API_RESPONSE){
                Log.d("APIRESPONSE", String.format("message: %s", message.message));
                Log.d("APIRESPONSE", String.format("status: %s", message.status));
                Log.d("APIRESPONSE", String.format("alts: %s", message.alts));
                Log.d("APIRESPONSE", String.format("concepts: %s", message.concepts));
                Log.d("APIRESPONSE", String.format("text: %s", message.text));
            }

            ArrayList<String> altsList = message.getAltsList();
            ArrayList<String> conceptsList = message.getConceptsList();
            ArrayList<String> textList = message.getTextList();

            notificationController.sendNotification(socialNetwork, altsList, conceptsList, textList);
        }else {
            if(Constants.PRINT_API_RESPONSE)
                Log.d("APIRESPONSE", "EMPTY RESPONSE");
        }
    }

    public void onInsertResponseMessage(Message message){

    }
}
