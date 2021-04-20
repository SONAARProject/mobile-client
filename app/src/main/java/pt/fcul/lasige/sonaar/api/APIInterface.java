package pt.fcul.lasige.sonaar.api;

import pt.fcul.lasige.sonaar.api.pojo.Message;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("search/{url}")
    Call<Message> searchImageUrl(@Path("url") String url);

    @FormUrlEncoded
    @POST("search/")
    Call<Message> searchImageBinary(@Field("imageBase64") String imageBase64, @Field("lang") String lang);

    @FormUrlEncoded
    @POST("insertBase64/")
    Call<Message> insertBase64(@Field("imageBase64") String imageBase64, @Field("altText") String altText);
}
