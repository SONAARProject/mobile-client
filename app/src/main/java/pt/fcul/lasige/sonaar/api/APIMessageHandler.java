package pt.fcul.lasige.sonaar.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.fcul.lasige.sonaar.Controller;
import pt.fcul.lasige.sonaar.notifications.NotificationController;
import pt.fcul.lasige.sonaar.api.pojo.Message;
import pt.fcul.lasige.sonaar.data.Constants;

public class APIMessageHandler{

    private NotificationController notificationController;

    public enum SOCIAL_NETWORK{TWITTER, FACEBOOK, INSTAGRAM, NONE}

    public APIMessageHandler(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public void onSearchResponseMessage(Message message, SOCIAL_NETWORK socialNetwork){

        if(message != null){
            if(Constants.PRINT_API_RESPONSE){
                Log.d("APIRESPONSE", String.format("message: %s", message.message));
                Log.d("APIRESPONSE", String.format("status: %s", message.status));
                Log.d("APIRESPONSE", String.format("alts: %s", message.alts));
                Log.d("APIRESPONSE", String.format("concepts: %s", message.concepts));
            }

            if (message.alts != null){
                try {
                    JSONArray array = new JSONArray(message.alts);
                    ArrayList<String> alts = new ArrayList<String>();

                    for (int i=0;i<array.length();i++){
                        JSONObject jsonObject = array.getJSONObject(i);
                        alts.add(jsonObject.getString("AltText"));
                    }

                    Controller.getInstance().setSonaarAltText(alts.get(0));
                    notificationController.sendNotification(socialNetwork, alts.get(0), false, alts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (message.concepts != null){
                notificationController.sendNotification(socialNetwork, message.concepts, true, null);
            }
        }else {
            if(Constants.PRINT_API_RESPONSE)
                Log.d("APIRESPONSE", "EMPTY RESPONSE");
        }
    }

    public void onInsertResponseMessage(Message message){

    }
}
