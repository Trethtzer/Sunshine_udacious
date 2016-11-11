package com.example.android.sunshine.app.fragments;

/**
 * Created by Trethtzer on 05/11/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
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
import android.widget.Toast;

import com.example.android.sunshine.app.DetailActivity;
import com.example.android.sunshine.app.R;

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
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    private String nameClass = "PlaceholderFragment";
    private static ArrayList<String> fakeData;
    private static ArrayAdapter<String> adapter;

    public ForecastFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        fakeData = new ArrayList<>();
        fakeData.add("Today - Sunny - 20ºC");
        fakeData.add("Tomorrow - Sunny - 20ºC");
        fakeData.add("Wednesday - Sunny - 20ºC");
        fakeData.add("Thursday - Sunny - 20ºC");
        fakeData.add("Friday - Sunny - 20ºC");
        fakeData.add("Saturday - Sunny - 20ºC");
        fakeData.add("Sunday - Sunny - 20ºC");

        adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,fakeData);
        ListView listViewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        listViewForecast.setAdapter(adapter);

        listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("Forecast",adapterView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_refresh:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String location = sp.getString("location","29140");
                new ForecastTask().execute(location);
                break;
            default:
                break;
        }
        return true;
    }

    // HEBRA PARA SACAR DATOS DE INTERNET.
    public static class ForecastTask extends AsyncTask<String,Void,String[]>{
        private String nameClass = "ForecastTask";



        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(nameClass, "Forecast entry: " + s);
            }
            return resultStrs;

        }




        public String[] doInBackground(String... strings){

            // No params
            if(strings == null){
                return null;
            }

            // CONECTAMOS PARA OBTENER LOS DATOS DEL TIEMPO.
            HttpURLConnection urlConnection = null;
            BufferedReader bReader = null;

            String forecastJsonStr = null;
            String APPKEY = "b33843be8c7971ec5abfe8732be7b4a2";

            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", strings[0] + ",ES")
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt","7")
                        .appendQueryParameter("APPID",APPKEY);



                Uri builtUri = builder.build();

                /*
                Uri builtUri = Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily?").buildUpon()
                        .appendQueryParameter("q", strings[0] + ",ES")
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt","7")
                        .appendQueryParameter("APPID",APPKEY).build();
                        */

                URL url = new URL(builtUri.toString());
                Log.v(nameClass,builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    forecastJsonStr = null;
                }
                bReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = bReader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() ==0){
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();

            }catch(IOException e){
                Log.e(nameClass,"Culpable: IOException",e);
            }finally{
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(bReader != null){
                    try{
                        bReader.close();
                    }catch (final IOException e){
                        Log.e(nameClass,"Error closing stream",e);
                    }
                }
            }
            // FIN DE LA CONEXION
            try {
                return getWeatherDataFromJson(forecastJsonStr, 7);
            }catch (JSONException e){
                Log.e(nameClass,e.toString());
            }

            return null;
        }

        protected void onPostExecute(String[] result) {
            adapter.clear();
            for(String s : result){
                adapter.add(s);
            }
            adapter.notifyDataSetChanged();
        }
    }
}