package com.wetrain.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

/**
 * Created by test on 2/25/16.
 */
public class TermsConditionFragment extends WeTrainBaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.terms_condition_view, container, false);


        return v;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(enter){
            Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((TextView) getView().findViewById(R.id.terms_cond_txt)).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) getView().findViewById(R.id.terms_cond_txt)).setText(Constants.TERMS_CONDITIONS);

                        }
                    }, 100);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            return anim;

        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    protected void onFragmentResumed() {
        ((MainActivity) getActivity()).setStatusBarColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setTitle("Terms of Service & Privacy");
        ((MainActivity) getActivity()).setActionBarBgColor(R.color.account_top_bar_clr);
        ((MainActivity) getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_BLUE_BACK);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setNavigationText("Back");

    }
}
