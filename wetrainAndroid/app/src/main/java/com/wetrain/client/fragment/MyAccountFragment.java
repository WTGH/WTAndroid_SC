package com.wetrain.client.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

import java.io.File;
import java.util.Date;


/**
 * Created by Administrator on 12/25/15.
 */
public class MyAccountFragment extends WeTrainBaseFragment implements AdapterView.OnItemClickListener{


    private static final String[] ACCOUNT_LIST_ITEM_LOGIN_USER = new String[]{ "Edit Profile Info", "Update your credit card", "Workout descriptions","View tutorial", "Feedback", "Terms of Service & Privacy", "Credits", "Logout"};
    private static final String[] ACCOUNT_LIST_ITEM_NEW_USER = new String[]{ "Log in", "Sign up","Workout descriptions", "View tutorial", "Feedback", "Terms of Service & Privacy", "Credits"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myaccount_screen, container, false);

        ((ListView) view.findViewById(R.id.myaccount_listview)).setOnItemClickListener(this);

        return  view;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    class MyAccountAdapter extends BaseAdapter{

        private String[] listItem;
        public MyAccountAdapter() {
            Log.d("TEST", "Current user=" + ParseUser.getCurrentUser());
            if(ParseUser.getCurrentUser() == null){
                listItem = ACCOUNT_LIST_ITEM_NEW_USER;
            }else{
                listItem = ACCOUNT_LIST_ITEM_LOGIN_USER;
            }
        }

        @Override
        public int getCount() {
            return listItem.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder = null;
            if (v == null) {
                v = getActivity().getLayoutInflater().inflate(R.layout.my_account_adapter_view, null);
                holder = new ViewHolder();
                holder.txt = (TextView) v.findViewById(R.id.myaccount_txtview);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();

            }

            holder.txt.setText("" + listItem[position].toString());


            return v;
        }
        public class ViewHolder {

            TextView txt;

        }
    }

    @Override
    protected void onFragmentResumed() {

        ((MainActivity)getActivity()).setStatusBarColor(R.color.account_top_bar_clr);

        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).setTitle("My Account");
        ((MainActivity) getActivity()).setActionBarBgColor(R.color.account_top_bar_clr);
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.black), CustomActionBar.TitleType);
        ((MainActivity) getActivity()).setNavigationTextVisibility(View.GONE);
        ((MainActivity) getActivity()).setOptionButtonVisibility(View.GONE);
        ((MainActivity) getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);

        /*
         * referesh List
         */
        ((ListView) getView().findViewById(R.id.myaccount_listview)).setAdapter(new MyAccountAdapter());

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0){
            if(ParseUser.getCurrentUser() == null) {
                //Login
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.LoginFragmentTag, false, false);
            }else{
                // update user profile
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.UserProfileFragmentTag, false, false);
            }

        } else if(position ==1){
            if(ParseUser.getCurrentUser() == null) {
                //Sign up
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.SignUpFragmentTag, false, false);

            }else{
                // update user profile
                FragmentHolder.setFragment(getActivity(),null, FragmentHolder.FragmentTags.CreditsFragmentTag, false, false);

            }

        } else if(position ==3){
            //View Tutorials
            FragmentHolder.setFragment(getActivity(),null, FragmentHolder.FragmentTags.TutorialFragmentTag, false, false);

        } else if(position ==2){
            //Workout descriptions
            FragmentHolder.setFragment(getActivity(),null, FragmentHolder.FragmentTags.WorkoutDescriptionFragmentTag, false, false);

        } else if(position ==4){
            //Feedback
            if(ParseUser.getCurrentUser() == null){
                ((MainActivity) getActivity()).showAlertDialog("Log in first?", "You are not logged in. Please log in first so we can respond to you.", "Close", null, "Leave Anonymous Feedback", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.FeedBackFragmentTag, false, false);

                    }
                }, false);

            }else {
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.FeedBackFragmentTag, false, false);
            }

        }else if(position == 5){
            //Terms of service and Provacy
            FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.TermsServiceFragmentTag, false, false);

        } else if(position == 6){
            //Credits
            String msg = "Copyright 2016 WeTrain, LLC Version ";
            try {
                msg = msg + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            }catch(Exception e){

            }
            ((MainActivity) getActivity()).showAlertDialog("Credits", msg, "Close", null, null, null, false);


        } else if(position == 7){
            //logout
            //check exist user workout session
            if(ParseUser.getCurrentUser() != null) {
                if (ParseUser.getCurrentUser().getParseObject("client") != null) {
                    final ParseObject clientParseObj = ParseUser.getCurrentUser().getParseObject("client");
                    if(clientParseObj != null) {
                        ((MainActivity) getActivity()).showProgressDialog();

                        clientParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if(clientParseObj.containsKey("workout")) {
                                    final ParseObject workoutParseObj = clientParseObj.getParseObject("workout");
                                    if (workoutParseObj != null) {
                                        workoutParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {

                                                if (workoutParseObj.containsKey("status")) {
                                                    if (workoutParseObj.get("status") != null) {
                                                        String status = workoutParseObj.getString("status");
                                                        if ((status.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString()) || status.equals(Constants.WORKOUT_REQUEST_STATE.Searching.toString()))) {
                                                            ((MainActivity) getActivity()).closeProgerssDialog();

                                                            String title = "Cancel request?";
                                                            String buttonTitle = "Cancel training";
                                                            String message = "Are you sure you want to cancel your training request?";

                                                            if (status.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString())) {
                                                                title = "Cancel session?";
                                                                buttonTitle = "Cancel session";
                                                                message = "Your session hasn't started yet. Do you want to cancel the session?";
                                                            }

                                                            ((MainActivity) getActivity()).showAlertDialog(title, message, buttonTitle, new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    workoutParseObj.put("status", Constants.WORKOUT_REQUEST_STATE.Cancelled.toString());
                                                                    workoutParseObj.put("end", new Date());
                                                                    workoutParseObj.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, false);
                                                                            logout();
                                                                        }
                                                                    });

                                                                }
                                                            }, "Cancel", null, true);

                                                        } else {
                                                            logout();
                                                        }

                                                    } else {
                                                        logout();
                                                    }
                                                } else {
                                                    logout();
                                                }
                                            }
                                        });
                                    } else {
                                        logout();
                                    }
                                }else{
                                    logout();
                                }
                            }
                        });
                    }else{
                        logout();
                    }


                }else{
                    logout();
                }
            }


        }


    }

    private void logout(){
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {

                FragmentHolder.removeTrainTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.HomeFragmenttag, false);

                ((MainActivity) getActivity()).closeProgerssDialog();
                if (e == null) {
                    if(UserProfileInfoFragment.getImageUri() != null) {
                        File profileImgFile = new File(UserProfileInfoFragment.getImageUri().getPath());
                        profileImgFile.delete();
                    }
                    ((ListView) getView().findViewById(R.id.myaccount_listview)).setAdapter(new MyAccountAdapter());

                } else {
                    Toast.makeText(getActivity(), "Logout error " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
