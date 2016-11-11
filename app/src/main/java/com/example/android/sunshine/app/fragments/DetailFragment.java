package com.example.android.sunshine.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

/**
 * Created by Trethtzer on 11/11/2016.
 */

public class DetailFragment extends Fragment {

    public DetailFragment() {
        this.setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView tv = (TextView) rootView.findViewById(R.id.textViewDescriptionDetail);
        tv.setText(getActivity().getIntent().getStringExtra("Forecast"));

        return rootView;
    }
}
