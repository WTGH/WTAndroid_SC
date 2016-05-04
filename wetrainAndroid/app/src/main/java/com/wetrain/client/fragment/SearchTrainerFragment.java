package com.wetrain.client.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.Utills;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;
import com.wetrain.client.customviews.dotprogressbar.DottedProgressBar;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 12/31/15.
 */

public class SearchTrainerFragment extends WeTrainBaseFragment {


    private static final int[] FUN_FACTS_TEXT_ARRAY = { R.string.funfact1,
                                                        R.string.funfact2,
                                                        R.string.funfact3,
                                                        R.string.funfact4,
                                                        R.string.funfact5,
                                                        R.string.funfact6,
                                                        R.string.funfact7,
                                                        R.string.funfact8,
                                                        R.string.funfact9,
                                                        R.string.funfact10,
                                                        R.string.funfact11,
                                                        R.string.funfact12,
                                                        R.string.funfact13,
                                                        R.string.funfact14,
                                                        R.string.funfact15};

    private static final int[] BG_IMG_ARRAY = {R.drawable.bg_workout1,
                                                R.drawable.bg_workout2,
                                                R.drawable.bg_workout3};


    public static ParseObject currentWorkoutRequestObj;

    private Handler updateRequestStatusHandler;

    private String currentWorkoutState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_trainner_screen, container, false);


        ((LinearLayout) view.findViewById(R.id.search_trainer_lyt)).setBackgroundResource(BG_IMG_ARRAY[0]);



        ((TextSwitcher) view.findViewById(R.id.request_message_txt)).setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                TextView textView = new TextView(getActivity());
                TextSwitcher.LayoutParams lp = new TextSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.workout_desc_title_txt_size) / getResources().getDisplayMetrics().density);
                textView.setTextColor(Color.WHITE);
                textView.setLayoutParams(lp);
                textView.setPadding(5, 5, 5, 5);
                return textView;
            }
        });

        // set the animation type of textSwitcher
        ((TextSwitcher) view.findViewById(R.id.request_message_txt)).setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
        ((TextSwitcher) view.findViewById(R.id.request_message_txt)).setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right));



        ((DottedProgressBar) view.findViewById(R.id.progress)).startProgress();


        //((TextSwitcher) view.findViewById(R.id.request_message_txt)).setCurrentText(getString(R.string.take_few_minutes));

        if(currentWorkoutRequestObj != null){
            currentWorkoutState = currentWorkoutRequestObj.containsKey("status") ? currentWorkoutRequestObj.getString("status") : "";
            if(currentWorkoutState.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString()) || currentWorkoutState.equals(Constants.WORKOUT_REQUEST_STATE.Training.toString())){
                //move to Trainer Screen
                moveToTrainerInfoScreen();

            }else{
                startRequestUpdateHandler();

            }
        }



        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onPause() {
        super.onPause();
        stopRequestUpdateHandler();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            stopRequestUpdateHandler();
        }
    }

    private Runnable updateRequestStatusRunnable = new Runnable() {
        @Override
        public void run() {
            if(updateRequestStatusHandler == null) return;


            int random = new Random().nextInt(BG_IMG_ARRAY.length);
            ((LinearLayout) getView().findViewById(R.id.search_trainer_lyt)).setBackgroundResource(BG_IMG_ARRAY[random]);


            updateCurrentWorkoutStatus();



            if(ParseUser.getCurrentUser() != null){
                if (updateRequestStatusHandler != null) {
                    updateRequestStatusHandler.postDelayed(this, Constants.UPDATE_REQUEST_INTERVAL);
                }
            }

        }
    };


    private void updateCurrentWorkoutStatus(){
        if(ParseUser.getCurrentUser() == null) return;

        ParseObject clientObj = ParseUser.getCurrentUser().getParseObject("client");
        ParseObject workoutRequest = clientObj.getParseObject("workout");
        if(workoutRequest != null) {
            workoutRequest.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    currentWorkoutRequestObj = object;
                    if (currentWorkoutRequestObj != null) {
                        String status = currentWorkoutRequestObj.containsKey("status") ? currentWorkoutRequestObj.getString("status") : "";
                        if (status.equals(Constants.WORKOUT_REQUEST_STATE.Complete.toString())) {
                            if (currentWorkoutRequestObj.getObjectId() != null) {
                                if (currentWorkoutRequestObj.get("clientRating") == null) {
                                    // show Rating view
                                    ((MainActivity) getActivity()).showTrainerRatingAlert(currentWorkoutRequestObj);
                                }
                            }
                        }

                        toggleRequestStatus(status);

                    } else {
                        toggleRequestStatus(Constants.WORKOUT_REQUEST_STATE.NoRequest.toString());

                    }
                }
            });

        }else{
            toggleRequestStatus(Constants.WORKOUT_REQUEST_STATE.NoRequest.toString());

        }

    }

    private void toggleRequestStatus(String requestState){
        if(currentWorkoutRequestObj == null) return;

        currentWorkoutState = requestState;

        if(requestState.equals(Constants.WORKOUT_REQUEST_STATE.NoRequest.toString())) {
            String title = "No current workout";
            String message = "You're not currently in a workout or waiting for a trainer. Please click OK to go back to the training menu.";

            updateTitle(title, message, "Close", moveToHomeViewClickListener);

            stopRequestUpdateHandler();

            if(getView() != null) {
                ((DottedProgressBar) getView().findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
                ((DottedProgressBar) getView().findViewById(R.id.progress)).stopProgress();
            }

        }else if(requestState.equals(Constants.WORKOUT_REQUEST_STATE.Cancelled.toString())) {
            String title = "Search was cancelled";
            String message = "You have cancelled the training session. You have not been charged for this training session since no trainer was matched. Please click OK to go back to the training menu.";
            if (currentWorkoutRequestObj.getString("cancelReason") != null) {
                message = currentWorkoutRequestObj.getString("cancelReason");
            }

            updateTitle(title, message, "Ok", moveToHomeViewClickListener);
            stopRequestUpdateHandler();

            unsubscribeToCurrentRequestChannel();

            if(getView() != null) {
                ((DottedProgressBar) getView().findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
                ((DottedProgressBar) getView().findViewById(R.id.progress)).stopProgress();
            }
        }else if(requestState.equals(Constants.WORKOUT_REQUEST_STATE.Searching.toString())) {

            String title = "Searching for a Trainer";
            String message = "Please be patient this should only take a few minutes. In the meantime feel free to close the app. We will let you know once a trainer has been matched!";

            updateTitle(title, message, "Cancel Request", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).showAlertDialog("Cancel request?",
                            "Are you sure you want to cancel your training request?",
                            "Keep waiting", null,
                            "Cancel training", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(currentWorkoutRequestObj != null){
                                        currentWorkoutRequestObj.put("status", Constants.WORKOUT_REQUEST_STATE.Cancelled.toString());
                                        currentWorkoutRequestObj.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                toggleRequestStatus(Constants.WORKOUT_REQUEST_STATE.Cancelled.toString());
                                            }
                                        });
                                    }
                                }
                            }, false);

                }
            });
            if(getView() != null) {
                ((DottedProgressBar) getView().findViewById(R.id.progress)).startProgress();
            }
        }else if(requestState.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString())) {
            String title = "Trainer found";
            String message = "You have been matched with a trainer!";
            updateTitle(title, message, "Cancel Request", null);
            stopRequestUpdateHandler();

            unsubscribeToCurrentRequestChannel();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveToTrainerInfoScreen();

                }
            }, 200);

            if(getView() != null) {
                ((DottedProgressBar) getView().findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
                ((DottedProgressBar) getView().findViewById(R.id.progress)).stopProgress();
            }

        }else if(requestState.equals(Constants.WORKOUT_REQUEST_STATE.Training.toString())){
            String title = "Training in session";
            String message = "";
            updateTitle(title, message, "Cancel Request", null);
            stopRequestUpdateHandler();

            unsubscribeToCurrentRequestChannel();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveToTrainerInfoScreen();

                }
            }, 200);


            if(getView() != null) {
                ((DottedProgressBar) getView().findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
                ((DottedProgressBar) getView().findViewById(R.id.progress)).stopProgress();
            }

        }

    }

    private void moveToTrainerInfoScreen(){

        FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, false);
        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.TrainerScreenFragmentTag, false, true);
        TrainerScreenFragment.currentWorkoutRequest = currentWorkoutRequestObj;

    }

    private  void startRequestUpdateHandler(){
        if(updateRequestStatusHandler == null) {
            updateRequestStatusHandler = new Handler();
            updateRequestStatusHandler.post(updateRequestStatusRunnable);
        }

    }
    private void stopRequestUpdateHandler(){
        if(updateRequestStatusHandler != null){
            updateRequestStatusHandler.removeCallbacks(updateRequestStatusRunnable);

        }
        //updateRequestStatusRunnable = null;
        updateRequestStatusHandler = null;

        Log.d("TEST", updateRequestStatusHandler+">>>stopRequestUpdateHandler>>>>"+updateRequestStatusHandler);

    }

    private View.OnClickListener moveToHomeViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Move to home screen
            FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, false);
            FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.HomeFragmenttag, false, false);

        }
    };

    private void unsubscribeToCurrentRequestChannel(){
        try {
            if (currentWorkoutRequestObj != null) {
                if (currentWorkoutRequestObj.getObjectId() != null) {
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();

                    String channelName = "workout_" + currentWorkoutRequestObj.getObjectId();

                    List channelArray = installation.getList("channels");
                    channelArray.remove(channelName);


                    installation.put("channels", channelArray);
                    installation.put("userId", currentWorkoutRequestObj.getObjectId());

                    installation.saveInBackground();

                }
            }
        }catch (Exception e){

        }

    }

    private void updateTitle(String title, String message, String buttonTxt, View.OnClickListener buttonClickListener){
        if(getActivity() == null || getView() == null || !isVisible()) return;

        ((MainActivity)getActivity()).setTitle(title);
        ((TextSwitcher) getView().findViewById(R.id.request_message_txt)).setCurrentText(message);
        ((Button) getView().findViewById(R.id.cancel_request_btn)).setText(buttonTxt);
        ((Button) getView().findViewById(R.id.cancel_request_btn)).setOnClickListener(buttonClickListener);

    }

    /*private Runnable updateFunFactsRunnable = new Runnable() {
        @Override
        public void run() {
            ((TextSwitcher) getView().findViewById(R.id.request_message_txt)).setText(getString(FUN_FACTS_TEXT_ARRAY[textIndex]));
            textIndex++;

            if (textIndex >= 15){
                textIndex = 0;
            }


        }
    };*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRequestUpdateHandler();

        currentWorkoutRequestObj = null;
    }



    @Override
    protected void onFragmentResumed() {
        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setTitle("Searching for a Trainer");
        ((MainActivity) getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.TitleType);

        ((MainActivity)getActivity()).setNavigationTextVisibility(View.GONE);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);

        if(currentWorkoutState.equals(Constants.WORKOUT_REQUEST_STATE.Searching.toString())) {
            startRequestUpdateHandler();
        }

    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }



}
