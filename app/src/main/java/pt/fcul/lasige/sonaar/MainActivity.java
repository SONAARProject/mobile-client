package pt.fcul.lasige.sonaar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;

import pt.fcul.lasige.sonaar.api.APIClient;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1904;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {} else {
            // You can directly ask for the permission.
            requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE);
        }

        findViewById(R.id.bt_open_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS), 0);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(getApplicationContext(), "Sonaar needs permission to read external storage in order to the app to function", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }
}
