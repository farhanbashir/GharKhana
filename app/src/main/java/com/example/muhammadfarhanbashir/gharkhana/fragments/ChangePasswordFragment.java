package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class ChangePasswordFragment extends Fragment {
    View myView;
    TextView title_textview;
    AppBarLayout main_header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_change_password, container, false);

        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview.setText("CHANGE PASSWORD");
        main_header.setVisibility(View.VISIBLE);


        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

    }
}
