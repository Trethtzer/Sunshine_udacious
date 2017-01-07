package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.fragments.ForecastFragment;


public class MainActivity extends ActionBarActivity {
    String nameClass = "MainActivity";
    String mLocation;
    static final String FORECASTFRAGMENT_TAG = "1005";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(),FORECASTFRAGMENT_TAG)
                    .commit();
        }
        mLocation = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.etp_key_location),
                getString(R.string.etp_defaultValue_location));
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(!mLocation.equals(PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.etp_key_location),
                getString(R.string.etp_defaultValue_location)))){
            // Si el valor de location se ha actualizado se ejecuta este if.
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            ff.onLocationChanged();
            mLocation = PreferenceManager.getDefaultSharedPreferences(this).getString(
                    getString(R.string.etp_key_location),
                    getString(R.string.etp_defaultValue_location));
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
}
