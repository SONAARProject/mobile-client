package pt.fcul.lasige.sonaar.api;

import pt.fcul.lasige.sonaar.api.pojo.Message;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    @FormUrlEncoded
    @POST("search/")
    Call<Message> searchImageUrl(@Field("imageUrl") String imageBase64,
                                 @Field("lang") String lang,
                                 @Field("type") String type,
                                 @Field("platform") String platform,
                                 @Field("userId") String userId);

    @FormUrlEncoded
    @POST("search/")
    Call<Message> searchImageBinary(@Field("imageBase64") String imageBase64,
                                    @Field("lang") String lang,
                                    @Field("type") String type,
                                    @Field("platform") String platform,
                                    @Field("socialMedia") String socialMedia,
                                    @Field("userId") String userId);

    @FormUrlEncoded
    @POST("search/")
    Call<Message> searchImageBinary(@Field("imageBase64") String imageBase64,
                                    @Field("lang") String lang,
                                    @Field("type") String type,
                                    @Field("platform") String platform,
                                    @Field("userId") String userId);

    @FormUrlEncoded
    @POST("insertBase64/")
    Call<Message> insertBase64(@Field("imageBase64") String imageBase64,
                               @Field("type") String type,
                               @Field("altText") String altText,
                               @Field("postText") String postText,
                               @Field("lang") String lang,
                               @Field("userId") String userId,
                               @Field("platform") String platform,
                               @Field("socialMedia") String socialMedia);
}
