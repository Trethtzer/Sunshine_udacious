package com.example.android.sunshine.app.fragments;

/**
 * Created by Trethtzer on 05/11/2016.
 */

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.app.ForecastAdapter;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import static com.example.android.sunshine.app.Utility.getPreferredLocation;
import static com.example.android.sunshine.app.sync.SunshineSyncAdapter.syncImmediately;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private String nameClass = "ForecastFragment";
    private static ForecastAdapter adapter;
    private int LOADER_ID = 1005;
    private Bundle sIS;
    private int lastPosition;
    ListView listView;
    private boolean mTwoPane;

    // Comunicacion del fragmento con mainActivity
    mainActivityCallback mCallback;

    public interface mainActivityCallback{
        public void onItemSelected(Uri dateUri);
    }

    private static final String[] FORECAST_COLUMNS = {
        WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
        WeatherContract.WeatherEntry.COLUMN_DATE,
        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
        WeatherContract.LocationEntry.COLUMN_COORD_LAT,
        WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;


    public ForecastFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new ForecastAdapter(getActivity(),null,0);
        adapter.useTodayLayout(mTwoPane);
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        if(null != savedInstanceState){
            lastPosition = savedInstanceState.getInt("position");
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null){
                    String locationSetting = getPreferredLocation(getActivity());
                    mCallback.onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting,cursor.getLong(COL_WEATHER_DATE)));
                }
                lastPosition = position;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            mCallback = (mainActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void onSaveInstanceState(Bundle outB){
        outB.putInt("position",lastPosition);
        super.onSaveInstanceState(outB);
    }

    // Funcion para cargar los datos de nuevo.
    public void updateWeather(){
        syncImmediately(getActivity());
    }

    public void onLocationChanged(){
        updateWeather();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void setUseTodayLayout(boolean mTwoPane){
        this.mTwoPane = mTwoPane;
        if(adapter != null) adapter.useTodayLayout(mTwoPane);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(id == LOADER_ID){
            String locationSetting = getPreferredLocation(getActivity());
            // Sort order:  Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                    locationSetting, System.currentTimeMillis());

            return new CursorLoader(getActivity(),weatherForLocationUri,FORECAST_COLUMNS,null,null,sortOrder);
        }

        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if(lastPosition != ListView.INVALID_POSITION) listView.smoothScrollToPosition(lastPosition);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}