package com.wetrain.client.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;

/**
 * Created by test on 11/17/15.
 */
public abstract class WeTrainBaseFragment extends Fragment {

    public static long buttonLastClickedTime;


    @Override
    public void onResume() {
        super.onResume();
        if(isVisible())
            onFragmentResumed();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
            onFragmentResumed();
    }

    protected abstract void onFragmentResumed();

    public boolean canTriggerClickEvent(){
        if (SystemClock.elapsedRealtime() - buttonLastClickedTime < 500){
            return false;
        }
        buttonLastClickedTime = SystemClock.elapsedRealtime();

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
