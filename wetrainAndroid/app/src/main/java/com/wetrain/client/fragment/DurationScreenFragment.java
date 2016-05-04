package com.wetrain.client.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

/**
 * Created by Administrator on 12/24/15.
 */
public class DurationScreenFragment extends WeTrainBaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.duration_screen, container, false);


        ((LinearLayout)view.findViewById(R.id.half_hour_lyt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To avoid double click
                if(!canTriggerClickEvent()) return;


                WorkoutLocationFragment.WORKOUT_LENGTH = 30;
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.WorkoutFragmentTag, false, false);
            }
        });
        ((LinearLayout)view.findViewById(R.id.one_hour_lyt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To avoid double click
                if(!canTriggerClickEvent()) return;

                WorkoutLocationFragment.WORKOUT_LENGTH = 60;
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.WorkoutFragmentTag, false, false);
            }
        });


        return view;
    }



    @Override
    protected void onFragmentResumed() {
        Log.v("test", "onFragmentResumed");

        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).findViewById(R.id.account_button).setSelected(false);
        getActivity().findViewById(R.id.train_button).setSelected(true);
        ((MainActivity)getActivity()).setTitle("Select Duration");
        ((MainActivity)getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.TitleType);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationText("Back");
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);
        ((MainActivity) getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_WHITE_BACK);
    }


}
