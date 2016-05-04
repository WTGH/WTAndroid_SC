package com.wetrain.client.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;
import com.wetrain.client.customviews.DetectSoftKeyPadLayout;

/**
 * Created by Administrator on 12/25/15.
 */
public class LoginFragment extends WeTrainBaseFragment implements CustomActionBar.ActionBarOptionsButtonClickListener
        , MainActivity.FacebookLoginCompleteListener, DetectSoftKeyPadLayout.KeyPadListener{


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_screen, container, false);



        view.findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = ((EditText) view.findViewById(R.id.login_username_lbl)).getText().toString();
                String password = ((EditText) view.findViewById(R.id.login_password_lbl)).getText().toString();

                if (userName.length() == 0) {
                    ((MainActivity) getActivity()).showAlertDialog( "Please enter a login email", null, "Close", null);

                } else if (password.length() == 0) {
                    ((MainActivity) getActivity()).showAlertDialog("Please enter a password", null, "Close", null);

                } else {
                    ((MainActivity) getActivity()).showProgressDialog();
                    ParseUser.logInInBackground(userName, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            ((MainActivity) getActivity()).closeProgerssDialog();
                            if (user != null) {
                                FragmentHolder.onBackPressed(getActivity());

                            } else {
                                if (e.getCode() == 100) {
                                    ((MainActivity) getActivity()).showAlertDialog("Login Error", "Please check your internet connection", "Close", null);
                                } else if (e.getCode() == 101) {
                                    ((MainActivity) getActivity()).showAlertDialog("Login Error", "Invalid email or password", "Close", null);

                                }
                            }
                        }
                    });

                }

            }
        });




        (view.findViewById(R.id.login_facebook_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showProgressDialog();
                ((MainActivity) getActivity()).loginWithFacebook(LoginFragment.this);
            }
        });

        view.findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View alertView = inflater.inflate(R.layout.forget_password_alert_view, null);

                final Dialog alert = new Dialog(getActivity(), R.style.default_dialog_theme);
                alert.getWindow().setContentView(alertView);



                ((Button) alertView.findViewById(R.id.alert__cancel_btn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });

                ((Button) alertView.findViewById(R.id.alert__Reset_btn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        String email = ((EditText) alertView.findViewById(R.id.forget_passsword_email_txt)).getText().toString();
                        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                            @Override
                            public void done(ParseException e) {
                                String message = "";
                                if (e == null) {
                                    message = "Email to reset password is sent. Please check your email.";
                                }else{
                                    message = "There was an error on request. Please try again." + e.getMessage();
                                }

                                ((MainActivity) getActivity()).showAlertDialog("Message", message, "Cancel", null);


                            }
                        });

                    }
                });

                alert.show();
            }
        });

        return view;
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
    public void onFacebookLoginCompleted(String e, boolean isNewUser) {
        ((MainActivity) getActivity()).closeProgerssDialog();

        if(e == null){
            if(isNewUser){
                FragmentHolder.onBackPressed(getActivity());
                Bundle bundle = new Bundle();
                bundle.putBoolean(UserProfileInfoFragment.BUNDLE_SCREEN_FROM_SIGNUP_LOGIN_KEY, true);
                FragmentHolder.setFragment(getActivity(), bundle, FragmentHolder.FragmentTags.UserProfileFragmentTag, false, false);
            }else {
                FragmentHolder.onBackPressed(getActivity());
            }

        }else{
            ((MainActivity) getActivity()).showAlertDialog("Facebook Error", e, "Ok", null);

        }


    }

    @Override
    protected void onFragmentResumed() {
        ((MainActivity) getActivity()).setStatusBarColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).setTitle("");
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationText("Cancel");
        ((MainActivity)getActivity()).setActionBarBgColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.bottom_bar_text_selected_clr), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.GONE);

    }


    @Override
    public void onOptionsButtonClicked() {

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
