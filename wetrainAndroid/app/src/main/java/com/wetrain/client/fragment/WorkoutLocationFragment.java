package com.wetrain.client.fragment;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.KeyListener;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.Utills;
import com.wetrain.client.activity.AlarmReceiver;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;
import com.wetrain.client.customviews.DetectSoftKeyPadLayout;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 12/29/15.
 */
public class WorkoutLocationFragment extends WeTrainBaseFragment implements DetectSoftKeyPadLayout.KeyPadListener {

    //public static final String BUNDLE_SCHECULE_PARSE_OBJ_KEY = "BundleParseScheduleObject";

    private static int SCHEDULE_ALARM_REQUEST_CODE;

    public static int WORKOUT_SESSION_TYPE;
    public static int WORKOUT_TYPE;
    public static int WORKOUT_LENGTH;
    public static Calendar WORKOUT_SCHEDULE_TIME;

    public static ParseObject SCHEDULE_PARSE_OBJ;


    public static final int WORKOUT_SESSION_TRAIN_NOW = 1;
    public static final int WORKOUT_SESSION_TRAIN_LATER = 2;



    private GoogleMap mMap;

    private String wSessionAddress;
    private LatLng wSessionLocation;
    private PopupWindow transactionFeePopupView;

    boolean isAppFromScheduleNotification;

    private KeyListener addressKeyListener;

    private ParseObject enteredPromoCodeObj;

    private boolean verifiedPromoCode;


    private Dialog scheduleConfirmAlert;

