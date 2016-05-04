package com.wetrain.client.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wetrain.client.Constants;
import com.wetrain.client.Utills;
import com.wetrain.client.customviews.CircularProgressDrawable;
import com.wetrain.client.customviews.CustomActionBar;
import com.wetrain.client.customviews.DetectSoftKeyPadLayout;
import com.wetrain.client.customviews.SystemBarTintManager;
import com.wetrain.client.fragment.FragmentHolder;
import com.wetrain.client.fragment.SearchTrainerFragment;
import com.wetrain.client.fragment.TrainerScreenFragment;
import com.wetrain.client.fragment.WorkoutLocationFragment;
import com.wetrain.client.locationservice.LocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.wetrain.client.R;


public class MainActivity extends AppCompatActivity implements  CustomActionBar.ActionBarNavigationButtonClickListener, DetectSoftKeyPadLayout.DetectSoftKeyPadListener {

    public interface FacebookLoginCompleteListener{
        void onFacebookLoginCompleted(String exception, boolean isNewUser);

    }

    private static final int[] BOTTOM_TAB_BTN_IDS = new int[]{R.id.train_button, R.id.account_button};
    private ProgressDialog progressDialog;
    public static ParseObject currentScheduleParseObj;
    public static ParseObject currentPromoCodeParseObj;

    public LocationService locationService;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Log.d("MainActivity", getClass().getCanonicalName() + " onNewIntent");
        //checkAppFromNotification(true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        locationService = new LocationService(this);


