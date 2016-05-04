package com.wetrain.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.wetrain.client.R;
import com.wetrain.client.Utills;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;
import com.wetrain.client.customviews.DetectSoftKeyPadLayout;


/**
 * Created by Administrator on 12/25/15.
 */
public class SignUpScreenFragment extends WeTrainBaseFragment implements CustomActionBar.ActionBarOptionsButtonClickListener ,
                        MainActivity.FacebookLoginCompleteListener, DetectSoftKeyPadLayout.KeyPadListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_screen, container, false);

        view.findViewById(R.id.signup_facebook_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showProgressDialog();
                ((MainActivity) getActivity()).loginWithFacebook(SignUpScreenFragment.this);
            }
        });


        return view;
    }

    @Override
    public void onFacebookLoginCompleted(String exception, boolean isNewUser) {
        ((MainActivity) getActivity()).closeProgerssDialog();

        if(exception == null){
            if(isNewUser){
                FragmentHolder.onBackPressed(getActivity());
                Bundle bundle = new Bundle();
                bundle.putBoolean(UserProfileInfoFragment.BUNDLE_SCREEN_FROM_SIGNUP_LOGIN_KEY, true);
                FragmentHolder.setFragment(getActivity(), bundle, FragmentHolder.FragmentTags.UserProfileFragmentTag, false, false);
            }else {
                FragmentHolder.onBackPressed(getActivity());
            }

        }else{
            ((MainActivity) getActivity()).showAlertDialog("Facebook Error", exception, "Ok", null);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setLayoutChangeListener();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setKeyPadListener(this, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).removeLayoutChangeListener();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setKeyPadListener(null, true);

    }

    @Override
    protected void onFragmentResumed() {
        ((MainActivity) getActivity()).setStatusBarColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).setTitle("Sign Up");
        ((MainActivity)getActivity()).setActionBarBgColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.black), CustomActionBar.TitleType);
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
        ((MainActivity)getActivity()).setOptionsButtonLabel("Save");
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.OptionType);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationText("Cancel");
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.bottom_bar_text_selected_clr), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setActionbarButtonClickListener(this);

        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.GONE);


    }



    @Override
    public void onOptionsButtonClicked() {
        /*
         * Sign up new user
         */
        String email = ((EditText) getView().findViewById(R.id.signup_user_email_lbl)).getText().toString().trim();
        String password = ((EditText) getView().findViewById(R.id.signup_password_lbl)).getText().toString();
        String reenterPasword = ((EditText) getView().findViewById(R.id.signup_reenter_password_lbl)).getText().toString();

        boolean acceptTermsAndCond = ((CheckBox) getView().findViewById(R.id.signup_accept_tos_chkbox)).isChecked();

        if(email.length() == 0){
            ((MainActivity) getActivity()).showAlertDialog("Please enter your email as a username",null, "Close", null);

        }else if(!Utills.isValidEmailAddress(email)){
            ((MainActivity) getActivity()).showAlertDialog( "Please enter a valid email address",null, "Close" , null);

        }else if(password.length() == 0) {
            ((MainActivity) getActivity()).showAlertDialog("Please enter a password",null, "Close" , null);

        }else if(reenterPasword.length() == 0){
            ((MainActivity) getActivity()).showAlertDialog( "Please enter a password confirmation",null, "Close" , null);

        }else if(!password.equals(reenterPasword)){
            ((MainActivity) getActivity()).showAlertDialog("Please make sure password matches confirmation",null, "Close" , null);

        }/*else if(!acceptTermsAndCond){
            ((MainActivity) getActivity()).showAlertDialog("Please agree to the Terms and Conditions", "You must read the Terms and Conditions and check the box to continue.", "Ok" , null);

        }*/else{
            ((MainActivity) getActivity()).showProgressDialog();
            ParseUser signupUser = new ParseUser();
            signupUser.setUsername(email);
            signupUser.setPassword(password);
            signupUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        final ParseObject clientParseObj = ParseObject.create("Client");
                        clientParseObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null) {
                                    ((MainActivity) getActivity()).closeProgerssDialog();
                                    ParseUser.getCurrentUser().put("client", clientParseObj);
                                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                FragmentHolder.removeAccountTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.AccountFragmentTag, false);
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(UserProfileInfoFragment.BUNDLE_SCREEN_FROM_SIGNUP_LOGIN_KEY, true);
                                                FragmentHolder.setFragment(getActivity(), bundle, FragmentHolder.FragmentTags.UserProfileFragmentTag, false, false);

                                            } else {
                                                showSignupError(e);

                                            }
                                        }
                                    });
                                }else{
                                    showSignupError(e);
                                }
                            }
                        });

                    }else{
                        showSignupError(e);
                    }
                }
            });
        }

    }

    private void showSignupError(ParseException e){
        ((MainActivity) getActivity()).closeProgerssDialog();
        String msg = "";
        if(e.getCode() == 100){
            msg = "Please check your internet connection";

        }else if(e.getCode() == 202){
            msg = "Username already taken";

        }else{
            msg = e.getMessage();
        }

        ((MainActivity) getActivity()).showAlertDialog("Signup error", msg , "Ok", null);
    }

    @Override
    public void softKeyPadShown() {

    }

    @Override
    public void softKeyPadHidden() {

    }

    @Override
    public void clickedOnMaskLayout() {

    }


}
