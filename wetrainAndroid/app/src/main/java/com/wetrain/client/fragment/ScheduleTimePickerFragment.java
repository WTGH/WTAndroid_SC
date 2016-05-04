package com.wetrain.client.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;
import com.wetrain.client.customviews.DateTimePickerWheel.AbstractWheelTextAdapter;
import com.wetrain.client.customviews.DateTimePickerWheel.OnWheelChangedListener;
import com.wetrain.client.customviews.DateTimePickerWheel.OnWheelClickedListener;
import com.wetrain.client.customviews.DateTimePickerWheel.WheelView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by test on 1/29/16.
 */
public class ScheduleTimePickerFragment extends WeTrainBaseFragment{

    private static final int WHEEL_ADAPTER_DATE = 1;
    private static final int WHEEL_ADAPTER_HOURS = 2;
    private static final int WHEEL_ADAPTER_MINUTES = 3;
    private static final int WHEEL_ADAPTER_AMPM = 4;

    private static final List<Integer> MINS_ARRAY = Arrays.asList(new Integer[] {0, 15, 30, 45});
    private static final List<String> AM_PM_ARRAY = Arrays.asList(new String[] {"AM", "PM"});


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_date_time_picker_view, container, false);



        WheelView day = (WheelView) view.findViewById(R.id.day);
        AbstractWheelTextAdapter dayAdapter = new WheelViewAdapter(WHEEL_ADAPTER_DATE, day, null);
        dayAdapter.setWheelView(day);
        day.setViewAdapter(dayAdapter);
        day.addClickingListener(dateTimeWheelclickListener);

        day.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                try {
                    AbstractWheelTextAdapter dayAdapter = new WheelViewAdapter(WHEEL_ADAPTER_DATE, wheel, null);
                    dayAdapter.setWheelView(wheel);
                    wheel.setViewAdapter(dayAdapter);
                }catch (Exception e){

                }
            }
        });



        WheelView hours = (WheelView) view.findViewById(R.id.hour);
        AbstractWheelTextAdapter hourAdapter = new WheelViewAdapter(WHEEL_ADAPTER_HOURS, hours, null);
        hourAdapter.setWheelView(hours);
        hourAdapter.setItemResource(R.layout.day_picker_adapter_view);
        hourAdapter.setItemTextResource(R.id.date_time_label);
        hours.setViewAdapter(hourAdapter);
        //hours.addScrollingListener(dateTimeWheelScrollListener);
        hours.addClickingListener(dateTimeWheelclickListener);
        hours.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                try{
                AbstractWheelTextAdapter hourAdapter = new WheelViewAdapter(WHEEL_ADAPTER_HOURS, wheel, null);
                hourAdapter.setWheelView(wheel);
                hourAdapter.setItemResource(R.layout.day_picker_adapter_view);
                hourAdapter.setItemTextResource(R.id.date_time_label);
                wheel.setViewAdapter(hourAdapter);
                }catch (Exception e){

                }

            }
        });



        WheelView mins = (WheelView) view.findViewById(R.id.mins);
        AbstractWheelTextAdapter minAdapter = new WheelViewAdapter(WHEEL_ADAPTER_MINUTES, mins, "%02d");
        minAdapter.setWheelView(mins);
        minAdapter.setItemResource(R.layout.day_picker_adapter_view);
        minAdapter.setItemTextResource(R.id.date_time_label);
        mins.setViewAdapter(minAdapter);
        mins.addClickingListener(dateTimeWheelclickListener);
        mins.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                try{
                    AbstractWheelTextAdapter minAdapter = new WheelViewAdapter(WHEEL_ADAPTER_MINUTES, wheel, "%02d");
                    minAdapter.setWheelView(wheel);
                    minAdapter.setItemResource(R.layout.day_picker_adapter_view);
                    minAdapter.setItemTextResource(R.id.date_time_label);
                    wheel.setViewAdapter(minAdapter);
                }catch (Exception e){

                }

            }
        });



        WheelView ampm = (WheelView) view.findViewById(R.id.ampm);
        AbstractWheelTextAdapter ampmAdapter = new WheelViewAdapter(WHEEL_ADAPTER_AMPM, ampm, null);
        ampmAdapter.setWheelView(ampm);
        ampmAdapter.setItemResource(R.layout.day_picker_adapter_view);
        ampmAdapter.setItemTextResource(R.id.date_time_label);
        ampm.setViewAdapter(ampmAdapter);
        ampm.addClickingListener(dateTimeWheelclickListener);
        ampm.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                try {
                    AbstractWheelTextAdapter ampmAdapter = new WheelViewAdapter(WHEEL_ADAPTER_AMPM, wheel, null);
                    ampmAdapter.setWheelView(wheel);
                    ampmAdapter.setItemResource(R.layout.day_picker_adapter_view);
                    ampmAdapter.setItemTextResource(R.id.date_time_label);
                    wheel.setViewAdapter(ampmAdapter);
                } catch (Exception e) {

                }

            }
        });


        view.findViewById(R.id.schedule_session_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar schCalendar = getScheduledDateTime();
                if (schCalendar != null) {
                    WorkoutLocationFragment.WORKOUT_SESSION_TYPE = WorkoutLocationFragment.WORKOUT_SESSION_TRAIN_LATER;
                    WorkoutLocationFragment.WORKOUT_SCHEDULE_TIME = schCalendar;
                    FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.DurationFragmentTag, false, false);

                }
            }
        });

        //check location service enable state
        ((MainActivity) getActivity()).checkLocationServiceEnabled();


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moveToNext1HourDate((WheelView) view.findViewById(R.id.day), (WheelView) view.findViewById(R.id.hour), (WheelView) view.findViewById(R.id.mins), (WheelView) view.findViewById(R.id.ampm));

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(enter){


        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void moveToNext1HourDate(final WheelView dayWheelView, final WheelView hoursWheelView, final WheelView  minutesWheelView, final WheelView ampmWheelView){

        final Calendar calendar = Calendar.getInstance();
        final int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int minutes = calendar.get(Calendar.MINUTE);

        // schedule minutes
        if(minutes % 15 > 0){
            calendar.set(Calendar.MINUTE, (15 * (minutes / 15) + 15));
        }

        calendar.add(Calendar.HOUR_OF_DAY, 1);


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dayWheelView.setCurrentItem(calendar.get(Calendar.DAY_OF_YEAR) - currentDayOfYear, true);
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                hours = hours == 0 ? 12 : hours;
                if(hours > 12){
                    hours -= 12;
                }

                hoursWheelView.setCurrentItem(hours - 1, true);
                minutesWheelView.setCurrentItem(MINS_ARRAY.indexOf(calendar.get(Calendar.MINUTE)), true);
                ampmWheelView.setCurrentItem(calendar.get(Calendar.AM_PM), true);

            }
        });



    }

    private OnWheelClickedListener dateTimeWheelclickListener = new OnWheelClickedListener() {
        @Override
        public void onItemClicked(WheelView wheel, int itemIndex) {
            wheel.setCurrentItem(itemIndex, true);
        }
    };



    private Calendar getScheduledDateTime(){
        if(getView() == null) return null;

        WheelView dayView = (WheelView) getView().findViewById(R.id.day);
        WheelView hoursView = (WheelView) getView().findViewById(R.id.hour);
        WheelView minsView = (WheelView) getView().findViewById(R.id.mins);
        WheelView ampmView = (WheelView) getView().findViewById(R.id.ampm);

        Calendar schCalendar = Calendar.getInstance();
        schCalendar.add(Calendar.DAY_OF_YEAR, dayView.getCurrentItem());

        // hours
        int hours = hoursView.getCurrentItem() + 1;
        if(hours == 12){
            hours = 0;
        }

        if(ampmView.getCurrentItem() == 1){
            hours += 12;
        }

        schCalendar.set(Calendar.HOUR_OF_DAY, hours);

        // mins
        schCalendar.set(Calendar.MINUTE, MINS_ARRAY.get(minsView.getCurrentItem()));

        // seconds
        schCalendar.set(Calendar.SECOND, 0);

        // meridian
        /*if(ampmView.getCurrentItem() == 0){
            schCalendar.set(Calendar.AM_PM, Calendar.AM);
        }else{
            schCalendar.set(Calendar.AM_PM, Calendar.PM);
        }*/

        long diffMs = schCalendar.getTime().getTime() - new Date().getTime();
        long diffSec = diffMs / 1000;
        long min = diffSec / 60;

        Log.d("TEST", isServiceAvailableForLocation()+ "selected date=" + DateFormat.format("EEEE MMM dd hh:mm:ss a", schCalendar.getTime()) + " min Difference=" + min);


        if(min < 60 && !isServiceAvailableForLocation()){
            ((MainActivity) getActivity()).showAlertDialog("Sorry!", "You are outside of our on-demand booking area. Please schedule atleast an hour in advance so our trainer will have time to get to you!",
                    "Try Again", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*moveToNext1HourDate((WheelView) getView().findViewById(R.id.day),
                    (WheelView) getView().findViewById(R.id.hour),
                    (WheelView) getView().findViewById(R.id.mins),
                    (WheelView) getView().findViewById(R.id.ampm));*/

                }
            }, null, null, false);

            return null;
        }


        return schCalendar;
    }



    /**
     * Day adapter
     *
     */
    private class WheelViewAdapter extends AbstractWheelTextAdapter {
        private int adapterType;
        private WheelView wheelView;
        private String format;
        protected WheelViewAdapter(int adapterType, WheelView wheelView, String format) {
            super(getActivity(), R.layout.day_picker_adapter_view, NO_RESOURCE);
            setItemTextResource(R.id.date_time_label);

            this.adapterType = adapterType;
            this.wheelView = wheelView;
            this.format = format;

        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            TextView weekday = (TextView) view.findViewById(R.id.date_time_label);
            weekday.setGravity(Gravity.CENTER);
            if(adapterType == WHEEL_ADAPTER_DATE){
                weekday.setGravity(Gravity.LEFT | Gravity.CENTER);
            }

            weekday.setText(getItemText(index));

            try {
                if (wheelView.getCurrentItem() == index) {
                    weekday.setTextColor(getResources().getColor(R.color.workout_request_session_btn_clr));
                    weekday.setTypeface(Typeface.DEFAULT_BOLD);

                } else {
                    weekday.setTextColor(Color.BLACK);
                    weekday.setTypeface(Typeface.DEFAULT);

                }
            } catch(Exception e){

            }

            return view;
        }

        @Override
        protected void configureTextView(TextView view, int position) {
            super.configureTextView(view, position);

        }

        @Override
        public int getItemsCount() {
            if(adapterType == WHEEL_ADAPTER_DATE){
                return 365;// 1 year

            }else if(adapterType == WHEEL_ADAPTER_HOURS){
                return 12; //hours

            }else if(adapterType == WHEEL_ADAPTER_MINUTES){
                return   MINS_ARRAY.size();

            } else if(adapterType == WHEEL_ADAPTER_AMPM){
                return   AM_PM_ARRAY.size();

            }
            return 0;
        }

        @Override
        protected CharSequence getItemText(int index) {
            if(adapterType == WHEEL_ADAPTER_DATE){
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.add(Calendar.DAY_OF_YEAR, index);
                return DateFormat.format("EEE MMM dd", newCalendar.getTime());

            }else if(adapterType == WHEEL_ADAPTER_HOURS){
                return "" + (index + 1);

            }else if(adapterType == WHEEL_ADAPTER_MINUTES){
                return   "" + (format == null ? MINS_ARRAY.get(index) : String.format(format, MINS_ARRAY.get(index)).toString());

            } else if(adapterType == WHEEL_ADAPTER_AMPM){
                return   "" + AM_PM_ARRAY.get(index);

            }

            return "";
        }
    }


    @Override
    protected void onFragmentResumed() {


        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.TitleType);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationText("Back");
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_WHITE_BACK);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);
        ((MainActivity)getActivity()).setTitle("Schedule");

    }

    private boolean isServiceAvailableForLocation(){
        if(((MainActivity) getActivity()).locationService == null) return false;

        try {

            if(ParseUser.getCurrentUser() == null) return true;

            ParseObject clientObj = ParseUser.getCurrentUser().getParseObject("client");
            if(clientObj != null){
                if(clientObj.containsKey("locationOverride")){
                    boolean locOverride = clientObj.getBoolean("locationOverride");
                    if(locOverride){
                        return true;
                    }
                }
            }

            Location philadelphiaLocation = new Location("philadelphia");
            philadelphiaLocation.setLatitude(Constants.PHILADELPHIA_LAT);
            philadelphiaLocation.setLongitude(Constants.PHILADELPHIA_LON);

            Location currentLocation = ((MainActivity) getActivity()).locationService.getCurrentLocation();

            float distanceInMeters = currentLocation.distanceTo(philadelphiaLocation);
            if (distanceInMeters <= Constants.SERVICE_RANGE_METERS) {
                return true;
            }

        }catch (Exception e){
            Log.d("TEST", ">>>>>>>>"+e.getMessage());

        }
        return false;

    }
}
