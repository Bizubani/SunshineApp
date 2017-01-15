package negativespace.projectsunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> myAdapter;

    public ForecastFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstancedState){
        super.onCreate(savedInstancedState);
        setHasOptionsMenu(true);
    }

    private void updateWeather(){
        RetrieveData data = new RetrieveData();
        data.execute();
    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecast_fragment,menu);
     }

    @Override public  boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        List<String>weatherData = new ArrayList<>();
        myAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textView,
                weatherData);

        ListView myList = (ListView) rootView.findViewById(R.id.list_forecast);
        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String forecast = myAdapter.getItem(i);
                Intent myIntent = new Intent(getActivity(),Details.class).putExtra(Intent.EXTRA_TEXT,forecast);
                startActivity(myIntent);
                            }
        });


        return rootView;
    }

    private class RetrieveData extends AsyncTask<String, Void, String[]> {


        SharedPreferences sharedInfo = PreferenceManager.getDefaultSharedPreferences(getActivity());


        private final String LOG_TAG = RetrieveData.class.getSimpleName();
        private String id = "7cf94b9e9028dd29813b70274b104984";
        private String format = "json";
        private String unit = "Metric";
        private String city = sharedInfo.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        private String countryCode = "TT";
        private int days = 7;
        private String unitType = sharedInfo.getString(getString(R.string.pref_unit_key),getString(R.string.pref_unit_metric));

        private String simpleDate (long time){
            SimpleDateFormat simpleDate = new SimpleDateFormat("EEE, MMM dd, ''yy");
            return simpleDate.format(time);
        }

        private String formatHighLows(double high, double low, String unitType){

            if(unitType.equals(getString(R.string.pref_unit_imperial))){
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            }

            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh +  " / " + roundedLow ;
            return highLowStr;
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr)
                throws JSONException{

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_MIN = "temp_min";
            final String OWM_MAX = "temp_max";
            final String OWM_DESCRIPTION = "main";
            final String OWM_DAY_TIME = "dt";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray dataList = forecastJson.getJSONArray(OWM_LIST);
            JSONObject dayForecast;

            String[] resultStrs = new String[dataList.length()];

            for(int i = 0; i < dataList.length(); i++){

                String day;
                String description;
                String highAndLow;

                dayForecast = dataList.getJSONObject(i);


//                GregorianCalendar gc = new GregorianCalendar();
//                gc.add(GregorianCalendar.DATE,i);
//                Date time = gc.getTime();
//                SimpleDateFormat readableDate = new SimpleDateFormat("EEE, MMM dd, ''yy");
//                day = readableDate.format(time);

                day = simpleDate(dayForecast.getLong(OWM_DAY_TIME)*1000);

                JSONObject weatherDescription = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherDescription.getString(OWM_DESCRIPTION);

                JSONObject weatherData= dayForecast.getJSONObject(OWM_DESCRIPTION);
                double high = weatherData.getDouble(OWM_MAX);
                double low = weatherData.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low, unitType);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;

            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... strings) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr;

            final String FORECAST_URL_BASE = "http://api.openweathermap.org/data/2.5/forecast?";
            final String QUERY_PARAM = "q";
            final String UNIT_PARAM = "units";
            final String FORMAT_PARAM = "mode";
            final String COUNT_PARAM = "cnt";
            final String ID_PARAM = "appid";
            try {
                Uri.Builder address = Uri.parse(FORECAST_URL_BASE).buildUpon().
                        appendQueryParameter(QUERY_PARAM , city+","+countryCode).
                        appendQueryParameter(UNIT_PARAM,unit).
                        appendQueryParameter(FORMAT_PARAM,format).
                       // appendQueryParameter(COUNT_PARAM, Integer.toString(days)).
                        appendQueryParameter(ID_PARAM,id);
                Log.d(LOG_TAG,"Built URL: " + address.toString());


                URL url = new URL(address.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();

               return getWeatherDataFromJson(forecastJsonStr);

            } catch (JSONException e){
                Log.e(LOG_TAG, "Error form method getWeatherDataFromJson ", e );
            }
            catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    }catch(final IOException e) {
                        Log.e(LOG_TAG, "Error closing reader", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if(strings != null){
                myAdapter.clear();
                myAdapter.addAll(strings);
            }
        }
    }


    }
