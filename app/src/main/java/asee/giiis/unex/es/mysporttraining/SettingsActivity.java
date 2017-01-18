package asee.giiis.unex.es.mysporttraining;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Customize Action Bar
        getSupportActionBar().setTitle("Ajustes");


        // Shared Preferences for sync calendar
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_calendar), MODE_PRIVATE);
        // 0 - Default value if doesn't found preferences
        int sharedPrefCalendar = sharedPref.getInt(getString(R.string.shared_preferences_calendar), 0);

        Switch syncCalendar = (Switch) findViewById(R.id.settings_switch);
        if (sharedPrefCalendar == 0){
            syncCalendar.setChecked(false);
        }else {
            syncCalendar.setChecked(true);
        }
        syncCalendar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_calendar), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putInt(getString(R.string.shared_preferences_calendar), 1);
                    editor.commit();
                } else {
                    editor.putInt(getString(R.string.shared_preferences_calendar), 0);
                    editor.commit();
                }
            }
        });
    }
}
