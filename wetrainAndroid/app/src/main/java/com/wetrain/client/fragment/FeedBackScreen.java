package com.wetrain.client.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wetrain.client.R;
import com.wetrain.client.Utills;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

/**
 * Created by Administrator on 12/28/15.
 */
public class FeedBackScreen extends WeTrainBaseFragment implements CustomActionBar.ActionBarOptionsButtonClickListener {
    private static final String[] FEEDBACK_CATEGORY_ARRAY = new String[] {"Select a category", "App issues", "Account issues", "General feedback"};
    private int lastSelectedCategory;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.feedback_screen, container, false);


        DrawableCompat.setTint(((RatingBar) view.findViewById(R.id.feedback_rating_bar)).getProgressDrawable(), Color.parseColor("#51e09d"));



        ((TextView) view.findViewById(R.id.feedback_category_txt)).setKeyListener(null);
        view.findViewById(R.id.feedback_category_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ((MainActivity) getActivity()).showBottomListAlert(FEEDBACK_CATEGORY_ARRAY, lastSelectedCategory, new MainActivity.AlertListItemSelectListener() {
                    @Override
                    public void onItemSelected(int pos) {
                        ((TextView) getView().findViewById(R.id.feedback_category_txt)).setText(FEEDBACK_CATEGORY_ARRAY[pos]);
                        lastSelectedCategory = pos;
                    }

                    @Override
                    public void onNextBtnClicked() {
                        v.requestFocus(View.FOCUS_DOWN);
                    }
                });
            }
        });





        ((EditText) view.findViewById(R.id.feedback_email_txt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    ((MainActivity) getActivity()).setOptionButtonEnable(true);

                } else {
                    ((MainActivity) getActivity()).setOptionButtonEnable(false);

                }
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(ParseUser.getCurrentUser() != null){
                    if(ParseUser.getCurrentUser().getString("email") != null){
                        ((EditText) view.findViewById(R.id.feedback_email_txt)).setText(ParseUser.getCurrentUser().getString("email"));

                    }else if(ParseUser.getCurrentUser().getUsername() != null){
                        if(Utills.isValidEmailAddress(ParseUser.getCurrentUser().getUsername())){
                            ((EditText) view.findViewById(R.id.feedback_email_txt)).setText(ParseUser.getCurrentUser().getUsername());
                        }
                    }
                }
            }
        });


        return view;
    }



    @Override
    public void onOptionsButtonClicked() {


        String email = ((EditText) getView().findViewById(R.id.feedback_email_txt)).getText().toString();
        if(!Utills.isValidEmailAddress(email)){
            ((MainActivity) getActivity()).showAlertDialog("Please enter a valid email so we can get back to you!", null, "Ok", null);
            return;
        }

        ((MainActivity) getActivity()).showProgressDialog();


        ParseObject feedbackObj = ParseObject.create("Feedback");
        feedbackObj.put("email", email);

        RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.feedback_rating_bar);
        if(ratingBar.getRating() > 0){
            feedbackObj.put("rating", ratingBar.getRating());
        }

        if(ParseUser.getCurrentUser() != null){
            if(ParseUser.getCurrentUser().getObjectId() != null){
                feedbackObj.put("user", ParseUser.getCurrentUser());
            }
        }


        String category = ((TextView) getView().findViewById(R.id.feedback_category_txt)).getText().toString();
        if(category.length() > 0){
            feedbackObj.put("category", category);
        }

        String message = ((EditText) getView().findViewById(R.id.feedback_comments_txt)).getText().toString();
        feedbackObj.put("message", message);

        feedbackObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ((MainActivity) getActivity()).closeProgerssDialog();
                if(e == null){
                    ((MainActivity) getActivity()).showAlertDialog("Thanks!", "Your feedback has been submitted", "Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHolder.onBackPressed(getActivity());
                        }
                    }, null, null, false);

                }else{
                    ((MainActivity) getActivity()).showAlertDialog("Error submitting feedback", "There was an issue sending your feedback. Please try again!", "Ok", null);
                }
            }
        });



    }

    @Override
    protected void onFragmentResumed() {

        ((MainActivity) getActivity()).setStatusBarColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.GONE);

        ((MainActivity)getActivity()).setTitle("");
        ((MainActivity)getActivity()).setActionBarBgColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
        ((MainActivity)getActivity()).setOptionsButtonLabel("Submit");
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.OptionType);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationText("Close");
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setActionbarButtonClickListener(this);
        ((MainActivity) getActivity()).setOptionButtonEnable(false);




    }


}
