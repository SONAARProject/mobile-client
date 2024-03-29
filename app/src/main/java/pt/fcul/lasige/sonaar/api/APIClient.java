package pt.fcul.lasige.sonaar.api;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pt.fcul.lasige.sonaar.api.pojo.Message;
import pt.fcul.lasige.sonaar.data.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static void searchImageUrl(String url, IMessageHandler messageHandler, MessageHandler.SOCIAL_NETWORK socialNetwork, String uid){

        String lang;
        if(!Locale.getDefault().getLanguage().equals("pt") && !Locale.getDefault().getLanguage().equals("en")){
            lang = "en";
        }else {
            lang = Locale.getDefault().getLanguage();
        }

        Call<Message> call1 = getClient().create(APIInterface.class).searchImageUrl(url, lang, "authoring", "app", uid);
        call1.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = response.body();
                messageHandler.onSearchResponseMessage(message, socialNetwork);
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                t.printStackTrace();
                messageHandler.onSearchResponseMessage(null, socialNetwork);
            }

        });
    }

    public static void searchImageFile(byte[] bytes, IMessageHandler messageHandler, MessageHandler.SOCIAL_NETWORK socialNetwork, String type, String uid){
        Call<Message> call;

        String lang;
        if(!Locale.getDefault().getLanguage().equals("pt") && !Locale.getDefault().getLanguage().equals("en")){
            lang = "en";
        }else {
            lang = Locale.getDefault().getLanguage();
        }

        if(socialNetwork.toString().equals("none")){
            call = getClient().create(APIInterface.class)
                    .searchImageBinary(Base64.encodeToString(bytes, Base64.NO_WRAP),
                            lang,
                            type,
                            "app",
                            uid);
        }else {
            call = getClient().create(APIInterface.class)
                    .searchImageBinary(Base64.encodeToString(bytes, Base64.NO_WRAP),
                            lang,
                            type,
                            "app",
                            socialNetwork.toString(),
                            uid);
        }


        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                Message message = response.body();
                messageHandler.onSearchResponseMessage(message, socialNetwork);
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                t.printStackTrace();
                messageHandler.onSearchResponseMessage(null, socialNetwork);
            }

        });
    }

    public static void insertImageAndAltText(byte[] bytes, String altText, String postText, MessageHandler.SOCIAL_NETWORK socialNetwork, String uid){

        String lang;
        if(!Locale.getDefault().getLanguage().equals("pt") && !Locale.getDefault().getLanguage().equals("en")){
            lang = "en";
        }else {
            lang = Locale.getDefault().getLanguage();
        }

        Call<Message> call = getClient().create(APIInterface.class).insertBase64(
                Base64.encodeToString(bytes, Base64.NO_WRAP),
                "authoring",
                altText,
                postText,
                lang,
                uid,
                "app",
                socialNetwork.toString());

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = response.body();
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                t.printStackTrace();
            }

        });
    }

    private static byte[] getBytes(File file){

        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }
}

