package pt.fcul.lasige.sonaar.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.fcul.lasige.sonaar.MediaPostCreationDetector;
import pt.fcul.lasige.sonaar.NotificationController;
import pt.fcul.lasige.sonaar.api.pojo.Message;
import pt.fcul.lasige.sonaar.data.Constants;

public class APIMessageHandler{

    private NotificationController notificationController;

    public APIMessageHandler(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public void onSearchResponseMessage(Message message){
        if(message != null){
            if(Constants.PRINT_API_RESPONSE){
                Log.d("APIRESPONSE", String.format("message: %s", message.message));
                Log.d("APIRESPONSE", String.format("status: %s", message.status));
                Log.d("APIRESPONSE", String.format("alts: %s", message.alts));
            }
            if (message.status == 1){
                try {
                    JSONArray array = new JSONArray(message.alts);
                    for (int i=0;i<array.length();i++){
                        JSONObject jsonObject = array.getJSONObject(i);
                        notificationController.sendNotification("Twitter", jsonObject.getString("AltText"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(message.status == 3){
                MediaPostCreationDetector.getInstance().setSendAltTextToAPI(true);
            }
        }else {
            if(Constants.PRINT_API_RESPONSE)
                Log.d("APIRESPONSE", "EMPTY RESPONSE");
        }
    }

    public void onInsertResponseMessage(Message message){

    }
}
