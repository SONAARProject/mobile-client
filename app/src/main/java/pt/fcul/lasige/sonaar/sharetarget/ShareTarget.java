package pt.fcul.lasige.sonaar.sharetarget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import pt.fcul.lasige.sonaar.notifications.NotificationController;
import pt.fcul.lasige.sonaar.R;
import pt.fcul.lasige.sonaar.api.APIClient;
import pt.fcul.lasige.sonaar.api.APIMessageHandler;

public class ShareTarget extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_target);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        ImageView iv = findViewById(R.id.iv_received);
                        iv.setImageBitmap(bitmap);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

                        APIClient.searchImageFile(
                                bos.toByteArray(),
                                new APIMessageHandler(new NotificationController(getApplicationContext())),
                                APIMessageHandler.SOCIAL_NETWORK.NONE);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}