package negativespace.projectsunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
            return true;
        }

        if (id == R.id.show_on_map) {
            openPreferredLocation();
            return true;
        } else
            return super.onOptionsItemSelected(item);

    }

    public void openPreferredLocation(){

        Intent showMap = new Intent(Intent.ACTION_VIEW);
        SharedPreferences info = PreferenceManager.getDefaultSharedPreferences(this);
        Uri.Builder buildLocation = Uri.parse("geo:0,0?").buildUpon().
                appendQueryParameter("q", info.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default)))
                .appendPath("%2%20Trinidad");
        showMap.setData(buildLocation.build());

        Log.d("Checking values", "This is the URI: " + buildLocation.toString());

        if (showMap.resolveActivity(getPackageManager()) != null) {
            startActivity(showMap);
        }
    }
}
