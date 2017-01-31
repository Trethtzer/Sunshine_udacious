package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.fragments.DetailFragment;
import com.example.android.sunshine.app.fragments.ForecastFragment;


public class MainActivity extends ActionBarActivity implements ForecastFragment.mainActivityCallback{
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    String nameClass = "MainActivity";
    String mLocation;
    public boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                /*getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();*/
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(mTwoPane);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (null != ff) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        if(id == R.id.action_map){
            String location = PreferenceManager.getDefaultSharedPreferences(this).getString(
                    getString(R.string.etp_key_location),
                    getString(R.string.etp_defaultValue_location));

            Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q",location)
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);

            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }else{
                Log.d(getNameClass(),"Error");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap(){
        String location = Utility.getPreferredLocation(this);
    }

    //Funcion que devuelve el nombre de la activity
    public String getNameClass(){
        return nameClass;
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if(!mTwoPane){
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
        }else{
            Toast.makeText(this,dateUri.toString(),Toast.LENGTH_LONG).show();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, DetailFragment.newInstance(dateUri), DETAILFRAGMENT_TAG)
                    .commit();
        }
    }
}
