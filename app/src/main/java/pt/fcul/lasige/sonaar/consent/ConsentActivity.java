package pt.fcul.lasige.sonaar.consent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import pt.fcul.lasige.sonaar.R;

public class ConsentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1904;

    private int currentPage = 0;
    private FragmentManager fragmentManager;
    private Button btNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ConsentPage1.class, null)
                .setReorderingAllowed(true)
                .commit();
        btNext = findViewById(R.id.bt_next);
        btNext.setOnClickListener(v -> {
            switch (currentPage){
                case 0:
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ConsentPage2.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                    btNext.setText(R.string.i_accept);
                    break;
                case 1:
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ConsentPage3.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                    btNext.setText(R.string.next);
                    break;
                case 2:
                    if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {

                    } else {
                        requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_CODE);
                    }
                    break;
                default:
                    finish();
                    break;
            }
            currentPage++;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            Intent returnIntent = new Intent();
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ConsentPage5.class, null)
                        .setReorderingAllowed(true)
                        .commit();
                btNext.setText(R.string.start);
                findViewById(R.id.bt_read_doc).setVisibility(View.VISIBLE);
                findViewById(R.id.bt_read_doc).setOnClickListener(v -> {
                    Uri uri = Uri.parse(getString(R.string.doc_url));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
                setResult(Activity.RESULT_OK, returnIntent);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.consent), true);
//                editor.putString(getString(R.string.uuid), UUID.randomUUID().toString());
                editor.apply();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ConsentPage4.class, null)
                        .setReorderingAllowed(true)
                        .commit();
                btNext.setText(R.string.exit);
                setResult(Activity.RESULT_CANCELED, returnIntent);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.consent), false);
                editor.apply();
            }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}