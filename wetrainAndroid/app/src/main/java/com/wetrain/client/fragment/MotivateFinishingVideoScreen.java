package com.wetrain.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

/**
 * Created by Administrator on 1/19/16.
 */
public class MotivateFinishingVideoScreen extends WeTrainBaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.motivate_me_finished_video,null);

        return view;
    }

    @Override
    protected void onFragmentResumed() {

        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.GONE);
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.TitleType);
        ((MainActivity)getActivity()).setTitle("Motivate Me!");
    }



}