        if(Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        currentScheduleParseObj = null;
        currentPromoCodeParseObj = null;

        ((DetectSoftKeyPadLayout) findViewById(R.id.keypad_detector_lyt)).initSoftKeyPadElements(this, findViewById(R.id.keypad_mask_layout));


        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        CustomActionBar actionBarView = (CustomActionBar) getLayoutInflater().inflate(R.layout.action_bar_view, null);
        actionBarView.setNavigationClickListener(this);
        actionBar.setCustomView(actionBarView);
        Toolbar parent =(Toolbar) actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        selectBottomTab(0);


        findViewById(R.id.train_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentScheduleParseObj = null;
                currentPromoCodeParseObj = null;
                selectBottomTab(0);

                FragmentHolder.FragmentTags lastFragmentTag = FragmentHolder.getLastFragmentTag();

                //
                if (lastFragmentTag.equals(FragmentHolder.FragmentTags.SearchTrainerFragmentTag) || lastFragmentTag.equals(FragmentHolder.FragmentTags.TrainerScreenFragmentTag)) {
                    return;
                }
                if (lastFragmentTag != null) {
                    if (FragmentHolder.isAccountTabFragment(lastFragmentTag.name())) {
                        if (FragmentHolder.hideAccountTabFragments(getSupportFragmentManager())) {
                            return;
                        }
                    }
                }

                FragmentHolder.removeTrainTabFragmants(getSupportFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, true);
                FragmentHolder.setFragment(MainActivity.this, null, FragmentHolder.FragmentTags.HomeFragmenttag, false, false);

            }
        });

        findViewById(R.id.account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentScheduleParseObj = null;
                currentPromoCodeParseObj = null;
                selectBottomTab(1);

                /*FragmentHolder.FragmentTags lastFragmentTag = FragmentHolder.getLastFragmentTag();
                if(lastFragmentTag != null){
                    if(FragmentHolder.isTrainTabFragment(lastFragmentTag.name())){
                        if(FragmentHolder.hideTrainTabFragments(getSupportFragmentManager())) {
                            return;
                        }
                    }
                }*/

                FragmentHolder.removeAccountTabFragmants(getSupportFragmentManager(), FragmentHolder.FragmentTags.AccountFragmentTag, false);
                FragmentHolder.setFragment(MainActivity.this, null, FragmentHolder.FragmentTags.AccountFragmentTag, false, false);
            }
        });

        FragmentHolder.setFragment(MainActivity.this, null, FragmentHolder.FragmentTags.SplashScreenFragmentTag, false, false);

        showNotificationView(View.VISIBLE);


        /*
         * update available trainer count
         */
        //setNotificationTitleTxt(new Random().nextInt(30) + " Trainers Waiting For You");


    }

    @Override
    protected void onResume() {
        super.onResume();

        checkAppFromNotification();



        if(locationService != null) {
            locationService.startLocationUpdate();
        }

        /*
         * update available trainer count
         */
        setAvailableTrainerCount();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationService != null) {
            locationService.pauseLocationUpdates();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentScheduleParseObj = null;
        currentPromoCodeParseObj = null;
        if(locationService != null) {
            locationService.stopLocationUpdate();
        }

    }

    public boolean checkLocationServiceEnabled(){
        boolean locServiceEnabled = false;
        if(locationService != null){
            locServiceEnabled = locationService.checkLocationServiceEnabled();
        }
        if(!locServiceEnabled){
            showAlertDialog("Location", "Location service is disabled. Please enable Location service.", "Settings", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }
            }, "Cancel", null, false);
        }
        return false;
    }

    public void checkAppFromNotification(){

        if (FragmentHolder.getLastFragmentTag().equals(FragmentHolder.FragmentTags.SplashScreenFragmentTag)) {
            return;
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            //check is app from trainer confirmed push notification or schedule notification

            String channelId = bundle.getString("com.parse.Channel");
            if(channelId != null){ // from trainer accept push notification
                if(!Utills.checkScheduleIsAlreadyNotified(this, channelId)){
                    Utills.removeScheduleDataInPref(this, channelId);

                    try {
                        JSONObject pushData = new JSONObject(bundle.getString("com.parse.Data"));
                        String trainerObjectId = pushData.getString("trainer");
                        String workoutObjId = pushData.getString("request");
                        getMatchedTrainerInfo(workoutObjId, trainerObjectId);
                        return;
                    }catch (Exception e){

                    }
                }

            }else { // from schedule alarm notification
                boolean appFromNotification = bundle.getBoolean(AlarmReceiver.INTENT_FROM_NOTIFICATION, false);
                final int notificationCode = bundle.getInt(AlarmReceiver.SCHEDULE_ALARM_REQUEST_CODE, 0);

                int flags = getIntent().getFlags();
                if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0 && appFromNotification) {
                    getIntent().removeExtra(AlarmReceiver.INTENT_SCHEDULE_ALERT);
                    appFromNotification = false;
                    bundle.clear();
                }

                final String scheduleObjId = bundle.getString(AlarmReceiver.SCHEDULE_PARSE_OBJCT_ID);

                /*
                 * Show schedule remainder alert dialog when app in foreground
                 */
                boolean canShowScheduleAlert = getIntent().getBooleanExtra(AlarmReceiver.INTENT_SCHEDULE_ALERT, false);
                if (canShowScheduleAlert && !Utills.checkScheduleIsAlreadyNotified(this, scheduleObjId)) {
                    showAlertDialog("Message", "Hey! It's almost time to workout", "Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkWorkoutSchedules(scheduleObjId, notificationCode);
                        }
                    }, null, null, false);

                    return;
                }

                if(!Utills.checkScheduleIsAlreadyNotified(this, scheduleObjId) ) {
                    checkWorkoutSchedules(scheduleObjId, notificationCode);
                    return;
                }
            }
        }


         /*
         * Check Exist workout session Request
         */
        checkExistingRequest();

    }



    private void getMatchedTrainerInfo(String workoutRequest, String trainer){
        if(ParseUser.getCurrentUser() == null) return;

        showProgressDialog();
        ParseQuery parseQuery = ParseQuery.getQuery("Workout");
        parseQuery.whereEqualTo("objectId", workoutRequest);
        parseQuery.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {

            }

            @Override
            public void done(Object o, Throwable throwable) {
                closeProgerssDialog();
                if (throwable == null) {
                    if (o instanceof List) {
                        Object workoutObj = ((List) o).get(0);
                        if (workoutObj instanceof ParseObject) {
                            moveToTrainerScreen((ParseObject) workoutObj);
                        }
                    }
                }
            }
        });

    }

    public void setActionBarVisibility(int visibility){
        if(visibility == View.VISIBLE){
            getSupportActionBar().show();

        }else{
            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void setTitle(CharSequence title) {
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setTitle(title);
    }


    public void setActionBarBgColor(int res){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setActionBarBgColor(res);
    }

    public void setTextColor(int res, String type){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setTextColor(res, type);
    }

    public void setNavigationText(CharSequence txt){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setNavigationText(txt);
    }

    public void setNavigationTextVisibility(int visible){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setNavigationTextVisibility(visible);
    }

    public void setOptionButtonVisibility(int visibility){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setOptionButtonVisibility(visibility);
    }

    public void setActionbarButtonClickListener(CustomActionBar.ActionBarOptionsButtonClickListener clickListener){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setOptionsButtonClickListener(clickListener);
    }

    public void setOptionsButtonLabel(String label){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setOptionButtonLabel(label);
    }
    public void setNavigationType(int navigationType){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setNavigationType(navigationType);
    }


    public void showNotificationView(int visibility){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.showNotificationView(visibility);
    }

    public void setNotificationTitleTxt(String notificationTxt){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        if(actionBarView != null) {
            actionBarView.setNotificationTitleTxt(notificationTxt);
        }
    }

    public void setNotificationTitleVisibility(int visibility){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setNotificationTitleVisibility(visibility);
    }

    public void setOptionButtonEnable(boolean enable){
        CustomActionBar actionBarView = (CustomActionBar) getSupportActionBar().getCustomView();
        actionBarView.setOptionButtonEnable(enable);
    }



    public void selectBottomTab(int pos){
        if(pos < BOTTOM_TAB_BTN_IDS.length){
            for(int i = 0 ; i < BOTTOM_TAB_BTN_IDS.length ; i++){
                findViewById(BOTTOM_TAB_BTN_IDS[i]).setSelected(false);
            }
            findViewById(BOTTOM_TAB_BTN_IDS[pos]).setSelected(true);

        }else{
            Toast.makeText(this, "Invalid Tab Position", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        FragmentHolder.onBackPressed(this);

    }

    @Override
    public void onNavigationBackButtonClicked() {
        FragmentHolder.onBackPressed(this);
    }

    public void showAlertDialog(String title, String message, String positiveBtnTxt, String negativeBtnTxt){
        showAlertDialog(title, message, positiveBtnTxt, null, negativeBtnTxt, null, false);
    }

    public void showAlertDialog(String title, String message, String positiveBtnTxt, final View.OnClickListener positiveBtnClickListener, String negativeBtnTxt, final View.OnClickListener negativeBtnClickListener , boolean isVerticalBtn){
        showAlertDialog(title, message, positiveBtnTxt, positiveBtnClickListener, negativeBtnTxt, negativeBtnClickListener ,null, null, isVerticalBtn);

    }

    public void showAlertDialog(String title, String message, String positiveBtnTxt, final View.OnClickListener positiveBtnClickListener, String negativeBtnTxt, final View.OnClickListener negativeBtnClickListener , String neutralBtnText, final View.OnClickListener neutralBtnClickListener, boolean isVerticalBtn){
        View alertView = getLayoutInflater().inflate(R.layout.alert_custom_view, null);

        final Dialog alert = new Dialog(this, R.style.default_dialog_theme);
        alert.getWindow().setContentView(alertView);

        ((TextView) alertView.findViewById(R.id.alert_title_txt)).setText(title);
        ((TextView) alertView.findViewById(R.id.alert_message_txt)).setVisibility(View.GONE);
        if(message != null) {
            ((TextView) alertView.findViewById(R.id.alert_message_txt)).setVisibility(View.VISIBLE);
            ((TextView) alertView.findViewById(R.id.alert_message_txt)).setText(message);
        }

        if(isVerticalBtn){
            ((LinearLayout) alertView.findViewById(R.id.alert_btn_lyt)).setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((Button) alertView.findViewById(R.id.alert__btn_1)).setLayoutParams(lp);
            ((Button) alertView.findViewById(R.id.alert__btn_2)).setLayoutParams(lp);
            ((Button) alertView.findViewById(R.id.alert__btn_3)).setLayoutParams(lp);

            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
            alertView.findViewById(R.id.alert_btn_seperator_view_1).setLayoutParams(lp);
            alertView.findViewById(R.id.alert_btn_seperator_view_2).setLayoutParams(lp);

            if(negativeBtnTxt != null){
                ((Button) alertView.findViewById(R.id.alert__btn_1)).setBackgroundResource(R.drawable.alert_button_clr_bg);
            }

            if(neutralBtnText != null){
                ((Button) alertView.findViewById(R.id.alert__btn_2)).setBackgroundResource(R.drawable.alert_button_clr_bg);
            }

        }else{
            if(negativeBtnTxt != null){
                ((Button) alertView.findViewById(R.id.alert__btn_1)).setBackgroundResource(R.drawable.alert_button_left_rect_bg);
                ((Button) alertView.findViewById(R.id.alert__btn_2)).setBackgroundResource(R.drawable.alert_button_right_rect_bg);
            }

            if(neutralBtnText != null){
                ((Button) alertView.findViewById(R.id.alert__btn_2)).setBackgroundResource(R.drawable.alert_button_clr_bg);
                ((Button) alertView.findViewById(R.id.alert__btn_3)).setBackgroundResource(R.drawable.alert_button_right_rect_bg);

            }
        }

        ((Button) alertView.findViewById(R.id.alert__btn_1)).setVisibility(View.GONE);
        if(positiveBtnTxt != null){
            ((Button) alertView.findViewById(R.id.alert__btn_1)).setVisibility(View.VISIBLE);
            ((Button) alertView.findViewById(R.id.alert__btn_1)).setText(positiveBtnTxt);

            ((Button) alertView.findViewById(R.id.alert__btn_1)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();

                    if (positiveBtnClickListener != null) {
                        positiveBtnClickListener.onClick(v);
                    }

                }
            });
        }

        ((Button) alertView.findViewById(R.id.alert__btn_2)).setVisibility(View.GONE);
        if(negativeBtnTxt != null){
            alertView.findViewById(R.id.alert_btn_seperator_view_1).setVisibility(View.VISIBLE);
            ((Button) alertView.findViewById(R.id.alert__btn_2)).setVisibility(View.VISIBLE);
            ((Button) alertView.findViewById(R.id.alert__btn_2)).setText(negativeBtnTxt);
            ((Button) alertView.findViewById(R.id.alert__btn_2)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    if (negativeBtnClickListener != null) {
                        negativeBtnClickListener.onClick(v);
                    }


                }
            });
        }

        ((Button) alertView.findViewById(R.id.alert__btn_3)).setVisibility(View.GONE);
        if(neutralBtnText != null){
            alertView.findViewById(R.id.alert_btn_seperator_view_2).setVisibility(View.VISIBLE);
            ((Button) alertView.findViewById(R.id.alert__btn_3)).setVisibility(View.VISIBLE);
            ((Button) alertView.findViewById(R.id.alert__btn_3)).setText(neutralBtnText);
            ((Button) alertView.findViewById(R.id.alert__btn_3)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    if (neutralBtnClickListener != null) {
                        neutralBtnClickListener.onClick(v);
                    }

                }
            });
        }


        alert.show();
    }



    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setDimAmount(0.3f);
        progressDialog.show();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View progressView = getLayoutInflater().inflate(R.layout.progress_dialog_view, null);
        //set custom progress drawable
        ProgressBar v = (ProgressBar) progressView.findViewById(R.id.progress_bar);
        v.setIndeterminateDrawable(new CircularProgressDrawable(getResources().getColor(R.color.loginbtn_clr), getResources().getDimension(R.dimen.circular_progress_bar_size)));

        progressDialog.setContentView(progressView);



    }

    public void closeProgerssDialog(){
        if(progressDialog != null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }


    public void setStatusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(color));

        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(color);
        }
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }

        win.setAttributes(winParams);
    }

    public void setWindowFullScreen(boolean on){
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void moveToTrainerScreen(ParseObject workoutParseObj ){
        FragmentHolder.FragmentTags lastFragment = FragmentHolder.getLastFragmentTag();
        if(!lastFragment.equals(FragmentHolder.FragmentTags.TrainerScreenFragmentTag)) {
            selectBottomTab(0);
            if(FragmentHolder.isAccountTabFragment(lastFragment.name())) {
                FragmentHolder.hideAccountTabFragments(getSupportFragmentManager());
            }
            FragmentHolder.removeTrainTabFragmants(getSupportFragmentManager(), FragmentHolder.FragmentTags.TrainerScreenFragmentTag, false);
            FragmentHolder.setFragment(MainActivity.this, null, FragmentHolder.FragmentTags.TrainerScreenFragmentTag, false, false);
            TrainerScreenFragment.currentWorkoutRequest = workoutParseObj;
        }


    }

    private void moveToSearchingTrainerScreen(ParseObject workoutParseObj){
        /*
         * Move to Search Trainer screen
         */
        FragmentHolder.FragmentTags lastFragment = FragmentHolder.getLastFragmentTag();
        Log.d("TEST", "moveToSearchingTrainerScreen>>>>>"+lastFragment);

        if(!lastFragment.equals(FragmentHolder.FragmentTags.SearchTrainerFragmentTag)) {
            selectBottomTab(0);
            if(FragmentHolder.isAccountTabFragment(lastFragment.name())) {
                FragmentHolder.hideAccountTabFragments(getSupportFragmentManager());
            }
            FragmentHolder.removeTrainTabFragmants(getSupportFragmentManager(), FragmentHolder.FragmentTags.SearchTrainerFragmentTag, false);
            FragmentHolder.setFragment(MainActivity.this, null, FragmentHolder.FragmentTags.SearchTrainerFragmentTag, false, false);
            SearchTrainerFragment.currentWorkoutRequestObj = workoutParseObj;
        }
    }


    /*
     * Check Exist Workout Request
     */
    public void checkExistingRequest(){

        if(ParseUser.getCurrentUser() == null) return;
        if(ParseUser.getCurrentUser().getParseObject("client") == null) return;
        //if(!FragmentHolder.getLastFragmentTag().equals(FragmentHolder.FragmentTags.HomeFragmenttag)) return;

        final ParseObject clientParseObj = ParseUser.getCurrentUser().getParseObject("client");
        clientParseObj.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    final ParseObject workoutParseObj = clientParseObj.getParseObject("workout");
                     if(workoutParseObj != null){
                        workoutParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    if (workoutParseObj.get("status") instanceof String) {
                                        String status = workoutParseObj.getString("status");

                                        //check for Rating
                                        if (status.equals(Constants.WORKOUT_REQUEST_STATE.Complete.toString())) {
                                            if (workoutParseObj.getObjectId() != null) {
                                                ///rating view not shown for client yet
                                                if(workoutParseObj.get("clientRating") == null) {
                                                    //show Rating view
                                                    showTrainerRatingAlert(workoutParseObj);
                                                }
                                            }
                                        }

                                        if (status.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString())) {
                                            // Show Trainer Matched screen
                                            moveToTrainerScreen(workoutParseObj);

                                        } else if (status.equals(Constants.WORKOUT_REQUEST_STATE.Searching.toString())) {
                                            if (workoutParseObj.getObjectId() != null) {
                                                if (workoutParseObj.get("time") instanceof Date) {
                                                    Date sessionDate = workoutParseObj.getDate("time");
                                                    long minElapsed = Constants.getMinutesBetweenTwoDates(sessionDate);


                                                    if (minElapsed > 60) { // cancel after 60 minutes of searching
                                                        // cancel exist request
                                                        workoutParseObj.put("status", Constants.WORKOUT_REQUEST_STATE.Cancelled.toString());
                                                        workoutParseObj.saveInBackground();

                                                    } else {
                                                       /*
                                                         * Move to Search Trainer screen
                                                         */
                                                        moveToSearchingTrainerScreen(workoutParseObj);


                                                    }

                                                }
                                            }

                                        } else if (status.equals(Constants.WORKOUT_REQUEST_STATE.Training.toString())) {
                                            if (workoutParseObj.getObjectId() != null) {
                                                if (workoutParseObj.get("start") instanceof Date) {
                                                    Date startDate = workoutParseObj.getDate("start");
                                                    long minElapsed = Constants.getMinutesBetweenTwoDates(startDate);
                                                    int sessionLength = workoutParseObj.getInt("length");

                                                    if (minElapsed > sessionLength * 2) { // cancel after 2x the workout time
                                                        //complete the exist request
                                                        workoutParseObj.put("status", Constants.WORKOUT_REQUEST_STATE.Complete.toString());
                                                        workoutParseObj.saveInBackground();

                                                    } else {
                                                        /*
                                                         * Move to Search Trainer screen
                                                         */
                                                        moveToTrainerScreen(workoutParseObj);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }




    /*
     * Check Workout Schedule time
     */
    public void checkWorkoutSchedules(final String schObjKey, final int notificationId){
        if(ParseUser.getCurrentUser() == null) return;

        /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> prefKeys =  pref.getAll();

        for(Map.Entry<String, ?> entry : prefKeys.entrySet()){
            boolean canContinue = true;
            if(schObjKey != null){
                if(!schObjKey.equals(entry.getKey())){
                    canContinue = false;
                }
            }


            if(canContinue) {
             if (entry.getValue() instanceof Long) {*/

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);


        long schTime = (Long) pref.getLong(schObjKey, 0);
        Calendar schCalendar = Calendar.getInstance();
        schCalendar.setTimeInMillis(schTime);
        schCalendar.add(Calendar.MINUTE, -Constants.SCHEDULE_NOTIFICATION_INTERVAL);

        if (System.currentTimeMillis() >= schCalendar.getTime().getTime()) {

            showProgressDialog();
            ParseQuery parseQuery = ParseQuery.getQuery("ScheduleInfo");
            parseQuery.whereEqualTo("objectId", schObjKey);
            parseQuery.findInBackground(new FindCallback() {
                @Override
                public void done(List objects, ParseException e) {
                    closeProgerssDialog();
                }

                @Override
                public void done(Object o, Throwable throwable) {
                    closeProgerssDialog();
                    if (throwable == null) {
                        if (o instanceof List) {
                            if (((List) o).size() > 0) {
                                if (((List) o).get(0) instanceof ParseObject) {
                                    final ParseObject schData = (ParseObject) ((List) o).get(0);
                                    String status = schData.getString("status");
                                    Log.d("SCHEDULE", "schedule status=" + status);
                                    if (!status.equals(Constants.SCHEDULE_STATE.SelfConfirmed.toString())) {
                                        /*
                                         * Move to Workout Location fragment
                                         */
                                        new Handler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Utills.removeScheduleDataInPref(MainActivity.this, schObjKey);
                                                final Bundle notificationBundle = new Bundle();
                                                notificationBundle.putBoolean(AlarmReceiver.INTENT_FROM_NOTIFICATION, true);

                                                //ParseProxyObject parseObject = new ParseProxyObject(schData);
                                                //notificationBundle.putSerializable(WorkoutLocationFragment.BUNDLE_SCHECULE_PARSE_OBJ_KEY, parseObject);
                                                currentScheduleParseObj = schData;

                                                final ParseObject promoCodeObj = MainActivity.currentScheduleParseObj.getParseObject("promoCode");
                                                if (promoCodeObj != null) {
                                                    promoCodeObj.fetchInBackground(new GetCallback<ParseObject>() {
                                                        @Override
                                                        public void done(ParseObject object, ParseException e) {
                                                            if (e == null) {
                                                                currentPromoCodeParseObj = promoCodeObj;
                                                            }

                                                            moveToSessionConfirmScreen(notificationBundle, notificationId);
                                                        }
                                                    });
                                                } else {
                                                    moveToSessionConfirmScreen(notificationBundle, notificationId);
                                                }

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            });

            //break;

        }
                /*}
            }
        }*/

    }

    private void moveToSessionConfirmScreen(Bundle notificationBundle, int notificationID){
        WorkoutLocationFragment workoutLocationFragment = (WorkoutLocationFragment) getSupportFragmentManager().findFragmentByTag(FragmentHolder.FragmentTags.WorkoutLocationFragmentTag.name());
        FragmentHolder.setFragment(MainActivity.this, notificationBundle, FragmentHolder.FragmentTags.WorkoutLocationFragmentTag, false, false);

        if (workoutLocationFragment != null) {
            if (workoutLocationFragment.getArguments() != null) {
                workoutLocationFragment.getArguments().putAll(notificationBundle);
            }
            workoutLocationFragment.onScheduleNotificationReceived();
        }

        //remove schedule notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);

    }

    private void setAvailableTrainerCount(){

        Log.d("TEST", ">>>>"+(findViewById(R.id.avail_trainer_count_lyt)).getVisibility());


                final int randomBetween30 = new Random().nextInt(30);
                int availTrainer = 20 + randomBetween30;
                setNotificationTitleTxt(availTrainer + " Trainers Waiting For You");



        /*ParseQuery trainerQuery = ParseQuery.getQuery("Trainer");
        trainerQuery.whereEqualTo("status", "available");

        trainerQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                Log.d("TEST", "Available trainer count="+count+" random="+randomBetween30);

                int availTrainer = count + 20 + randomBetween30;

                setNotificationTitleTxt(availTrainer + " Trainers Waiting For You");



            }
        });*/

    }


    @Override
    public void setLayoutChangeListener() {
        DetectSoftKeyPadLayout detectSoftKeyPadLayout = (DetectSoftKeyPadLayout) (findViewById(R.id.keypad_detector_lyt));
        if(detectSoftKeyPadLayout != null){
            detectSoftKeyPadLayout.setLayoutChangeListener();
        }
    }

    @Override
    public void removeLayoutChangeListener() {
        DetectSoftKeyPadLayout detectSoftKeyPadLayout = (DetectSoftKeyPadLayout) (findViewById(R.id.keypad_detector_lyt));
        if(detectSoftKeyPadLayout != null){
            detectSoftKeyPadLayout.removeLayoutChangeListener();
        }
    }

    @Override
    public void setKeyPadListener(DetectSoftKeyPadLayout.KeyPadListener listener, boolean hideKeypadMaskAlways) {
        DetectSoftKeyPadLayout detectSoftKeyPadLayout = (DetectSoftKeyPadLayout) (findViewById(R.id.keypad_detector_lyt));
        if(detectSoftKeyPadLayout != null){
            detectSoftKeyPadLayout.setKeyPadListener(listener, hideKeypadMaskAlways);
        }
    }



    public void loginWithFacebook(final FacebookLoginCompleteListener fbLoginCompleteListener){

        //List<String> permissions = Arrays.asList("id, name,first_name,last_name, email, age_range, birthday, picture.type(large), gender");
        List<String> permissions = Arrays.asList("public_profile", "email", "user_birthday", "user_friends");//Arrays.asList("public_profile", "user_about_me", "user_relationships", "user_birthday", "user_location", "email");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(final ParseUser user, final ParseException err) {
                Log.d("TEST", user+"|"+err);
                if (err == null) {
                    if (user != null) {
                        if(user.isNew()) {
                            //create a new client for faceboook user
                            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(final JSONObject object, GraphResponse response) {

                                    Log.d("TEST", "Facebook JSON obj="+object+"\nGraphResponse="+response);

                                    if(response != null){
                                        if(response.getError() == null){
                                            try {

                                                final ParseObject clientObj = ParseObject.create("Client");

                                                int age = 0;
                                                try{
                                                    // calculate User age from Facebook DOB
                                                    if(object.getString("birthday") != null){
                                                        Calendar currentCalendar = Calendar.getInstance();
                                                        Log.d("TEST", "DOB>>>>>"+object.getString("birthday"));
                                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                                        Date userDob = sdf.parse(object.getString("birthday"));
                                                        Calendar ageCalendar = Calendar.getInstance();
                                                        ageCalendar.setTime(userDob);
                                                        age = currentCalendar.get(Calendar.YEAR) - ageCalendar.get(Calendar.YEAR);


                                                        clientObj.put("age", ""+age);


                                                        if(age > 18 ){
                                                            clientObj.put("checkedAgeAbove18",  true);
                                                        }
                                                    }

                                                }catch (Exception e){
                                                    Log.d("TEST", "Error="+e.getMessage());
                                                    e.printStackTrace();
                                                }

                                                try{
                                                    // set user First name from Facebook
                                                    if(object.getString("first_name") != null){
                                                        clientObj.put("firstName", object.getString("first_name"));
                                                    }
                                                } catch (JSONException e){
                                                    Log.d("TEST", "Error="+e.getMessage());
                                                }


                                                try{
                                                    // set user Last name from Facebook
                                                    if(object.getString("last_name") != null){
                                                        clientObj.put("lastName", object.getString("last_name"));
                                                    }
                                                } catch (JSONException e){
                                                    Log.d("TEST", "Error="+e.getMessage());
                                                }

                                                try{
                                                    // set user Gender from Facebook
                                                    if(object.getString("gender") != null){
                                                        clientObj.put("gender", object.getString("gender"));
                                                    }
                                                } catch (JSONException e){
                                                    Log.d("TEST", "Error="+e.getMessage());
                                                }

                                                try {
                                                    // set user Email from Facebook
                                                    if (object.getString("email") != null) {
                                                        if (object.getString("email").length() > 0) {
                                                            if (ParseUser.getCurrentUser() != null) {
                                                                ParseUser.getCurrentUser().put("email", object.getString("email"));
                                                            }
                                                        }
                                                    }
                                                } catch (JSONException e){
                                                    Log.d("TEST", "Error="+e.getMessage());
                                                }

                                                final String picUrl = object.getString("picture");
                                                clientObj.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if(e == null){
                                                            ParseUser.getCurrentUser().put("client", clientObj);
                                                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    try {
                                                                        downloadFbProfileImg(object.getJSONObject("picture"));
                                                                    }catch (JSONException ex){
                                                                        Log.d("TEST", "Error="+e.getMessage());
                                                                    }

                                                                    fbLoginCompleteListener.onFacebookLoginCompleted(null, true);
                                                                }
                                                            });

                                                        }else{
                                                            fbLoginCompleteListener.onFacebookLoginCompleted(e.getMessage(), true);

                                                        }
                                                    }
                                                });


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            fbLoginCompleteListener.onFacebookLoginCompleted(response.getError().getErrorMessage(), true);

                                        }

                                    }else{
                                        fbLoginCompleteListener.onFacebookLoginCompleted(getString(R.string.fb_error), true);

                                    }

                                }
                            });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id, name,first_name,last_name, email, age_range, birthday, picture.type(large), gender");
                            request.setParameters(parameters);
                            request.executeAsync();

                        } else {
                            //Login with exist user
                            fbLoginCompleteListener.onFacebookLoginCompleted(null, false);

                        }

                    }else{
                        fbLoginCompleteListener.onFacebookLoginCompleted(getString(R.string.fb_error), false);

                    }

                } else {
                    fbLoginCompleteListener.onFacebookLoginCompleted(err.getMessage(), false);

                }
            }

        });
    }

    private void downloadFbProfileImg(JSONObject pictureData){
        try {
            if (pictureData != null) {
                String url = pictureData.getJSONObject("data").getString("url");

                Log.d("TEST", "img url="+url);

                if(url != null){
                    URL imgUrl = new URL(url);
                    Bitmap bmp = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);


                    ParseObject client = ParseUser.getCurrentUser().getParseObject("client");
                    if(client != null){
                        ParseFile imgFile = new ParseFile("profile.jpg", stream.toByteArray());
                        client.put("photo", imgFile);
                        client.saveInBackground();
                    }

                }
            }
        }catch(Exception e){
            Log.d("TEST", "Img error=" + e.getMessage());
            e.printStackTrace();
        }
    }


    public void showTrainerRatingAlert(final ParseObject workoutParseObj){

        final View alertView = getLayoutInflater().inflate(R.layout.rate_trainer_screen, null);
        final Dialog alert = new Dialog(this, R.style.contact_trainer_dialog_theme);
        alert.getWindow().setContentView(alertView);

        DrawableCompat.setTint(((RatingBar) alertView.findViewById(R.id.trainer_rating_bar)).getProgressDrawable(), Color.parseColor("#51e09d"));

        ((RatingBar) alertView.findViewById(R.id.trainer_rating_bar)).setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ((TextView) alertView.findViewById(R.id.trainer_rating_rate_txt)).setText(rating + " " + (rating > 1 ? "Stars" : "Star") + "!!");
            }
        });


        alertView.findViewById(R.id.trainer_rating_submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float rating = ((RatingBar) alertView.findViewById(R.id.trainer_rating_bar)).getRating();


                if (rating == 0) {
                    showAlertDialog("Message", "Please enter a rating", "Close", null);

                } else {
                    alert.dismiss();
                    //submit trainer rating
                    final ParseObject trainerParseObj = workoutParseObj.getParseObject("trainer");
                    if (trainerParseObj != null) {
                        trainerParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null && ParseUser.getCurrentUser() != null) {
                                    final ParseObject userParseObj = trainerParseObj.getParseObject("user");
                                    userParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null) {
                                                final ParseObject ratingParseObj = ParseObject.create("Ratings");
                                                ratingParseObj.put("rating", rating);
                                                ratingParseObj.put("comments", ((EditText) alertView.findViewById(R.id.trainer_rating_comments_txt)).getText().toString());
                                                ratingParseObj.put("status", Constants.RATING_STATE.Rated.toString());

                                                ratingParseObj.put("ratingForUserRole", "trainer");
                                                ratingParseObj.put("workout", workoutParseObj);
                                                ratingParseObj.put("ratedUser", ParseUser.getCurrentUser());

                                                ratingParseObj.put("ratingFor", userParseObj);

                                                if (Constants.TESTING) {
                                                    ratingParseObj.put("testing", true);
                                                }

                                                ratingParseObj.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            workoutParseObj.put("clientRating", ratingParseObj);
                                                            workoutParseObj.saveInBackground();

                                                        } else {
                                                            showAlertDialog("Could not save workout", "There was an issue on saving. Please try again.", "Ok", null);
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                    });
                                }


                            }
                        });
                    }
                }

            }
        });

        alert.show();
    }


    public interface AlertListItemSelectListener{
        void onItemSelected(int pos);
        void onNextBtnClicked();
    }
    public void showBottomListAlert(String[] listContent, int defaultSelection, final AlertListItemSelectListener listItemSelected){
        final View alertView = getLayoutInflater().inflate(R.layout.list_alert_view, null);
        final Dialog alert = new Dialog(this, R.style.contact_trainer_dialog_theme);

        ListView listView = (ListView) alertView.findViewById(R.id.alert_list_content);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, listContent);

        listView.setAdapter(adapter);

        listView.setItemChecked(defaultSelection, true);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alert.dismiss();
                if (listItemSelected != null) {
                    listItemSelected.onItemSelected(position);
                }
            }
        });


        alertView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        /*alertView.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                if (listItemSelected != null) {
                    listItemSelected.onNextBtnClicked();
                }
            }
        });*/

        alert.setCancelable(true);

        alert.getWindow().setContentView(alertView);

        alert.show();


    }


    public void showScheduleCreatedNotification(){
        View alertView = getLayoutInflater().inflate(R.layout.schedule_create_notification_alert_view, null);
        final Dialog alert = new Dialog(this, R.style.schedule_create_dialog_theme);
        alert.getWindow().setContentView(alertView);
        alert.setCancelable(true);
        alert.setCanceledOnTouchOutside(true);
        alert.show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.dismiss();
            }
        }, 1500);


    }

/*
    @Override
    public void onScheduleAlarmReceived() {
        Toast.makeText(MainActivity.this, "Schedule notification alaram received", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage("Hey! It's almost time to workout");
        builder.setPositiveButton("Ok", null);
        builder.show();
    }*/


}

