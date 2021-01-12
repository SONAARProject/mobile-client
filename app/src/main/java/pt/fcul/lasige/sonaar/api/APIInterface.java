package pt.fcul.lasige.sonaar.api;

import okhttp3.MultipartBody;
import pt.fcul.lasige.sonaar.pojo.Message;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("/sonaar/clarifai/search/{url}")
    Call<Message> searchImageUrl(@Path("url") String url);

    @FormUrlEncoded
    @POST("/sonaar/clarifai/search/")
    Call<Message> searchImageBinary(@Field("imageBase64") String imageBase64);
}
