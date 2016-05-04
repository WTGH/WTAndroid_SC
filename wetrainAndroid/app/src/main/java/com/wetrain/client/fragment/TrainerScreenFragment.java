package com.wetrain.client.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.Utills;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 12/31/15.
 */
public class TrainerScreenFragment extends WeTrainBaseFragment {
    public static ParseObject currentWorkoutRequest;
    private ParseObject trainerObj;
    private ParseObject currentPromoCodeObj;

    private Handler woRequestUpdateHandler;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trainer_screen, null);

        ((Button) view.findViewById(R.id.trainer_contact_btn)).setOnClickListener(contactTrainerClickListener);

        if(currentWorkoutRequest != null) {
            String channelName = "workout_" + currentWorkoutRequest.getObjectId();
            Utills.removeScheduleDataInPref(getActivity(), channelName);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(currentWorkoutRequest != null){
            trainerObj = currentWorkoutRequest.getParseObject("trainer");
            trainerObj.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    trainerObj = object;
                    currentPromoCodeObj = currentWorkoutRequest.getParseObject("promoCode");
                    if(currentPromoCodeObj != null){
                        currentPromoCodeObj.fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                updateTrainerInfo();
                                startRequestUpdateHandler();
                            }
                        });
                    }else {
                        updateTrainerInfo();
                        startRequestUpdateHandler();
                    }
                }
            });
        }
    }

    private Runnable woRequestUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if(currentWorkoutRequest != null) {
                currentWorkoutRequest.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        currentWorkoutRequest = object;
                        refreshWoState();

                    }
                });

                woRequestUpdateHandler.postDelayed(woRequestUpdateRunnable, Constants.UPDATE_REQUEST_INTERVAL);
            }
        }
    };

    private  void startRequestUpdateHandler(){
        woRequestUpdateHandler = new Handler();
        woRequestUpdateHandler.post(woRequestUpdateRunnable);

    }
    private void stopRequestUpdateHandler(boolean moveHomeScreen){
        if(woRequestUpdateHandler != null){
            woRequestUpdateHandler.removeCallbacks(woRequestUpdateRunnable);

        }
        woRequestUpdateRunnable = null;
        woRequestUpdateHandler = null;

        //Move to Home screen
        if(moveHomeScreen) {
            FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, true);
            FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.HomeFragmenttag, false, true);

        }
    }

    private void refreshWoState(){
        if(getView() == null || trainerObj == null) return;

        String status = currentWorkoutRequest.containsKey("status") ? currentWorkoutRequest.getString("status") : "";

        if(status.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString())){
            ((Button) getView().findViewById(R.id.trainer_contact_btn)).setText("Contact " + trainerObj.getString("firstName"));
            ((Button) getView().findViewById(R.id.trainer_contact_btn)).setEnabled(true);

        }else if(status.equals(Constants.WORKOUT_REQUEST_STATE.Training.toString())){
            ((Button) getView().findViewById(R.id.trainer_contact_btn)).setText("Workout In Progress");
            ((Button) getView().findViewById(R.id.trainer_contact_btn)).setEnabled(true);

        }else if(status.equals(Constants.WORKOUT_REQUEST_STATE.Complete.toString())){
            ((Button) getView().findViewById(R.id.trainer_contact_btn)).setText("Workout Complete");
            ((Button) getView().findViewById(R.id.trainer_contact_btn)).setEnabled(true);

            stopRequestUpdateHandler(true);

            //check for Rating
            if(currentWorkoutRequest.getParseObject("clientRating") == null){
                //show Rating view
                ((MainActivity) getActivity()).showTrainerRatingAlert(currentWorkoutRequest);
            }

        }else if(status.equals(Constants.WORKOUT_REQUEST_STATE.Cancelled.toString())){
            ((Button) getView().findViewById(R.id.trainer_contact_btn)).setText("Contact " + trainerObj.getString("firstName"));
            ((Button) getView().findViewById(R.id.trainer_contact_btn)).setEnabled(true);
            stopRequestUpdateHandler(false);

        }

        updateTrainerInfo();
    }



    private void updateTrainerInfo(){
        if(trainerObj == null || currentWorkoutRequest == null || getView() == null) return;

        ParseFile userAvatorImgFile = trainerObj.getParseFile("photo");
        if(userAvatorImgFile != null) {
            userAvatorImgFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if(e == null) {
                        Bitmap trainerAvatorBmpImg = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (trainerAvatorBmpImg != null) {
                            if(getView() != null) {
                                if(((ImageView) getView().findViewById(R.id.trainer_avator_img)) != null) {
                                    ((ImageView) getView().findViewById(R.id.trainer_avator_img)).setImageBitmap(trainerAvatorBmpImg);
                                }
                            }
                        }
                    }
                }
            });
        }

        String trainerName = trainerObj.getString("firstName") + " " + trainerObj.getString("lastName") == null ? "" : trainerObj.getString("lastName");
        ((TextView) getView().findViewById(R.id.trainer_name_txt)).setText(trainerName);


        String woStatus = currentWorkoutRequest.containsKey("status") ? currentWorkoutRequest.getString("status") : "" ;

        String trainerInfoTxt = "";


        /*
         * Passcode info
         */
        if(woStatus.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString())){
            String passCode = null;
            try {
                currentPromoCodeObj = currentWorkoutRequest.getParseObject("promoCode");
                if (currentPromoCodeObj != null) {
                    if (currentPromoCodeObj.containsKey("promoCode")) {
                        if (currentPromoCodeObj.getString("promoCode") != null) {
                            passCode = currentPromoCodeObj.getString("promoCode");

                        }
                    }
                }
            }catch(Exception e){

            }




            if(passCode == null){
                passCode = currentWorkoutRequest.getString("passcode");

            }else if(passCode.isEmpty()){
                passCode = currentWorkoutRequest.getString("passcode");

            }

            trainerInfoTxt = "Tell your trainer the passcode for today's workout:<br/><b>" +  passCode.toUpperCase() +"</b><br/><br/>";


        }

        /*
         * Trainer BIO
         */
        if(trainerObj.getString("bio") != null){
            trainerInfoTxt += "About " + trainerObj.getString("firstName") + ":<br/><br/>" + trainerObj.getString("bio") +"<br/><br/>";
        }

        if(woStatus.equals(Constants.WORKOUT_REQUEST_STATE.Cancelled.toString())){
            if(trainerObj.getString("firstName") != null) {
                trainerInfoTxt += trainerObj.getString("firstName") + " cancelled the workout.";

            }else{
                trainerInfoTxt += "Your trainer cancelled the workout.";

            }

            ((MainActivity)getActivity()).setNavigationText("Close");


        }


        /*
         * Trainer Average Rating
         */
        DecimalFormat format = new DecimalFormat("0.0");
        format.setRoundingMode(RoundingMode.CEILING);
        double avgRating = (double) trainerObj.getDouble("avgRating");

        if(avgRating > 0){
            ((TextView) getView().findViewById(R.id.trainer_avg_rating_txt)).setText("Average Rating: " + format.format(avgRating));
        }else{
            ((TextView) getView().findViewById(R.id.trainer_avg_rating_txt)).setText("");
        }

        ((TextView) getView().findViewById(R.id.trainer_bio_txt)).setText(Html.fromHtml(trainerInfoTxt));

    }

    @Override
    protected void onFragmentResumed() {
        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setTitle("");
        ((MainActivity) getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity) getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setNavigationText("Cancel");

    }





    public void onActionBarCancelBtnClicked(){
        showCancelWorkoutAlert();

    }

    private View.OnClickListener contactTrainerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(((Button)v).getText().toString().equals("Workout Complete")){
                //Move to Home screen
                FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, true);
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.HomeFragmenttag, false, true);

            }else if(((Button)v).getText().toString().equals("Workout In Progress")){
                showCancelWorkoutAlert();
            }else {
                showContactTrainerAlert();
            }
        }
    };

    private void showCancelWorkoutAlert(){
        String title = "End session?";
        String buttonTitle = "End session";
        String message = "You seem to be in a training session. Do you want to end it?";
        String woStatus = currentWorkoutRequest.containsKey("status") ? currentWorkoutRequest.getString("status") : "";

        boolean addContactTrainerBtn = false;


        if(woStatus.equals(Constants.WORKOUT_REQUEST_STATE.Training.toString())){
            Date startDate = currentWorkoutRequest.getDate("start");
            Date endDate = currentWorkoutRequest.getDate("end");
            if(endDate != null){
                // if workout has ended but somehow we still have a timer, just end it
                stopRequestUpdateHandler(true);
                return;
            }

            int workoutMinutes = currentWorkoutRequest.getInt("length") * 60;
            long interval = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - startDate.getTime());
            if(interval > (60 * workoutMinutes)){
                message = "You seem to be in a training session that may have already ended. Do you want to close this session?";

            }


        } else if(woStatus.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString())){
            addContactTrainerBtn = true;
            // matched, but not started yet
            title = "Cancel session?";
            buttonTitle = "Cancel session";
            message = "Your session hasn't started yet. Do you want to cancel the session?";



        }else if(woStatus.equals(Constants.WORKOUT_REQUEST_STATE.Cancelled.toString())){
            FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, true);
            FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.HomeFragmenttag, false, false);
            return;
        }

       ((MainActivity) getActivity()).showAlertDialog(title, message,
               buttonTitle, new View.OnClickListener() {
                               @Override
                               public void onClick(final View v) {
                                   if(currentWorkoutRequest != null){
                                       currentWorkoutRequest.put("status", ((Button) v).getText().toString().equals("End session") ? Constants.WORKOUT_REQUEST_STATE.Complete.toString() : Constants.WORKOUT_REQUEST_STATE.Cancelled.toString()) ;
                                       currentWorkoutRequest.saveInBackground(new SaveCallback() {
                                           @Override
                                           public void done(ParseException e) {
                                                stopRequestUpdateHandler(true);

                                               if(((Button) v).getText().toString().equals("End session")) {
                                                   //check for Rating
                                                   if (currentWorkoutRequest.getParseObject("clientRating") == null) {
                                                       //show Rating view
                                                       ((MainActivity) getActivity()).showTrainerRatingAlert(currentWorkoutRequest);
                                                   }
                                               }
                                           }
                                       });
                                   }
                               }
                           },
               (addContactTrainerBtn ? "Contact Trainer" : null), (addContactTrainerBtn ? contactTrainerClickListener : null),
                "Go Back", null, true);
    }

    private void showContactTrainerAlert(){

        String name = trainerObj.getString("firstName");
        final String mobNumber = trainerObj.getString("phone");
        if(mobNumber == null){
            ((MainActivity) getActivity()).showAlertDialog("Could not contact trainer", "The number we had for "+name+" was invalid.", "Ok", null);
            return;
        } else if(mobNumber.isEmpty()){
            ((MainActivity) getActivity()).showAlertDialog("Could not contact trainer", "The number we had for "+name+" was invalid.", "Ok", null);
            return;
        }



        View alertView = getActivity().getLayoutInflater().inflate(R.layout.contact_trainer_alert, null);
        final Dialog alert = new Dialog(getActivity(), R.style.contact_trainer_dialog_theme);
        alert.getWindow().setContentView(alertView);

        ((Button) alertView.findViewById(R.id.call_trainer_btn)).setText("Call " + name);
        alertView.findViewById(R.id.call_trainer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();

                //start dialer intent
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mobNumber + ""));
                startActivity(intent);

            }
        });

        ((Button) alertView.findViewById(R.id.message_trainer_btn)).setText("Text " + name);
        alertView.findViewById(R.id.message_trainer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:" +mobNumber+ ""));
                startActivity(intent);
            }
        });

        alertView.findViewById(R.id.dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();

            }
        });


        alert.show();
    }


}
