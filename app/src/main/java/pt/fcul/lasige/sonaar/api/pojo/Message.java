package pt.fcul.lasige.sonaar.api.pojo;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("status")
    public Integer status;
    @SerializedName("message")
    public String message;
    @SerializedName("alts")
    public String alts;
}