    //private String lastEnteredPromoCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * Manually call onFragmentResumed when ap from notification
         */
        onFragmentResumed();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.workout_location_screen, container, false);

        verifiedPromoCode = false;


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        mMap = mapFragment.getMap();


        if(WORKOUT_SESSION_TYPE == WORKOUT_SESSION_TRAIN_LATER){
            ((Button)view.findViewById(R.id.request_sessions)).setText("Schedule a session");
        }


        ((Button)view.findViewById(R.id.request_sessions)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTrainingSession();

            }
        });

        /*((EditText) view.findViewById(R.id.workout_location_address_txt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        ((EditText) v).setKeyListener(addressKeyListener);
                        ((EditText) v).setCursorVisible(true);
                        ((EditText) v).setText(((EditText) v).getText());
                        ((EditText) v).setSelection(((EditText) v).getText().length());

                        Utills.showSoftKeyboard(v);

                        Log.d("TEST", "Selection start=" + ((EditText) v).getSelectionStart() + " end=" + ((EditText) v).getSelectionEnd());

                    }
                });

            }
        });*/

        ((EditText) view.findViewById(R.id.workout_location_address_txt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    getView().findViewById(R.id.workout_location_close_btn).setVisibility(View.INVISIBLE);
                } else {
                    getView().findViewById(R.id.workout_location_close_btn).setVisibility(View.VISIBLE);
                }

            }
        });

        addressKeyListener = ((EditText) view.findViewById(R.id.workout_location_address_txt)).getKeyListener();

        ((EditText) view.findViewById(R.id.workout_location_address_txt)).setKeyListener(null);
        ((EditText) view.findViewById(R.id.workout_location_address_txt)).setFocusable(true);
        ((EditText) view.findViewById(R.id.workout_location_address_txt)).setFocusableInTouchMode(true);
        ((EditText) view.findViewById(R.id.workout_location_address_txt)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((EditText) v).setKeyListener(addressKeyListener);
                    ((EditText) v).setCursorVisible(true);
                    ((EditText) v).setText(((EditText) v).getText());
                    ((EditText) v).setSelection(((EditText) v).getText().length());
                    ((EditText) v).setFocusable(true);
                    ((EditText) v).setFocusableInTouchMode(true);

                    ((EditText) v).requestFocus();

                    //v.performClick();
                    Utills.showSoftKeyboard(v);

                    //return true;
                }
                return false;
            }
        });



        ((EditText) view.findViewById(R.id.workout_location_address_txt)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String YouEditTextValue = v.getText().toString();
                    if (YouEditTextValue.length() > 0) {
                        Utills.hideSoftKeyboard(v);
                        getLocationFromAddress(YouEditTextValue, true);

                    }
                    return true;
                }
                return false;
            }
        });






        view.findViewById(R.id.workout_location_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) getView().findViewById(R.id.workout_location_address_txt)).setText("");

                // Obtain MotionEvent object
                long downTime = SystemClock.uptimeMillis();
                long eventTime = SystemClock.uptimeMillis() + 100;
                float x = 0.0f;
                float y = 0.0f;
                // List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
                int metaState = 0;
                MotionEvent motionEvent = MotionEvent.obtain(
                        downTime,
                        eventTime,
                        MotionEvent.ACTION_UP,
                        x,
                        y,
                        metaState
                );

                // Dispatch touch event to view
                ((EditText) getView().findViewById(R.id.workout_location_address_txt)).dispatchTouchEvent(motionEvent);
                //((EditText) getView().findViewById(R.id.workout_location_address_txt)).performClick();

            }
        });



        return view;

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
         * check is from Schedule Notification Reminder
         */
        onScheduleNotificationReceived();

        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).removeLayoutChangeListener();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setKeyPadListener(this, true);



    }

    private void initMap(){

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnCameraChangeListener(mapPositionChangeListener);

        Location currentLocation = null;
        if (((MainActivity) getActivity()).locationService != null && !isAppFromScheduleNotification) {
            currentLocation = ((MainActivity) getActivity()).locationService.getCurrentLocation();

            if (currentLocation != null) {
                LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                getAddressForLocation(location, true);
            }
        }
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(enter){
            try {
                Animation enterAnim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
                enterAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                              initMap();
                            }
                        }, 300);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                return enterAnim;
            } catch (Resources.NotFoundException e){
            }
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void showTransacrionFeeMsgPopup(View anchorView){
        if(getActivity() == null) return;

        if(transactionFeePopupView == null) {
            View popupView = getActivity().getLayoutInflater().inflate(R.layout.workout_location_popup_window, null);
            transactionFeePopupView = new PopupWindow(popupView, getResources().getDimensionPixelOffset(R.dimen.confirm_alert_trans_fee_msg_popup_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);

            transactionFeePopupView.setOutsideTouchable(true);
            transactionFeePopupView.setBackgroundDrawable(new BitmapDrawable());

            ((TextView) popupView.findViewById(R.id.txtView)).setMovementMethod(new ScrollingMovementMethod());

        }

        if(transactionFeePopupView.isShowing()){
            transactionFeePopupView.dismiss();
        }else{
            transactionFeePopupView.showAsDropDown(anchorView, 0, 0);
        }
    }

    private void requestTrainingSession(){

        if(ParseUser.getCurrentUser() == null){

            ((MainActivity) getActivity()).showAlertDialog("Please Login or Sign up", "Before you start using WeTrain, you must create a user profile!",
                    "Log in", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.LoginFragmentTag, false, false);

                        }
                    },
                    "Sign up", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.SignUpFragmentTag, false, false);

                        }
                    },
                    "Close", null, true);




        } else if(!isServiceAvailableForEnteredLocation(false)){
            ((MainActivity) getActivity()).showAlertDialog("Sorry!",
                    "You are outside the on-demand booking area. Don't worry - just use our handy scheduler to book a session at least an hour in advance so we can get you working out in no time!", "Close", null,
                    "Let's Schedule!",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHolder.removeFragment(getFragmentManager(), FragmentHolder.FragmentTags.WorkoutLocationFragmentTag.name());
                            FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.ScheduleDatePickerFragmentTag, false, true);

                        }
                    }, false);

        } else{
            final ParseObject clientParseObj = ParseUser.getCurrentUser().getParseObject("client");

            if(clientParseObj != null) {
                ((MainActivity) getActivity()).showProgressDialog();

                clientParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        ((MainActivity) getActivity()).closeProgerssDialog();
                        if(e == null) {

                            //check credit card
                            if(clientParseObj.getString("card") == null){
                                showProfileInCompleteAlert();
                                return;

                            } else if(clientParseObj.getString("card").isEmpty()){
                                showProfileInCompleteAlert();
                                return;

                            }

                            //check user phone
                            if(clientParseObj.getString("phone") == null){
                                showProfileInCompleteAlert();
                                return;

                            } else if(clientParseObj.getString("phone").isEmpty()){
                                showProfileInCompleteAlert();
                                return;

                            }

                            //check Term condition
                            if(!clientParseObj.getBoolean("checkedTOS")){
                                showProfileInCompleteAlert();
                                return;

                            }

                            //check facebook sigin user's email
                            boolean isLinkedWithFacebook = ParseFacebookUtils.isLinked(ParseUser.getCurrentUser());
                            if(isLinkedWithFacebook){
                                if(ParseUser.getCurrentUser().getString("email") == null){
                                    showProfileInCompleteAlert();
                                    return;

                                }else if(ParseUser.getCurrentUser().getString("email").isEmpty()){
                                    showProfileInCompleteAlert();
                                    return;

                                }
                            }

                            //show confirm session alert
                            showScheduleConfirmAlert();


                        } else {
                            ((MainActivity) getActivity()).showAlertDialog("Error", "Can't create session." + e.getMessage(), "Ok", null);

                        }

                    }
                });
            }else{
                // user's client was not found
                ((MainActivity) getActivity()).showAlertDialog("Message", "User not found. Before you start using WeTrain, you must create a user! Sign up now?", "Close", null);

            }

        }

    }

    private void showProfileInCompleteAlert(){
        ((MainActivity) getActivity()).showAlertDialog("Please complete profile",
                "We need you to fill out some required information so we can send you a trainer! It only takes a minute, and the more you tell us, the more we can customize your workout!", "Update My Profile", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.UserProfileFragmentTag, false, false);
                    }
                }, null, null, false);

    }



    private boolean isServiceAvailableForEnteredLocation(boolean checkScheduleServiceRange){
        try {
            Location philadelphiaLocation = new Location("philadelphia");
            philadelphiaLocation.setLatitude(Constants.PHILADELPHIA_LAT);
            philadelphiaLocation.setLongitude(Constants.PHILADELPHIA_LON);

            Location wLocation = new Location("");
            wLocation.setLatitude(wSessionLocation.latitude);
            wLocation.setLongitude(wSessionLocation.longitude);

            float distanceInMeters = wLocation.distanceTo(philadelphiaLocation);
            if(WORKOUT_SESSION_TYPE == WORKOUT_SESSION_TRAIN_NOW) {
                if (distanceInMeters <= Constants.SERVICE_RANGE_METERS) {
                    return true;
                }
            }else{
                if(checkScheduleServiceRange) {
                    if (distanceInMeters <= Constants.SCHEDULE_SERVICE_RANGE_METERS) {
                        return true;
                    }
                }else{
                    return true;
                }
            }
        }catch (Exception e){

        }
        return false;

    }

    private boolean isServiceAvailableForCurrentLocation(){
        try{
            Location philadelphiaLocation = new Location("philadelphia");
            philadelphiaLocation.setLatitude(Constants.PHILADELPHIA_LAT);
            philadelphiaLocation.setLongitude(Constants.PHILADELPHIA_LON);

            Location currentLocation = ((MainActivity) getActivity()).locationService.getCurrentLocation();

            float distanceInMeters = currentLocation.distanceTo(philadelphiaLocation);
            if(WORKOUT_SESSION_TYPE == WORKOUT_SESSION_TRAIN_NOW) {
                if (distanceInMeters <= Constants.SERVICE_RANGE_METERS) {
                    return true;
                }
            }else{
                if (distanceInMeters <= Constants.SCHEDULE_SERVICE_RANGE_METERS) {
                    return true;
                }
            }
        }catch (Exception e){

        }
        return false;
    }

    public void onScheduleNotificationReceived(){
        Log.d("SCHEDULE", MainActivity.currentScheduleParseObj+" onScheduleNotificationReceived>>"+getArguments());

        if(getArguments() != null && MainActivity.currentScheduleParseObj != null) {
            isAppFromScheduleNotification = getArguments().getBoolean(AlarmReceiver.INTENT_FROM_NOTIFICATION, false);

            WORKOUT_SESSION_TYPE = WORKOUT_SESSION_TRAIN_LATER;
            WORKOUT_LENGTH = MainActivity.currentScheduleParseObj.getInt("length");
            WORKOUT_TYPE = Constants.WORKOUT_TITLES.indexOf(MainActivity.currentScheduleParseObj.getString("workOutType"));
            WORKOUT_SCHEDULE_TIME = Calendar.getInstance();
            WORKOUT_SCHEDULE_TIME.setTime(MainActivity.currentScheduleParseObj.getDate("scheduledTime"));

            wSessionAddress = MainActivity.currentScheduleParseObj.getString("address");
            enteredPromoCodeObj = MainActivity.currentPromoCodeParseObj;
            wSessionLocation = getLocationFromAddress(wSessionAddress, false);

            if(isAppFromScheduleNotification) {
                showScheduleConfirmAlert();
            }

        }

        if(!isAppFromScheduleNotification){
            //check location service enable state
            ((MainActivity) getActivity()).checkLocationServiceEnabled();
        }

    }


    private void showScheduleConfirmAlert() {
        verifiedPromoCode = false;

        View alertView = getActivity().getLayoutInflater().inflate(R.layout.session_confirm_alert_view, null);

        scheduleConfirmAlert = new Dialog(getActivity(), R.style.session_confirm_dialog);
        scheduleConfirmAlert.getWindow().setContentView(alertView);


        if(WORKOUT_SESSION_TYPE == WORKOUT_SESSION_TRAIN_NOW){
            WORKOUT_SCHEDULE_TIME = Calendar.getInstance();
        }

        String dateSuffix = "";
        if(WORKOUT_SCHEDULE_TIME == null){
            WORKOUT_SCHEDULE_TIME = Calendar.getInstance();
        }
        int sDate = WORKOUT_SCHEDULE_TIME.get(Calendar.DAY_OF_MONTH);


        switch (sDate){
            case 1:
            case 21:
            case 31:
                dateSuffix = "st";
                break;
            case 2:
            case 22:
                dateSuffix = "nd";
                break;
            case 3:
            case 23:
                dateSuffix = "rd";
                break;
            default: dateSuffix = "th";
        }

        String sessionDate = DateFormat.format("MMM. d@ h:mm a", WORKOUT_SCHEDULE_TIME).toString();
        sessionDate = sessionDate.replaceFirst("\\@", dateSuffix + " @");
        ((TextView) alertView.findViewById(R.id.session_confirm_workout_date_time_txt)).setText(sessionDate);

        ((TextView) alertView.findViewById(R.id.session_confirm_workout_type_txt)).setText(Constants.WORKOUT_TITLES.get(WORKOUT_TYPE) + " for " + WORKOUT_LENGTH + " min");
        ((TextView) alertView.findViewById(R.id.session_confirm_address_txt)).setText(wSessionAddress);

        /*double price = 17.0;
        if(WORKOUT_LENGTH == 60){
            price = 25.0;
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        ((TextView) alertView.findViewById(R.id.session_confirm_price_txt)).setText("$ " + decimalFormat.format(price));
        double transactionFee = 0.3 + (0.029 * price);
        ((TextView) alertView.findViewById(R.id.session_confirm_transaction_fee_txt)).setText("$ " + decimalFormat.format(transactionFee));
        double total = price + transactionFee;
        ((TextView) alertView.findViewById(R.id.session_confirm_total_price_txt)).setText("$" + decimalFormat.format(total));*/


        final EditText promoCodeEditTxt = (EditText) alertView.findViewById(R.id.promo_edit_txt);

        promoCodeEditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Utills.hideSoftKeyboard(v);
                    verifyPromoCode(promoCodeEditTxt.getText().toString(), false);
                    return true;
                }
                return false;
            }
        });


        if(enteredPromoCodeObj != null){
            promoCodeEditTxt.setText(enteredPromoCodeObj.getString("promoCode"));

        }


        (alertView.findViewById(R.id.dialog_cancel_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utills.hideSoftKeyboard(v);
                if(scheduleConfirmAlert != null) {
                    scheduleConfirmAlert.dismiss();
                    scheduleConfirmAlert = null;
                }

                if(isAppFromScheduleNotification){
                    isAppFromScheduleNotification = false;
                    MainActivity.currentScheduleParseObj = null;
                }

                enteredPromoCodeObj = null;


            }
        });

        (alertView.findViewById(R.id.dialog_edit_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleConfirmAlert.dismiss();
                scheduleConfirmAlert = null;

                if (WORKOUT_SESSION_TYPE == WORKOUT_SESSION_TRAIN_NOW) {
                    FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.DurationFragmentTag, true);
                    FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.DurationFragmentTag, false, true);

                } else {
                    FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.ScheduleDatePickerFragmentTag, true);
                    FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.ScheduleDatePickerFragmentTag, false, true);

                }

            }
        });

        (alertView.findViewById(R.id.dialog_confirm_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPromoCode(promoCodeEditTxt.getText().toString(), true);

            }
        });

        (alertView.findViewById(R.id.session_confirm_transaction_fee_desc_txt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransacrionFeeMsgPopup(v);

            }
        });

        scheduleConfirmAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        scheduleConfirmAlert.show();


        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
            scheduleConfirmAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }


        setActualDiscountPrice();

    }

    private void setActualDiscountPrice(){
        if(scheduleConfirmAlert == null) return;

        double price = getSessionPrice();

        DecimalFormat precision = new DecimalFormat("0.00");
        precision.setRoundingMode(RoundingMode.CEILING);


        ((TextView) scheduleConfirmAlert.findViewById(R.id.session_confirm_price_txt)).setText("$ " + precision.format(price));
        double transactionFee = 0.3 + (0.029 * price);
        if(price == 0){
            transactionFee = 0;
        }
        ((TextView) scheduleConfirmAlert.findViewById(R.id.session_confirm_transaction_fee_txt)).setText("$ " + precision.format(transactionFee));
        price = price + transactionFee;
        ((TextView) scheduleConfirmAlert.findViewById(R.id.session_confirm_total_price_txt)).setText("$" +precision.format(price));

    }

    private double getSessionPrice(){
        double price = 17;
        if(WORKOUT_LENGTH == 60){
            price = 25;
        }
        double discountPercentage = 0;

        if(enteredPromoCodeObj != null){
            try {
                if(enteredPromoCodeObj.containsKey("discountPercentage")) {
                    discountPercentage = enteredPromoCodeObj.getInt("discountPercentage");
                }
            } catch(Exception e){
                discountPercentage = 0;
            }
        }


        price = price - (price * (discountPercentage / 100));

        return price;
    }


    private void requestWorkoutSession(boolean validPromoCode, boolean isFromConfirmBtn){
        Log.d("TEST", "validPromoCode=" + validPromoCode + " verifiedPromoCode=" + verifiedPromoCode);

        if(validPromoCode) {



            if(isAppFromScheduleNotification){
                verifiedPromoCode = true;
            }

            if(!isFromConfirmBtn){
                verifiedPromoCode = false;
            }


            if(!isFromConfirmBtn){
                ((MainActivity) getActivity()).closeProgerssDialog();
                if(enteredPromoCodeObj != null){
                    setActualDiscountPrice();
                }
                return;
            }

            /*if(!verifiedPromoCode && enteredPromoCodeObj != null){
                verifiedPromoCode = true;
                ((MainActivity) getActivity()).closeProgerssDialog();
                setActualDiscountPrice();

                return;
            }*/


            if (WORKOUT_SESSION_TYPE == WORKOUT_SESSION_TRAIN_NOW) { // Train Now
                sendWorkoutSessionRequest();

            } else {  // Train Later
                if (isAppFromScheduleNotification) {
                    sendScheduleSessionRequest(Constants.SCHEDULE_STATE.Searching, 0);

                } else {
                    /*
                     * if geo miles with in 5 miles philadelphia area and time with in 1 hour
                     * set schedule state to requested and
                     * create workout session
                     */
                    Constants.SCHEDULE_STATE state = Constants.SCHEDULE_STATE.Created;
                    if(isServiceAvailableForCurrentLocation() && Constants.getMinutesBetweenTwoDates(WORKOUT_SCHEDULE_TIME.getTime()) <= 60){
                        state = Constants.SCHEDULE_STATE.Searching;
                    }
                    long calendarId = addScheduleInCalendar();
                    sendScheduleSessionRequest(state, calendarId);

                }
            }


        } else {

            ((MainActivity) getActivity()).closeProgerssDialog();
            verifiedPromoCode = false;
            if(scheduleConfirmAlert != null){
                //lastEnteredPromoCode = ((EditText) scheduleConfirmAlert.findViewById(R.id.promo_edit_txt)).getText().toString();
                ((EditText) scheduleConfirmAlert.findViewById(R.id.promo_edit_txt)).setText("");
                ((EditText) scheduleConfirmAlert.findViewById(R.id.promo_edit_txt)).setHintTextColor(Color.RED);
                ((EditText) scheduleConfirmAlert.findViewById(R.id.promo_edit_txt)).setHint("Invalid Promo Code");
                ((EditText) scheduleConfirmAlert.findViewById(R.id.promo_edit_txt)).setSelected(true);

            }
        }
    }

    /*
     * Train Now
     * Request new Workout session
     */
    private void sendWorkoutSessionRequest(){

        if(wSessionLocation == null){
            ((MainActivity) getActivity()).showAlertDialog("Address not found", null, "Ok", null);
            return;
        }

        final ParseObject workoutRequest = ParseObject.create("Workout");
        workoutRequest.put("lat", wSessionLocation.latitude);
        workoutRequest.put("lon", wSessionLocation.longitude);
        workoutRequest.put("status", Constants.WORKOUT_REQUEST_STATE.Searching.toString());
        workoutRequest.put("address", wSessionAddress);
        workoutRequest.put("type", Constants.WORKOUT_TITLES.get(WORKOUT_TYPE));
        workoutRequest.put("length", WORKOUT_LENGTH);
        workoutRequest.put("time", new Date());

        if (enteredPromoCodeObj != null) {
            workoutRequest.put("promoCode", enteredPromoCodeObj);
        }

        if(Constants.TESTING){
            workoutRequest.put("testing", true);
        }

        final ParseObject clientParseObj = ParseUser.getCurrentUser().getParseObject("client");
        workoutRequest.put("client", clientParseObj);

        workoutRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                /*
                 * Add Workout in Client
                 */
                clientParseObj.put("workout", workoutRequest);
                clientParseObj.saveInBackground();

                if (e == null) {
                    if (workoutRequest.getObjectId() != null) {
                        String channelName = "workout_" + workoutRequest.getObjectId();
                        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                        parseInstallation.addUnique("channels", channelName);

                        parseInstallation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (getActivity() != null)
                                    ((MainActivity) getActivity()).closeProgerssDialog();

                                if (e == null) {
                                    //After successfully commit move to search trainer screen
                                    if (scheduleConfirmAlert != null) {
                                        if (scheduleConfirmAlert.isShowing()) {
                                            scheduleConfirmAlert.dismiss();
                                        }
                                        scheduleConfirmAlert = null;
                                    }

                                    /*
                                     * Move to Search Trainer screen
                                     */
                                    FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.SearchTrainerFragmentTag, false);
                                    FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.SearchTrainerFragmentTag, false, false);
                                    SearchTrainerFragment.currentWorkoutRequestObj = workoutRequest;


                                } else {
                                    ((MainActivity) getActivity()).showAlertDialog("Could not request workout", "There was an issue requesting a training session. Please try again.", "Ok", null);

                                }
                            }
                        });
                    }

                } else {
                    ((MainActivity) getActivity()).closeProgerssDialog();
                    ((MainActivity) getActivity()).showAlertDialog("Could not request workout", "There was an issue requesting a training session. Please try again.", "Ok", null);

                }

            }
        });
    }

    /*
     * Train later
     * Initialte New Workout schedule
     *
     */
    private ParseObject scheduleRequest;
    private void sendScheduleSessionRequest( final Constants.SCHEDULE_STATE state, final long  calendarId){

        scheduleRequest = ParseObject.create("ScheduleInfo");
        if(MainActivity.currentScheduleParseObj != null){
            scheduleRequest = MainActivity.currentScheduleParseObj;

            if(state == Constants.SCHEDULE_STATE.Searching){
                scheduleRequest.put("status", state.toString());
            }else{
                scheduleRequest.put("status", Constants.SCHEDULE_STATE.Edited.toString());
            }

        }else{
            scheduleRequest.put("status", state.toString());
            scheduleRequest.put("calendarEventId" , ""+calendarId);
        }


        scheduleRequest.put("lat", wSessionLocation == null ? 0.0 : wSessionLocation.latitude);
        scheduleRequest.put("lon", wSessionLocation == null ? 0.0 : wSessionLocation.longitude);

        scheduleRequest.put("address", wSessionAddress);
        scheduleRequest.put("workOutType", Constants.WORKOUT_TITLES.get(WORKOUT_TYPE));
        scheduleRequest.put("length", WORKOUT_LENGTH);
        scheduleRequest.put("scheduledTime", WORKOUT_SCHEDULE_TIME.getTime());



        if (enteredPromoCodeObj != null) {
            scheduleRequest.put("promoCode", enteredPromoCodeObj);
        }

        if(Constants.TESTING){
            scheduleRequest.put("testing", true);
        }

        final ParseObject clientParseObj = ParseUser.getCurrentUser().getParseObject("client");
        scheduleRequest.put("client", clientParseObj);

        scheduleRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //After successfully commit move to search trainer screen
                if (scheduleConfirmAlert != null) {
                    if (scheduleConfirmAlert.isShowing()) {
                        scheduleConfirmAlert.dismiss();
                    }
                }
                ((MainActivity) getActivity()).closeProgerssDialog();

                if (e == null) {
                    /*
                     * Add Workout in Client
                     */
                    clientParseObj.put("ScheduleInfo", scheduleRequest);
                    clientParseObj.saveInBackground();

                    if(state == Constants.SCHEDULE_STATE.Searching){
                        ((MainActivity) getActivity()).showProgressDialog();
                        sendWorkoutSessionRequest();

                    }else {
                        /*
                         * set Alarm for notify before 30 minutes the scheduled time
                         */
                        addScheduleNotificationAlarm(calendarId, scheduleRequest.getObjectId());

                        FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, true);
                        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.HomeFragmenttag, false, true);

                        /*
                         * Show schedule confirmed notification
                         */

                        ((MainActivity) getActivity()).showScheduleCreatedNotification();


                    }

                } else {
                    ((MainActivity) getActivity()).showAlertDialog("Could not save workout", "There was an issue on saving. Please try again." + e.getMessage(), "Ok", null);

                }

            }
        });
    }

    /*
     * Create Single time alarm to notify
     * 30 minutes before the Schedule time
     */
    private void addScheduleNotificationAlarm(long calendarID, String schParseObjedtId){
        if(calendarID == 0){
            calendarID = SCHEDULE_ALARM_REQUEST_CODE++;
        }

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putInt(AlarmReceiver.SCHEDULE_ALARM_REQUEST_CODE, (int) calendarID);
        bundle.putString(AlarmReceiver.SCHEDULE_PARSE_OBJCT_ID, schParseObjedtId);
        alarmIntent.putExtras(bundle);


        PendingIntent pendingIntent = PendingIntent.getBroadcast( getActivity(), (int) calendarID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT );


        /*
         * show schedule remainder notification before 90 minutes the schedule time
         */
        Calendar alarmTriggerTime = Calendar.getInstance();
        alarmTriggerTime.setTime(WORKOUT_SCHEDULE_TIME.getTime());
        alarmTriggerTime.add(Calendar.MINUTE, -Constants.SCHEDULE_NOTIFICATION_INTERVAL);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTriggerTime.getTimeInMillis(), pendingIntent);
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTriggerTime.getTimeInMillis(), pendingIntent);

        }
        Log.d("TEST", "Alarm triggered at " + DateFormat.format("MMM. d h:mm a", alarmTriggerTime));


        /*
         * Add Schedule parse object id and schedule time in Preferencr
         */
        Utills.addEditScheduleDataInPref(getActivity(), schParseObjedtId, WORKOUT_SCHEDULE_TIME.getTime().getTime());

    }



    @Override
    public void onPause() {
        super.onPause();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).removeLayoutChangeListener();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setKeyPadListener(null, true);

    }




    @Override
    protected void onFragmentResumed() {

        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).setTitle("Select Location");
        ((MainActivity)getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.TitleType);
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_WHITE_BACK);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationText("Back");
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);


        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setLayoutChangeListener();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setKeyPadListener(this, true);

    }

    private GoogleMap.OnCameraChangeListener mapPositionChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            LatLng centerLatLng = cameraPosition.target;
            getAddressForLocation(centerLatLng, false);
        }
    };

    public void getAddressForLocation(LatLng location, boolean pinOnMap) {
        try {

            if(getView() == null) return;

            double latitude = location.latitude;
            double longitude = location.longitude;
            int maxResults = 1;

            Geocoder gc = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

            if (addresses.size() > 0) {
                wSessionLocation = location;
                wSessionAddress = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality();
                //((TextView) getView().findViewById(R.id.street_name_txt)).setText("" + addresses.get(0).getAddressLine(0));
                //((TextView) getView().findViewById(R.id.city_name_txt)).setText("" + addresses.get(0).getLocality());
                ((EditText) getView().findViewById(R.id.workout_location_address_txt)).setKeyListener(null);
                ((EditText) getView().findViewById(R.id.workout_location_address_txt)).setText(wSessionAddress);

            }

            LatLng currentLocation = new LatLng(Double.valueOf(((MainActivity) getActivity() ).locationService.getCurrentLatitude()), Double.valueOf(((MainActivity) getActivity() ).locationService.getCurrentLongitude()));


            if(pinOnMap) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }




    public LatLng getLocationFromAddress(final String locationAddress, boolean animate) {

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String result = null;

        try {
            List addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLatitude()).append("\n");
                sb.append(address.getLongitude()).append("\n");

                result = sb.toString();

                if (result != null){
                    LatLng addressLocation = new LatLng(Double.valueOf(address.getLatitude()), Double.valueOf(address.getLongitude()));
                    if(animate) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addressLocation, 17));
                    }else{
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLocation, 17));
                    }

                    return addressLocation;
                }
            }
        } catch (IOException e) {
        }

        ((MainActivity) getActivity()).showAlertDialog("Error", "Entered Address is Not Valid", "Ok", null);
        return null;
    }


    private void verifyPromoCode(final String userPromoCode, final boolean fromConfirmBtn){
        if(getActivity() == null) return;
        ((MainActivity) getActivity()).showProgressDialog();

        /*if(WORKOUT_SESSION_TYPE == WORKOUT_SESSION_TRAIN_LATER || MainActivity.currentScheduleParseObj != null){
            enteredPromoCodeObj = null;
            requestWorkoutSession(true);
            return;

        }*/

        if(userPromoCode.length() == 0){
            enteredPromoCodeObj = null;
            requestWorkoutSession(true, fromConfirmBtn);

        }else{
            ParseQuery promoCodeQry = ParseQuery.getQuery("PromoCode");
            promoCodeQry.whereEqualTo("active", true);

            promoCodeQry.findInBackground(new FindCallback() {
                @Override
                public void done(List objects, ParseException e) {
                }

                @Override
                public void done(Object o, Throwable throwable) {
                    if(throwable == null){

                        for(final Object promoCode : ((List) o)){
                            if(promoCode instanceof ParseObject) {
                                if (((ParseObject) promoCode).getString("promoCode").equalsIgnoreCase(userPromoCode)) {
                                    Log.d("TEST", userPromoCode+" promo code="+((ParseObject) promoCode).getString("promoCode"));

                                    ParseRelation promoCodeClientRelation = ((ParseObject) promoCode).getRelation("usedClients");
                                    ParseQuery promoCodeClientQry = promoCodeClientRelation.getQuery();

                                    ParseObject clientObj = ParseUser.getCurrentUser().getParseObject("client");
                                    promoCodeClientQry.whereEqualTo("objectId", clientObj.getObjectId());

                                    promoCodeClientQry.countInBackground(new CountCallback() {
                                        @Override
                                        public void done(int count, ParseException e) {
                                            if (e == null) {
                                                if (count == 0) {
                                                    enteredPromoCodeObj = ((ParseObject) promoCode);
                                                    requestWorkoutSession(true, fromConfirmBtn);
                                                    return;

                                                } else {
                                                    enteredPromoCodeObj = null;
                                                    requestWorkoutSession(false, fromConfirmBtn);
                                                    return;

                                                }

                                            } else {
                                                enteredPromoCodeObj = null;
                                                requestWorkoutSession(false, fromConfirmBtn);
                                                return;

                                            }
                                        }
                                    });

                                    return;
                                }
                            }

                        }

                    }

                    requestWorkoutSession(false, fromConfirmBtn);

                }
            });

        }

    }

    /*
     * Add Workout session in device
     * Calendar
     */
    private long addScheduleInCalendar(){
        Cursor managedCursor = getActivity().getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), new String[]{ "_id", "calendar_displayName" }, "visible=1", null, null);


        if(managedCursor != null) {
            final int[] calIds = new int[managedCursor.getCount()];
            final String[] calNames = new String[managedCursor.getCount()];


            if (managedCursor.moveToFirst()) {
                for (int i = 0; i < calIds.length; i++) {
                    calIds[i] = managedCursor.getInt(0);
                    calNames[i] = managedCursor.getString(1);
                    managedCursor.moveToNext();

                }
            }
            managedCursor.close();


            /*int price = 17;
            if(WORKOUT_LENGTH == 60){
                price = 25;
            }*/

            double price =  getSessionPrice();

            double transPrice = 0.3 + (0.029 * price);
            if(price == 0){
                transPrice = 0;
            }

            price += transPrice;

            DecimalFormat precision = new DecimalFormat("0.00");
            precision.setRoundingMode(RoundingMode.CEILING);

            String desc = "Workout Type : "+Constants.WORKOUT_TITLES.get(WORKOUT_TYPE) +
                          "\nDuration : " +WORKOUT_LENGTH +" min"+
                          "\nPrice : " + "$" + precision.format(price);



            /*
             * Add WorkOut Event in Device Calendar
             */
            ContentValues cv = new ContentValues();
            cv.put("calendar_id", calIds[calIds[0]]);
            cv.put("title", "WeTrain Workout");
            cv.put("description", desc);
            cv.put("eventLocation", wSessionAddress);

            if(Integer.parseInt(Build.VERSION.SDK) >= 14 ){
                cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            }


            long startTime = WORKOUT_SCHEDULE_TIME.getTime().getTime();

            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(startTime);
            endTime.add(Calendar.MINUTE, WORKOUT_LENGTH);

            cv.put("dtstart", startTime);
            cv.put("dtend", endTime.getTime().getTime());

            Uri calendarUri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
                calendarUri = getActivity().getContentResolver().insert(Uri.parse("content://com.android.calendar/events"), cv);
            else
                calendarUri = getActivity().getContentResolver().insert(Uri.parse("content://calendar/events"), cv);


            long calendarId = 0;
            if(calendarUri != null){
                calendarId = ContentUris.parseId(calendarUri);
            }
            Log.d("TEST", "Calendar URI="+calendarUri.toString()+"\nCalendar ID="+calendarId);

            return calendarId;

        }

        return 0;

    }



    @Override
    public void softKeyPadShown() {
        if(scheduleConfirmAlert != null){
            if(scheduleConfirmAlert.isShowing()) {
                ((EditText) scheduleConfirmAlert.findViewById(R.id.promo_edit_txt)).setHint("Have a promo code?");
                ((EditText) scheduleConfirmAlert.findViewById(R.id.promo_edit_txt)).setHintTextColor(getResources().getColor(R.color.edittext_hint_clr));
                ((EditText) scheduleConfirmAlert.findViewById(R.id.promo_edit_txt)).setSelected(false);
            }

        }
    }

    @Override
    public void softKeyPadHidden() {
        if(getView() == null) return;

        ((EditText) getView().findViewById(R.id.workout_location_address_txt)).setKeyListener(null);
        ((EditText) getView().findViewById(R.id.workout_location_address_txt)).setText(((EditText) getView().findViewById(R.id.workout_location_address_txt)).getText());

    }

    @Override
    public void clickedOnMaskLayout() {

    }



}
