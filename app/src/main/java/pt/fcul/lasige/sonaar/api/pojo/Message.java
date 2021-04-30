package pt.fcul.lasige.sonaar.api.pojo;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.fcul.lasige.sonaar.Controller;

public class Message {
    @SerializedName("status")
    public Integer status;
    @SerializedName("message")
    public String message;
    @SerializedName("alts")
    public String alts;
    @SerializedName("concepts")
    public String concepts;
    @SerializedName("text")
    public String text;


    public ArrayList<String> getAltsList(){
        ArrayList<String> altsList = new ArrayList<>();

        if (this.alts != null){
            try {
                JSONArray array = new JSONArray(this.alts);
                ArrayList<java.lang.String> alts = new ArrayList<java.lang.String>();

                for (int i=0;i<array.length();i++){
                    JSONObject jsonObject = array.getJSONObject(i);
                    alts.add(jsonObject.getString("AltText"));
                }
                Controller.getInstance().setSonaarAltText(alts.get(0));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return altsList;
    }

    public ArrayList<String> getConceptsList(){
        ArrayList<String> conceptsList = new ArrayList<>();

        if (this.concepts != null){
            java.lang.String[] data = this.concepts.replace("\"","").split(",");
            for (java.lang.String s: data){
                conceptsList.add(s.trim());
            }
        }

        return conceptsList;
    }

    public ArrayList<String> getTextList(){
        ArrayList<String> textList = new ArrayList<>();

        if (this.text != null){
            try {
                JSONObject jsonObject = new JSONObject(this.text);
                JSONArray phrases = jsonObject.getJSONArray("phrases");
                for (int i = 0; i < phrases.length(); i++) {
                    textList.add(phrases.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return textList;
    }
}
