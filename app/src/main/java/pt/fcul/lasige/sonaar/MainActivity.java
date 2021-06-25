package pt.fcul.lasige.sonaar;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import pt.fcul.lasige.sonaar.accessibleservice.AccessibilityServiceClass;
import pt.fcul.lasige.sonaar.consent.ConsentActivity;
import pt.fcul.lasige.sonaar.consent.ConsentPage4;
import pt.fcul.lasige.sonaar.consent.ConsentPage5;
import pt.fcul.lasige.sonaar.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1904){
            if(resultCode == Activity.RESULT_OK){
                setContentView(R.layout.activity_main);
                TextView t1, t2;
                t1 = findViewById(R.id.tv_study_1);
                t2 = findViewById(R.id.tv_study_2);
                t1.setText(Html.fromHtml(getString(R.string.study_1)));
                t2.setText(Html.fromHtml(getString(R.string.study_2)));
                Linkify.addLinks(t1, Linkify.ALL);
                Linkify.addLinks(t2, Linkify.ALL);
                t1.setMovementMethod(LinkMovementMethod.getInstance());
                t2.setMovementMethod(LinkMovementMethod.getInstance());
                findViewById(R.id.bt_open_settings).setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)));
            }else {
                finish();
            }
        }
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void start(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean consent = prefs.getBoolean(getString(R.string.consent), false);
        String uuid = prefs.getString(getString(R.string.uuid), "null");

        if(!consent){
            Intent i = new Intent(getApplicationContext(), ConsentActivity.class);
            startActivityForResult(i, 1904);
        }else {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                setContentView(R.layout.activity_main);
                TextView t1, t2;
                t1 = findViewById(R.id.tv_study_1);
                t2 = findViewById(R.id.tv_study_2);
                t1.setText(Html.fromHtml(getString(R.string.study_1)));
                t2.setText(Html.fromHtml(getString(R.string.study_2)));
                Linkify.addLinks(t1, Linkify.ALL);
                Linkify.addLinks(t2, Linkify.ALL);
                t1.setMovementMethod(LinkMovementMethod.getInstance());
                t2.setMovementMethod(LinkMovementMethod.getInstance());
                TextView title = findViewById(R.id.tv_title);
                Button start = findViewById(R.id.bt_open_settings);
                if(!isMyServiceRunning(AccessibilityServiceClass.class)){
                    title.setText(R.string.please_activate_the_sonaar_service_on_the_accessibility_settings_menu);
                    start.setVisibility(View.VISIBLE);
                    start.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)));
                }else{
                    title.setText(R.string.soonar_is_active);
                    start.setVisibility(View.GONE);
                }

                if (uuid.equals("null")) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.uuid), UUID.randomUUID().toString());
                    editor.apply();
                }


            } else {
                setContentView(R.layout.activity_main_request_storage_permission);
                findViewById(R.id.bt_open_settings).setOnClickListener(view -> {
                    requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1904);
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1904) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setContentView(R.layout.activity_main);
                TextView t1, t2;
                t1 = findViewById(R.id.tv_study_1);
                t2 = findViewById(R.id.tv_study_2);
                t1.setText(Html.fromHtml(getString(R.string.study_1)));
                t2.setText(Html.fromHtml(getString(R.string.study_2)));
                Linkify.addLinks(t1, Linkify.ALL);
                Linkify.addLinks(t2, Linkify.ALL);
                t1.setMovementMethod(LinkMovementMethod.getInstance());
                t2.setMovementMethod(LinkMovementMethod.getInstance());
                TextView title = findViewById(R.id.tv_title);
                Button start = findViewById(R.id.bt_open_settings);
                if(!isMyServiceRunning(AccessibilityServiceClass.class)){
                    title.setText(R.string.please_activate_the_sonaar_service_on_the_accessibility_settings_menu);
                    start.setVisibility(View.VISIBLE);
                    start.setOnClickListener(v -> startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)));
                }else{
                    title.setText(R.string.soonar_is_active);
                    start.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_refuse), Toast.LENGTH_SHORT).show();
            }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }
}
