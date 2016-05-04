package com.wetrain.client.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

/**
 * Created by test on 2/18/16.
 */
public class WorkoutInfoFragment extends WeTrainBaseFragment implements CustomActionBar.ActionBarOptionsButtonClickListener{

    public static final String BUNDLE_WORKOUT_TYPE_KEY = "BundleWorkoutTypeKey";

    String workoutTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int woType = getArguments().getInt(BUNDLE_WORKOUT_TYPE_KEY, 0);
        workoutTitle = Constants.WORKOUT_TITLES.get(woType);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void checkExistWorkoutStatus(){
        //check exist user workout session
        if(ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().getParseObject("client") != null) {
                final ParseObject clientParseObj = ParseUser.getCurrentUser().getParseObject("client");
                if(clientParseObj != null) {
                    ((MainActivity) getActivity()).showProgressDialog();

                    clientParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            final ParseObject workoutParseObj = clientParseObj.getParseObject("workout");
                            if (workoutParseObj != null) {
                                workoutParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        ((MainActivity) getActivity()).closeProgerssDialog();
                                        if(workoutParseObj.containsKey("status")){
                                            if (workoutParseObj.get("status") != null) {
                                                String status = workoutParseObj.getString("status");
                                                status = status == null ? "" : status;
                                                Log.d("TEST", ">>>" + status);

                                                if ((status.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString()) || status.equals(Constants.WORKOUT_REQUEST_STATE.Searching.toString()))) {
                                                    ((MainActivity) getActivity()).setOptionButtonVisibility(View.GONE);
                                                }else{
                                                    ((MainActivity) getActivity()).setOptionButtonVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }
                                });
                            }else{
                                ((MainActivity) getActivity()).closeProgerssDialog();

                            }
                        }
                    });
                }


            }
        } else {
            ((MainActivity) getActivity()).setOptionButtonVisibility(View.VISIBLE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.workout_info_view, container, false);

        ((TextView) v.findViewById(R.id.workout_info_txt)).setText(Constants.GET_WORKOUT_DESC(workoutTitle));
        ((ImageView) v.findViewById(R.id.workout_icon_img)).setImageResource(Constants.GET_WORKOUT_DESC_DRAWABLE_ICON(workoutTitle));
        return v;
    }

    @Override
    public void onOptionsButtonClicked() {
        FragmentHolder.removeAccountTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.AccountFragmentTag, false);
        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.HomeFragmenttag, false, true);
        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.DurationFragmentTag, false, true);

    }

    @Override
    protected void onFragmentResumed() {

        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity) getActivity()).setTitle(workoutTitle);
        ((MainActivity) getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_WHITE_BACK);
        ((MainActivity) getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).setNavigationText("Back");
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.NavigationType);
        ((MainActivity) getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.TitleType);
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(R.color.bottom_bar_text_selected_clr), CustomActionBar.OptionType);
        ((MainActivity)getActivity()).setOptionsButtonLabel("Train Now");
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.OptionType);
        ((MainActivity)getActivity()).setActionbarButtonClickListener(this);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);

        checkExistWorkoutStatus();


    }




}
