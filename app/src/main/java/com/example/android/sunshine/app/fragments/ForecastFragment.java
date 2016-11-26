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
import com.example.android.sunshine.app.FetchWeatherTask;
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
    private String nameClass = "ForecastFragment";
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
                updateWeather();
                break;
            default:
                break;
        }
        return true;
    }
    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }

    // Funcion para cargar los datos de nuevo.
    public void updateWeather(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sp.getString(getString(R.string.etp_key_location),getString(R.string.etp_defaultValue_location));
        new FetchWeatherTask(getActivity(),adapter).execute(location);
    }
}