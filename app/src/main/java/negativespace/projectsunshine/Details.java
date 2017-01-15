package negativespace.projectsunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Details extends AppCompatActivity {

    private ShareActionProvider myProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.details,menu);


        MenuItem item = menu.findItem(R.id.action_share);
        myProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra(Intent.EXTRA_TEXT)+ "  #My little Sunshine");
        myProvider.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Intent settingIntent = new Intent(this,SettingActivity.class);
            startActivity(settingIntent);
            return true;
        }
        if (id == R.id.show_on_map) {
            openPreferredLocation();
            return true;
        }
        else
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

