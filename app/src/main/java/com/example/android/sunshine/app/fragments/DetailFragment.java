package com.example.android.sunshine.app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;

import java.net.URI;

/**
 * Created by Trethtzer on 11/11/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private ShareActionProvider mShareActionProvider;

    public static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private Uri uriIntent;
    boolean mForecast = false;

    private ImageView detailIcon;
    private String detailIconString;
    private TextView detailDay;
    private String detailDayString;
    private TextView detailDayAndMonth;
    private String detailDayAndMonthString;
    private TextView detailMaxTemp;
    private String detailMaxTempString;
    private TextView detailMinTemp;
    private String detailMinTempString;
    private TextView detailDescription;
    private String detailDescriptionString;
    private TextView detailHumidity;
    private String detailHumidityString;
    private TextView detailWind;
    private String detailWindString;
    private TextView detailPressure;
    private String detailPressureString;

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_WIND = 6;
    public static final int COL_WEATHER_PRESSURE = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;



    public DetailFragment() {
        setHasOptionsMenu(true);
    }
    public static DetailFragment newInstance(Uri uri){
        DetailFragment df = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("uri",uri);
        df.setArguments(args);

        return df;
    }
    public Uri getUri() {
        return getArguments().getParcelable("uri");
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if(getUri() != null) uriIntent = getUri();

        detailIcon = (ImageView) rootView.findViewById(R.id.detailIcon);
        detailDay = (TextView) rootView.findViewById(R.id.detailDay);
        detailDayAndMonth = (TextView) rootView.findViewById(R.id.detailDayAndMonth);
        detailMaxTemp = (TextView) rootView.findViewById(R.id.detailMaxTemp);
        detailMinTemp = (TextView) rootView.findViewById(R.id.detailMinTemp);
        detailDescription = (TextView) rootView.findViewById(R.id.detailDescription);
        detailHumidity = (TextView) rootView.findViewById(R.id.detailHumidity);
        detailWind = (TextView) rootView.findViewById(R.id.detailWind);
        detailPressure = (TextView) rootView.findViewById(R.id.detailPressure);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mForecast) mShareActionProvider.setShareIntent(createShareForecastIntent());
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_item_share:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (null != uriIntent)
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    uriIntent,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }

        detailIcon.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID)));
        detailDayString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        detailDescriptionString = data.getString(COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(getActivity());
        detailMaxTempString = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        detailMinTempString = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        detailDayAndMonthString = Utility.getFormattedMonthDay(getActivity(),data.getLong(COL_WEATHER_DATE));
        detailHumidityString = getString(R.string.format_humidity,data.getDouble(COL_WEATHER_HUMIDITY));
        detailWindString = Utility.getFormattedWind(getActivity(),data.getFloat(COL_WEATHER_WIND),data.getFloat(COL_WEATHER_DEGREES));
        detailPressureString = getString(R.string.format_pressure,data.getDouble(COL_WEATHER_PRESSURE));

        detailDay.setText(detailDayString);
        detailDayAndMonth.setText(detailDayAndMonthString);
        detailMaxTemp.setText(detailMaxTempString);
        detailMinTemp.setText(detailMinTempString);
        detailDescription.setText(detailDescriptionString);
        detailHumidity.setText(detailHumidityString);
        detailWind.setText(detailWindString);
        detailPressure.setText(detailPressureString);

        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

        mForecast = true;
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = uriIntent;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            uriIntent = updatedUri;
            getLoaderManager().restartLoader(0, null, this);
        }
    }
}
