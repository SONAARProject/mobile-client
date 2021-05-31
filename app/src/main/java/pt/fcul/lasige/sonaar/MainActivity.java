package pt.fcul.lasige.sonaar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import pt.fcul.lasige.sonaar.accessibleservice.AccessibilityServiceClass;
import pt.fcul.lasige.sonaar.consent.ConsentActivity;
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

        if(!consent){
            Intent i = new Intent(getApplicationContext(), ConsentActivity.class);
            startActivityForResult(i, 1904);
        }else {
            setContentView(R.layout.activity_main);
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

        }
    }
}
