package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class MainFragment extends Fragment {
    View myView;
    TextView title_textview;
    ImageView toolbar_image;
    AppBarLayout main_header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_main, container, false);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        //title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        //title_textview.setText("REGISTER");
        main_header.setVisibility(View.GONE);

        return myView;
    }
}
