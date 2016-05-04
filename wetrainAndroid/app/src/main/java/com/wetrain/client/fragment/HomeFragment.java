package com.wetrain.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wetrain.client.R;
import com.wetrain.client.activity.AlarmReceiver;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

/**
 * Created by Administrator on 1/11/16.
 */
public class HomeFragment extends WeTrainBaseFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_screen, null);

        view.findViewById(R.id.train_now_lyt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To avoid double click
               if(!canTriggerClickEvent()) return;

                WorkoutLocationFragment.WORKOUT_SESSION_TYPE = WorkoutLocationFragment.WORKOUT_SESSION_TRAIN_NOW;
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.DurationFragmentTag, false, false);

            }
        });
        view.findViewById(R.id.train_later_lyt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To avoid double click
                if(!canTriggerClickEvent()) return;

                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.ScheduleDatePickerFragmentTag, false, false);

            }
        });

        view.findViewById(R.id.motivate_me_lyt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To avoid double click
                if(!canTriggerClickEvent()) return;

                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.MotivativeMeSearchTrainerFragmentTag, false, false);

            }
        });


        initActionBarElements();

        return view;
    }

    @Override
    protected void onFragmentResumed() {

        initActionBarElements();


    }

    private void initActionBarElements(){
        ((MainActivity) getActivity()).setWindowFullScreen(false);
        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity) getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).findViewById(R.id.account_button).setSelected(false);
        getActivity().findViewById(R.id.train_button).setSelected(true);
        ((MainActivity) getActivity()).setTitle("");
        ((MainActivity) getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.GONE);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
        ((MainActivity)getActivity()).showNotificationView(View.VISIBLE);



    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments() != null){
            boolean isFromNotification = getArguments().getBoolean(AlarmReceiver.INTENT_FROM_NOTIFICATION, false);
            if(isFromNotification){
                Bundle notificationBundle = new Bundle();
                notificationBundle.putBoolean(AlarmReceiver.INTENT_FROM_NOTIFICATION, true);
                FragmentHolder.setFragment(getActivity(), notificationBundle, FragmentHolder.FragmentTags.WorkoutLocationFragmentTag, false, false);

            }

        }
    }

}
