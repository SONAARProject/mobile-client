package pt.fcul.lasige.sonaar.settings;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.w3c.dom.Text;

import pt.fcul.lasige.sonaar.BuildConfig;
import pt.fcul.lasige.sonaar.R;
import pt.fcul.lasige.sonaar.ReportProblemForm;
import pt.fcul.lasige.sonaar.util.ImageUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        ((TextView) findViewById(R.id.tv_version)).setText("Version: " + BuildConfig.VERSION_NAME);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String uuid = prefs.getString(getString(R.string.uuid), "null");
        ((TextView) findViewById(R.id.tv_uuid)).setText("ID: " + uuid);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final Preference delete = findPreference("delete");
            delete.setOnPreferenceClickListener(preference -> {

                try {
                    PendingIntent pi = MediaStore.createDeleteRequest(getActivity().getContentResolver(), ImageUtils.getAllScreenShots(getActivity()));
                    getActivity().startIntentSenderForResult(pi.getIntentSender(), 12345, null, 0, 0, 0);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            });
            final Preference revoke = findPreference("revoke");
            revoke.setOnPreferenceClickListener(preference -> {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.consent), false);
                editor.apply();
                Toast.makeText(getActivity(), "Screenshot consent revoked, please close the app.", Toast.LENGTH_SHORT).show();
                return false;
            });
            final Preference reportProblem = findPreference("report_problem");
            reportProblem.setOnPreferenceClickListener(preference -> {
                Intent i = new Intent(getContext(), ReportProblemForm.class);
                startActivity(i);
                return false;
            });
        }
    }
}