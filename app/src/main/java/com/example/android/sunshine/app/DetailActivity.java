package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.fragments.DetailFragment;
import com.example.android.sunshine.app.fragments.ForecastFragment;

public class DetailActivity extends ActionBarActivity {

    private String nameClass = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            Uri dateUri = getIntent().getData();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, DetailFragment.newInstance(dateUri))
                    .commit();
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

        return super.onOptionsItemSelected(item);
    }

    //Funcion que devuelve el nombre de la activity
    public String getNameClass(){
        return nameClass;
    }
}
