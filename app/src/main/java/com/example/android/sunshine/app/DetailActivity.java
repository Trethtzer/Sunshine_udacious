package com.example.android.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.example.android.sunshine.app.fragments.DetailFragment;
import com.example.android.sunshine.app.fragments.ForecastFragment;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }
}