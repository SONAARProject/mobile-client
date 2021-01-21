package pt.fcul.lasige.sonaar.api;

import pt.fcul.lasige.sonaar.api.pojo.Message;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("/sonaar/clarifai/search/{url}")
    Call<Message> searchImageUrl(@Path("url") String url);

    @FormUrlEncoded
    @POST("/sonaar/clarifai/search/")
    Call<Message> searchImageBinary(@Field("imageBase64") String imageBase64);

    @FormUrlEncoded
    @POST("/sonaar/clarifai/insertBase64/")
    Call<Message> insertBase64(@Field("imageBase64") String imageBase64, @Field("altText") String altText);
}
