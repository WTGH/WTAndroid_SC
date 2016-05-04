package com.wetrain.client.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;

/**
 * Created by test on 2/6/16.
 */
public class SplashScreenFragment extends WeTrainBaseFragment {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide device Action bar view and bottom bar
        ((MainActivity) getActivity()).setActionBarVisibility(View.GONE);
        onFragmentResumed();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_screen, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                //set default fragment

                FragmentHolder.removeFragment(getFragmentManager(), FragmentHolder.FragmentTags.SplashScreenFragmentTag.name());

                /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (prefs.getBoolean(Constants.FIRST_TIME_INSTALL, true)) {
                    prefs.edit().putBoolean(Constants.FIRST_TIME_INSTALL, false).commit();
                    FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.FirstTimeTutorialTag, false, false);

                } else {*/

                    FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.HomeFragmenttag, false, false);
                    /*
                     * Check is App from Schedule Notification
                     */
                    ((MainActivity) getActivity()).checkAppFromNotification();

                    /*
                     * Check Exist Workout session status
                     */
                    //((MainActivity) getActivity()).checkExistingRequest();

                //}
            }
        }, SPLASH_TIME_OUT);
    }


    @Override
    protected void onFragmentResumed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((MainActivity) getActivity()).setStatusBarColor(R.color.splash_status_bar_color);
        }else{
            ((MainActivity) getActivity()).setWindowFullScreen(true);
        }
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.GONE);


    }
}
