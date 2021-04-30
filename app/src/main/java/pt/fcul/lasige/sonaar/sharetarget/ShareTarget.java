package pt.fcul.lasige.sonaar.sharetarget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import pt.fcul.lasige.sonaar.Controller;
import pt.fcul.lasige.sonaar.R;
import pt.fcul.lasige.sonaar.api.APIClient;
import pt.fcul.lasige.sonaar.api.MessageHandler;
import pt.fcul.lasige.sonaar.overlay.AltTextListAdapter;

public class ShareTarget extends AppCompatActivity implements View.OnClickListener {
    TextView alt;
    ImageView iv;
    ShareTarget activity;
    boolean showMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_target);
        activity = this;

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        AltTextListAdapter mAdapter = new AltTextListAdapter(this, new ArrayList<>());

        alt = findViewById(R.id.tv_altText);
        iv = findViewById(R.id.iv_received);

        alt.setOnClickListener(this);
        RecyclerView mRecyclerView = findViewById(R.id.rv_more_alt_texts);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    new Thread(() -> {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            activity.runOnUiThread(() -> Glide.with(getApplicationContext()).load(bitmap).into(iv));
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            APIClient.searchImageFile(
                                    bos.toByteArray(),
                                    (message, socialNetwork) -> {
                                        if (message.alts != null){
                                            try {
                                                JSONArray array = new JSONArray(message.alts);
                                                ArrayList<String> alts = new ArrayList<String>();

                                                for (int i=0;i<array.length();i++){
                                                    JSONObject jsonObject = array.getJSONObject(i);
                                                    alts.add(jsonObject.getString("AltText"));
                                                }

                                                Controller.getInstance().setSonaarAltText(alts.get(0));
                                                activity.runOnUiThread(() -> alt.setText(alts.get(0)));

                                                mAdapter.setLocalDataSet(alts);
                                                if (alts.size() > 1)
                                                    activity.runOnUiThread(() -> findViewById(R.id.bt_show_more).setVisibility(View.VISIBLE));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else if (message.concepts != null){
                                            String[] data = message.concepts.replace("\"","").split(",");
                                            String concepts = String.join(", ", data);
                                            activity.runOnUiThread(() -> alt.setText(String.format(getString(R.string.notification_text_alt_not_found_none), concepts)));
                                        }
                                    },
                                    MessageHandler.SOCIAL_NETWORK.NONE);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }

                findViewById(R.id.bt_show_more).setOnClickListener(v -> {
                    if(showMore) {
                        ((Button) v).setText(R.string.show_less);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }else {
                        ((Button) v).setText(R.string.show_more);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    showMore = !showMore;
                });

            }else if (type.startsWith("text/")) {
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (text != null) {
                    new Thread(() -> {
                        try {
                            URLConnection connection = new URL(text).openConnection();
                            String contentType = connection.getHeaderField("Content-Type");
                            boolean image = contentType.startsWith("image/");
                            if(image){
                                activity.runOnUiThread(() -> Glide.with(getApplicationContext()).load(text).into(iv));
                                APIClient.searchImageUrl(
                                        text,
                                        (message, socialNetwork) -> {
                                            ArrayList<String> altsList = message.getAltsList();
                                            ArrayList<String> conceptsList = message.getConceptsList();
                                            ArrayList<String> textList = message.getTextList();

                                            if (altsList.size() > 0){
                                                Controller.getInstance().setSonaarAltText(altsList.get(0));
                                                alt.setText(altsList.get(0));
                                                mAdapter.setLocalDataSet(altsList);
                                                if (altsList.size() > 1)
                                                    findViewById(R.id.bt_show_more).setVisibility(View.VISIBLE);

                                            }else {
                                                activity.runOnUiThread(() -> alt.setText(String.format(getString(R.string.notification_text_alt_not_found_none), String.join(", ", conceptsList.subList(0, 3)))));
                                            }
                                        },
                                        MessageHandler.SOCIAL_NETWORK.NONE);
                            }
                        } catch (Exception e) {
                            activity.runOnUiThread(() -> alt.setText(R.string.image_not_found));
                            e.printStackTrace();
                        }
                    }).start();
                }else {
                    alt.setText(R.string.image_not_found);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("altText", ((TextView) v).getText().toString());
        clipboard.setPrimaryClip(clip);
        Controller.getInstance().setSonaarAltText(((TextView) v).getText().toString());
    }
}