package pt.fcul.lasige.sonaar.api;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pt.fcul.lasige.sonaar.pojo.Message;
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
                .baseUrl("https://accessible-serv.lasige.di.fc.ul.pt")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static void searchImageUrl(String url){

        Call<Message> call1 = getClient().create(APIInterface.class).searchImageUrl(url);
        call1.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.d("APIRESPONSE", call.toString());
                Log.d("APIRESPONSE", response.toString());
                Message message = response.body();
                if(message != null){
                    Log.d("APIRESPONSE", "message: " + message.message);
                    Log.d("APIRESPONSE", "status:  " + message.status);
//                    Log.d("APIRESPONSE", message.alts);
                    if (message.status == 1){
                        Log.d("APIRESPONSE", message.alts);
                        try {
                            JSONArray array = new JSONArray(message.alts);
                            for (int i=0;i<array.length();i++){
                                JSONObject jsonObject = array.getJSONObject(i);
                                Log.d("APIRESPONSE", jsonObject.getString("ClarifaiConcepts"));
                                Log.d("APIRESPONSE", jsonObject.getString("AltText"));
                                Log.d("APIRESPONSE", jsonObject.getString("Keywords"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Log.d("APIRESPONSE", "NULL RESPONSE");
                }

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d("APIRESPONSE", "BOOM");
                t.printStackTrace();
            }

        });
    }

    public static void searchImageFile(byte[] bytes){

        Call<Message> call = getClient().create(APIInterface.class).searchImageBinary(Base64.encodeToString(bytes, Base64.DEFAULT));
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.d("APIRESPONSE", call.toString());
                Log.d("APIRESPONSE", response.toString());
                Message message = response.body();
                if(message != null){
                    Log.d("APIRESPONSE", "message: " + message.message);
                    Log.d("APIRESPONSE", "status:  " + message.status);
//                    Log.d("APIRESPONSE", message.alts);
                }else {
                    Log.d("APIRESPONSE", "NULL RESPONSE");
                }

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d("APIRESPONSE", "BOOM");
                t.printStackTrace();
            }

        });
    }

    public static void searchImageFile(File f){

        Call<Message> call = getClient().create(APIInterface.class).searchImageBinary(Base64.encodeToString(getBytes(f), Base64.DEFAULT));
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.d("APIRESPONSE", call.toString());
                Log.d("APIRESPONSE", response.toString());
                Message message = response.body();
                if(message != null){
                    Log.d("APIRESPONSE", "message: " + message.message);
                    Log.d("APIRESPONSE", "status:  " + message.status);
//                    Log.d("APIRESPONSE", message.alts);
                }else {
                    Log.d("APIRESPONSE", "NULL RESPONSE");
                }

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d("APIRESPONSE", "BOOM");
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

