package com.example.android.sunshine.app.fragments;

/**
 * Created by Trethtzer on 05/11/2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    private String nameClass = "PlaceholderFragment";

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> fakeData = new ArrayList<>();
        fakeData.add("Today - Sunny - 20ºC");
        fakeData.add("Tomorrow - Sunny - 20ºC");
        fakeData.add("Wednesday - Sunny - 20ºC");
        fakeData.add("Thursday - Sunny - 20ºC");
        fakeData.add("Friday - Sunny - 20ºC");
        fakeData.add("Saturday - Sunny - 20ºC");
        fakeData.add("Sunday - Sunny - 20ºC");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,fakeData);
        ListView listViewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        listViewForecast.setAdapter(adapter);


        new ForecastTask().execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=29140,ES&mode=json&units=metric&cnt=7&APPID=");


        return rootView;
    }

    public static class ForecastTask extends AsyncTask<String,Void,String>{
        private String nameClass = "ForecastTask";

        public String doInBackground(String... strings){
            // CONECTAMOS PARA OBTENER LOS DATOS DEL TIEMPO.
            HttpURLConnection urlConnection = null;
            BufferedReader bReader = null;

            String forecastJsonStr = null;
            String APPKEY = "b33843be8c7971ec5abfe8732be7b4a2";

            try{
                URL url = new URL(strings[0] + APPKEY);
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

            return forecastJsonStr;
        }
    }
}