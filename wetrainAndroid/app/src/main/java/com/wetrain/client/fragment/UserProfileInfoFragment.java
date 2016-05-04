package com.wetrain.client.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.Utills;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;
import com.wetrain.client.customviews.DetectSoftKeyPadLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 12/28/15.
 */
public class UserProfileInfoFragment extends WeTrainBaseFragment implements CustomActionBar.ActionBarOptionsButtonClickListener,
        DetectSoftKeyPadLayout.KeyPadListener {

    public static final String BUNDLE_SCREEN_FROM_SIGNUP_LOGIN_KEY = "From_SignUp_Login";

    private static final List<String> GENDER_ARRAY= Arrays.asList(new String[]{"Male", "Female", "Other"});
    public static Context context;
    private String defaultCameraPackage;
    private static final int SELECT_PICTURE = 1;
    private static final int CAPTURE_IMAGE = 2;
    private static final int CROP_IMAGE = 3;

    private PackageManager packageManager = null;
    private Bitmap photo;
    private static String PersonImageFile;
    private Uri mImageCaptureUri;
    private String cameraFile;
    private int orientation;
    private SensorManager sensorManager;
    private ProgressBar progressBar;
    //private String imgPath;
    //private AlertDialog.Builder alert;
    private ParseObject clientParseObj;
    private int lastSelectedGender;

    private Dialog photoChooseAlertDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.edit_profile_screen, container, false);

        lastSelectedGender = 0;
        context = getActivity();
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        packageManager = getActivity().getPackageManager();


        ((LinearLayout)view).findViewById(R.id.edit_photo_lyt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View alertView = getActivity().getLayoutInflater().inflate(R.layout.contact_trainer_alert, null);
                photoChooseAlertDialog = new Dialog(getActivity(), R.style.contact_trainer_dialog_theme);
                photoChooseAlertDialog.getWindow().setContentView(alertView);

                ((Button) alertView.findViewById(R.id.call_trainer_btn)).setText("Take a Photo");
                alertView.findViewById(R.id.call_trainer_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoChooseAlertDialog.dismiss();

                        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
                        /*// ******** code for crop image
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 0);
                        intent.putExtra("aspectY", 0);
                        intent.putExtra("outputX", 200);
                        intent.putExtra("outputY", 150);*/
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, CAPTURE_IMAGE);


                    }
                });

                ((Button) alertView.findViewById(R.id.message_trainer_btn)).setText("Pick from Gallery");
                alertView.findViewById(R.id.message_trainer_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoChooseAlertDialog.dismiss();
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
                        startActivityForResult(galleryIntent, SELECT_PICTURE);



                    }
                });

                alertView.findViewById(R.id.dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoChooseAlertDialog.dismiss();

                    }
                });


                photoChooseAlertDialog.show();


            }
        });
        String redStarTxt = "<font color='red'>*</font>";

        if(ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            ((EditText) view.findViewById(R.id.user_profile_facebook_email_txt)).setVisibility(View.VISIBLE);
            ((EditText) view.findViewById(R.id.user_profile_facebook_email_txt)).setText(ParseUser.getCurrentUser().getString("email"));
        }


        ((TextView) view.findViewById(R.id.user_profile_required_txt)).setText(Html.fromHtml(redStarTxt + " Required"));
        ((EditText) view.findViewById(R.id.user_profile_facebook_email_txt)).setHint(Html.fromHtml("Email" + redStarTxt));
        ((EditText) view.findViewById(R.id.user_profile_mobile_number_txt)).setHint(Html.fromHtml("Phone Number" + redStarTxt));
        ((TextView) view.findViewById(R.id.user_profile_card_info_txt)).setHint(Html.fromHtml("Credit Card Info" + redStarTxt ));
        ((EditText) view.findViewById(R.id.user_profile_first_name_txt)).setHint("First Name");
        ((EditText) view.findViewById(R.id.user_profile_last_name_txt)).setHint("Last Name");
        ((TextView) view.findViewById(R.id.user_profile_gender_txt)).setHint("Gender");
        ((EditText) view.findViewById(R.id.user_profile_age_txt)).setHint("Age");
        ((EditText) view.findViewById(R.id.user_profile_injuries_txt)).setHint("List All Injuries & Medical Conditions");
        ((CheckBox) view.findViewById(R.id.signup_accept_tos_chkbox)).setText(Html.fromHtml(getString(R.string.agreetext) + redStarTxt));




        ((TextView) view.findViewById(R.id.user_profile_gender_txt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ((MainActivity) getActivity()).showBottomListAlert((String[]) GENDER_ARRAY.toArray(), lastSelectedGender, new MainActivity.AlertListItemSelectListener() {
                    @Override
                    public void onItemSelected(int pos) {
                        lastSelectedGender = pos;
                        ((TextView) getView().findViewById(R.id.user_profile_gender_txt)).setText(GENDER_ARRAY.get(pos));
                    }

                    @Override
                    public void onNextBtnClicked() {
                        v.requestFocus(View.FOCUS_DOWN);
                    }
                });
            }
        });

        ((EditText) view.findViewById(R.id.user_profile_mobile_number_txt)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Utills.hideSoftKeyboard(v);
                    view.findViewById(R.id.user_profile_card_info_txt).performClick();
                    return true;
                }
                return false;
            }
        });




        view.findViewById(R.id.user_profile_card_info_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        // Utills.hideSoftKeyboard(v);
                        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.CreditsFragmentTag, false, false);
                    }
                });


            }
        });



        return view;
    }

    private void initTermsConditionView(){

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        float topBarHeight = getResources().getDimension(R.dimen.action_bar_height);
        float bottomBarHeight = getResources().getDimension(R.dimen.bottom_tab_height);
        float margin = getResources().getDimension(R.dimen.large_padding_size);
        height -=  (Utills.getStatusBarHeight(getActivity()) +  topBarHeight +  bottomBarHeight + bottomBarHeight +margin + margin);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , height);
        ((ScrollView) getView().findViewById(R.id.terms_cond_scrol_view)).setLayoutParams(lp);



        ((ScrollView) getView().findViewById(R.id.user_profile_scroll_view)).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                getView().findViewById(R.id.terms_cond_scrol_view).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        ((ScrollView) getView().findViewById(R.id.terms_cond_scrol_view)).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of
                // child view
                ((ScrollView) getView().findViewById(R.id.user_profile_scroll_view)).fullScroll(View.FOCUS_DOWN);
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTermsConditionView();
        initUserProfile();

        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setLayoutChangeListener();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setKeyPadListener(this, false);

    }



    @Override
    public void onPause() {
        super.onPause();

        if(photoChooseAlertDialog != null){
            if(photoChooseAlertDialog.isShowing()){
                photoChooseAlertDialog.dismiss();
            }
        }
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).removeLayoutChangeListener();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setKeyPadListener(null, true);

    }


    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        try {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (getView() != null && enter) {
                        if (((TextView) getView().findViewById(R.id.terms_cond_txt)).getText().length() <= 0)
                            ((TextView) getView().findViewById(R.id.terms_cond_txt)).setText(Constants.TERMS_CONDITIONS);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            return anim;

        } catch (Resources.NotFoundException e){
        }
        return super.onCreateAnimation(transit, enter, nextAnim);

    }

    private void initUserProfile(){
        try {

            clientParseObj = ParseUser.getCurrentUser().getParseObject("client");

            if(clientParseObj != null) {
                ((MainActivity) getActivity()).showProgressDialog();
                clientParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        ((MainActivity) getActivity()).closeProgerssDialog();

                        if (e == null) {
                            ((EditText) getView().findViewById(R.id.user_profile_first_name_txt)).setText(clientParseObj.getString("firstName"));
                            ((EditText) getView().findViewById(R.id.user_profile_last_name_txt)).setText(clientParseObj.getString("lastName"));
                            ((EditText) getView().findViewById(R.id.user_profile_mobile_number_txt)).setText(clientParseObj.getString("phone"));
                            ((TextView) getView().findViewById(R.id.user_profile_gender_txt)).setText(clientParseObj.getString("gender"));
                            ((EditText) getView().findViewById(R.id.user_profile_age_txt)).setText(clientParseObj.getString("age"));
                            ((EditText) getView().findViewById(R.id.user_profile_injuries_txt)).setText(clientParseObj.getString("injuries"));
                            ((CheckBox) getView().findViewById(R.id.signup_accept_tos_chkbox)).setChecked(clientParseObj.getBoolean("checkedTOS"));

                            if(clientParseObj.getString("gender") != null){
                                lastSelectedGender = GENDER_ARRAY.indexOf(clientParseObj.getString("gender"));
                            }


                            ((TextView)getView().findViewById(R.id.user_profile_add_edit_photo_btn)).setText("Add photo");

                            if(clientParseObj.getString("stripeFour") != null) {
                                if(clientParseObj.getString("stripeFour").length() > 0) {
                                    ((TextView) getView().findViewById(R.id.user_profile_card_info_txt)).setText("Credit Card: *" + clientParseObj.getString("stripeFour"));
                                }
                            }

                            ParseFile imgFile = clientParseObj.getParseFile("photo");
                            Log.d("TEST","img file="+imgFile);

                            if (imgFile != null) {
                                imgFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (data != null) {
                                            ((TextView)getView().findViewById(R.id.user_profile_add_edit_photo_btn)).setText("Edit photo");
                                            Bitmap userAvatorBmpImg = BitmapFactory.decodeByteArray(data, 0, data.length);

                                            try {
                                                FileOutputStream fos = new FileOutputStream(new File(getImageUri().getPath()));
                                                fos.write(data);
                                                //userAvatorBmpImg.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                                fos.close();
                                            } catch (Exception ex) {

                                            }
                                            ((ImageView) getView().findViewById(R.id.user_profile_user_avator_txt)).setImageBitmap(userAvatorBmpImg);
                                            (getView().findViewById(R.id.picture_frame_view)).setVisibility(View.VISIBLE);


                                        /*Glide.with(UserProfileInfoFragment.this).load(data)
                                                .listener(new RequestListener<byte[], GlideDrawable>() {
                                                    @Override
                                                    public boolean onException(Exception e, byte[] model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onResourceReady(GlideDrawable resource, byte[] model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                        (getView().findViewById(R.id.picture_frame_view)).setVisibility(View.VISIBLE);

                                                        return false;
                                                    }
                                                })
                                                .centerCrop()
                                                .crossFade()
                                                .error(R.drawable.add_user)
                                                .into(((ImageView) getView().findViewById(R.id.user_profile_user_avator_txt)));*/
                                        }
                                    }
                                });
                            }
                        }else{
                            ((MainActivity) getActivity()).showAlertDialog("Error", ""+e.getMessage(), "Ok", null);
                        }
                    }
                });
            }

        } catch (Exception e){
        }

    }

    @Override
    protected void onFragmentResumed() {

        ((MainActivity) getActivity()).setStatusBarColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setTitle("User Info");
        ((MainActivity)getActivity()).setActionBarBgColor(R.color.account_top_bar_clr);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.black), CustomActionBar.TitleType);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setOptionsButtonLabel("Save");
        ((MainActivity)getActivity()).setNavigationText("My Account");
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.OptionType);

        ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_BLUE_BACK);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.NavigationType);
        ((MainActivity)getActivity()).setActionbarButtonClickListener(this);

        if(getArguments() != null){
            if(getArguments().getBoolean(BUNDLE_SCREEN_FROM_SIGNUP_LOGIN_KEY, false)){
                ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.GONE);
                ((MainActivity)getActivity()).setNavigationText("Cancel");
                ((MainActivity)getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
                ((MainActivity)getActivity()).setTextColor(getResources().getColor(R.color.bottom_bar_text_selected_clr), CustomActionBar.NavigationType);


            }
        }

        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setLayoutChangeListener();
        ((DetectSoftKeyPadLayout.DetectSoftKeyPadListener) getActivity()).setKeyPadListener(this, false);

    }

    private boolean hasDefualtCameraApp(String action) {
        final PackageManager packageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;

    }



    @Override
    public void onOptionsButtonClicked() {
        /*
         * save the User Info
         */
        String firstName = ((EditText) getView().findViewById(R.id.user_profile_first_name_txt)).getText().toString();
        String lastName = ((EditText) getView().findViewById(R.id.user_profile_last_name_txt)).getText().toString();
        String mobileNumber = ((EditText) getView().findViewById(R.id.user_profile_mobile_number_txt)).getText().toString();
        String gender = ((TextView) getView().findViewById(R.id.user_profile_gender_txt)).getText().toString();
        String age = ((EditText) getView().findViewById(R.id.user_profile_age_txt)).getText().toString();
        String injuriesMedicalCond = ((EditText) getView().findViewById(R.id.user_profile_injuries_txt)).getText().toString();



        boolean checkedTOS = ((CheckBox) getView().findViewById(R.id.signup_accept_tos_chkbox)).isChecked();

        Bitmap userAvatorBmpImg = decodeFile(getImageUri().getPath());

        if(mobileNumber.length() == 0){
            ((MainActivity) getActivity()).showAlertDialog("Please enter a valid phone number.", null, "Ok", null);
            return;

        }

        if(((TextView) getView().findViewById(R.id.user_profile_card_info_txt)).getText().length() == 0){//check credit card
            ((MainActivity) getActivity()).showAlertDialog("Please enter a valid credit card.", null, "Ok", null);
            return;

        }

        if(getView().findViewById(R.id.user_profile_facebook_email_txt).getVisibility() == View.VISIBLE){
            if(((EditText) getView().findViewById(R.id.user_profile_facebook_email_txt)).getText().length() == 0){
                ((MainActivity) getActivity()).showAlertDialog("Please enter a valid email address.", null, "Ok", null);
                return;
            }
        }


         /*if(!checkedTOS){
            ((MainActivity) getActivity()).showAlertDialog("Please agree to the Terms and Conditions", "You must read the Terms and Conditions and check the box to continue.", "Ok", null);
            return;
        }*/



        /*
         * update user profile
         */
        if(clientParseObj == null){
            clientParseObj = ParseObject.create("Client");
        }
        ((MainActivity) getActivity()).showProgressDialog();
        clientParseObj.put("firstName", firstName);
        clientParseObj.put("lastName", lastName);
        clientParseObj.put("phone", mobileNumber);
        clientParseObj.put("age", age);
        clientParseObj.put("gender", gender);
        clientParseObj.put("injuries", injuriesMedicalCond);
        clientParseObj.put("checkedTOS", checkedTOS);

        clientParseObj.put("user", ParseUser.getCurrentUser());

        if(userAvatorBmpImg != null) {
            ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
            userAvatorBmpImg.compress(Bitmap.CompressFormat.JPEG, 100, imgStream);
            final ParseFile imgFile = new ParseFile("profile.jpg", imgStream.toByteArray());
            imgFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    clientParseObj.put("photo", imgFile);
                    saveUserProfile(clientParseObj);
                }
            });



        }else{
            saveUserProfile(clientParseObj);
        }







    }

    private void saveUserProfile(final ParseObject clientParseObj){
        clientParseObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.getCurrentUser().put("client", clientParseObj);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ((MainActivity) getActivity()).closeProgerssDialog();
                            if (e == null) {
                                //update user email
                                if(getView().findViewById(R.id.user_profile_facebook_email_txt).getVisibility() == View.VISIBLE){
                                    ParseUser.getCurrentUser().put("email", ((EditText) getView().findViewById(R.id.user_profile_facebook_email_txt)).getText().toString());
                                    ParseUser.getCurrentUser().saveInBackground();
                                }
                                FragmentHolder.onBackPressed(getActivity());

                            } else {
                                ((MainActivity) getActivity()).showAlertDialog("Error creating profile", "We could not create your user profile."+e.getMessage(), "Ok", null);

                            }
                        }
                    });

                } else {
                    ((MainActivity) getActivity()).closeProgerssDialog();
                    ((MainActivity) getActivity()).showAlertDialog("Error creating profile", "We could not create your user profile."+e.getMessage(), "Ok", null);

                }
            }
        });
    }

    public static Uri getImageUri() {
        File file;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            file = new File(Environment.getExternalStorageDirectory() + "/WeTrain/", "profile" + ".png");
            /*if(!file.exists()){
                file.mkdirs();
                try {
                    file.createNewFile();
                }catch(Exception e){

                }
            }*/
        }else {
            file = new File(Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath() + "/WeTrain/" , "profile.png");

            /*if(!file.exists()){
                file.mkdirs();
                try {
                    file.createNewFile();
                }catch(Exception e){

                }
            }*/
        }
        return Uri.fromFile(file);
    }

    private void saveProfileImgageFile(Bitmap img){
        try {
            FileOutputStream fos = new FileOutputStream(new File(getImageUri().getPath()));
            img.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }catch (Exception e){

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CAPTURE_IMAGE:
                Bitmap userAvatorBmpImg = decodeFile(getImageUri().getPath());
                if(userAvatorBmpImg != null) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getRightAngleImage(getImageUri().getPath()));

                    userAvatorBmpImg = Bitmap.createBitmap(userAvatorBmpImg, 0, 0, userAvatorBmpImg.getWidth(), userAvatorBmpImg.getHeight(), matrix, true);

                    saveProfileImgageFile(userAvatorBmpImg);

                    ((ImageView) getView().findViewById(R.id.user_profile_user_avator_txt)).setImageBitmap(userAvatorBmpImg);
                    (getView().findViewById(R.id.picture_frame_view)).setVisibility(View.VISIBLE);

                    performCrop();
                }

                break;

             case SELECT_PICTURE:

                if(data == null) break;

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                Log.d("TEST", "gallery file img = " + filePath);

                 if(filePath !=null) {
                     userAvatorBmpImg = decodeFile(filePath);
                     saveProfileImgageFile(userAvatorBmpImg);

                     performCrop();
                 }

                break;

            case CROP_IMAGE:
                if(data == null) break;

                Uri fileUri = data.getData();

                ((ImageView) getView().findViewById(R.id.user_profile_user_avator_txt)).setImageBitmap(decodeFile(fileUri.getPath()));
                (getView().findViewById(R.id.picture_frame_view)).setVisibility(View.VISIBLE);


                break;

        }

    }

    private void performCrop(){
        try {


            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setType("image/*");

            List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities( cropIntent, 0 );

            Log.d("TEST", "app list="+list);
            if(list.size() > 0) {
                ResolveInfo res = list.get(0);
                Log.d("TEST", res.activityInfo.packageName+"|"+res.activityInfo.name);
                cropIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            }
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
            //indicate image type and Uri
            cropIntent.setDataAndType(getImageUri(), "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE);


        }catch(ActivityNotFoundException e){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private int getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return degree;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public Bitmap decodeFile(String path) {
        try {
            // Decode deal_image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            o.inJustDecodeBounds = false;

            // The new size we want to scale to
            final int REQUIRED_SIZE = 200;

            int widthtmp = o.outWidth;
            int heighttmp = o.outHeight;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (true)
            {
               if (widthtmp / 2 < REQUIRED_SIZE || heighttmp / 2 < REQUIRED_SIZE)
                break;
                widthtmp /= 2;
                heighttmp /= 2;
                scale++;
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            Bitmap userAvatorBmpImg = BitmapFactory.decodeFile(path, o2);
            return userAvatorBmpImg;

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }



    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {

        int targetWidth = 70;
        int targetHeight = 70;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if(b.getWidth() > b.getHeight()){
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }

        return imagePath;
    }

    public static Bitmap getclip(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth()  + 100 , bitmap.getWidth() );

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getWidth() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    public Bitmap getRoundedShapeBitmap(Bitmap scaleBitmapImage) {

        int targetWidth = scaleBitmapImage.getWidth();
        int targetHeight = scaleBitmapImage.getHeight();
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

 	    /*Path path = new Path();
 	    path.addCircle(((float) targetWidth - 1) / 2,
 	    ((float) targetHeight - 1) / 2,
 	    (Math.min(((float) targetWidth),
 	    ((float) targetHeight)) / 2),
 	    Path.Direction.CCW);
 	    canvas.clipPath(path);
 	    Bitmap sourceBitmap = scaleBitmapImage;
 	    canvas.drawBitmap(sourceBitmap,
 	    new Rect(0, 0, sourceBitmap.getWidth(),
 	    sourceBitmap.getHeight()),
 	    new Rect(0, 0, targetWidth, targetHeight), null);*/

        final Rect rect = new Rect(0, 0, targetWidth, targetHeight);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(targetWidth / 2, targetHeight / 2,
                targetWidth / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaleBitmapImage, rect, rect, paint);
        Bitmap userAvatorBmpImg = Bitmap.createScaledBitmap(targetBitmap, targetWidth/2 + 15, targetHeight/2 + 15 , true);


        return userAvatorBmpImg;

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

    public void updateCreditCardInfo(String lastFour){
        ((TextView)getView().findViewById(R.id.user_profile_card_info_txt)).setText("Credit Card: *"+lastFour);

    }


}
