package com.wetrain.client.customviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wetrain.client.R;
import com.wetrain.client.Utills;


/**
 * Created by test on 10/19/15.
 */
public class CustomActionBar extends FrameLayout{

    public static final String TitleType="Title";
    public static final String NavigationType="Back";
    public static final String OptionType="Option";

    /*
     * Custom Action Bar Options Clock Listener
     */
    public interface ActionBarOptionsButtonClickListener {
        void onOptionsButtonClicked();
    }

    private ActionBarOptionsButtonClickListener actionBarBtnClickListener;
    public void setOptionsButtonClickListener(ActionBarOptionsButtonClickListener buttonClickListener){
        actionBarBtnClickListener = buttonClickListener;
    }


    /*
     * Custom Action bar NAvigation button Click Listener
     */
    public interface ActionBarNavigationButtonClickListener{
        void onNavigationBackButtonClicked();
    }
    private ActionBarNavigationButtonClickListener navigationBtnClickListener;
    public void setNavigationClickListener(ActionBarNavigationButtonClickListener buttonClickListener){
        navigationBtnClickListener = buttonClickListener;
    }


    /*
     * NAVIGATION
     */
    public static final int ACTION_BAR_NAV_TYPE_BACK = 1;
    public static final int ACTION_BAR_NAV_TYPE_CLOSE = 2;

    /*
     * OPTIONS
     */
    public static final int ACTION_BAR_NAVIGATION_TYPE_BLUE_BACK = 1;
    public static final int ACTION_BAR_NAVIGATION_TYPE_WHITE_BACK = 2;
    public static final int ACTION_BAR_NAVIGATION_TYPE_NONE= 3;


    private TextView titleTxt;
    private TextView optionsButtonTxt;
    private TextView backButtonTxt;
    private FrameLayout actionbarMainLyt;
    private View notificationView;
    private TextView notificationTextLabel;
    public CustomActionBar(Context context) {
        super(context);
    }

    public CustomActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleTxt = (TextView) findViewById(R.id.actionbar_title_txt);
        optionsButtonTxt = (TextView) findViewById(R.id.actionbar_options_btn);
        backButtonTxt = (TextView)findViewById(R.id.actionbar_back_btn);
        actionbarMainLyt = (FrameLayout) findViewById(R.id.action_bar_lyt);

        notificationView = findViewById(R.id.avail_trainer_count_lyt);
        notificationTextLabel = (TextView) findViewById(R.id.avail_trainer_count_txt_label);

        backButtonTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utills.hideSoftKeyboard(v);
                if (navigationBtnClickListener != null) {
                    navigationBtnClickListener.onNavigationBackButtonClicked();
                }
            }
        });

        optionsButtonTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utills.hideSoftKeyboard(v);
                if (actionBarBtnClickListener != null) {
                    actionBarBtnClickListener.onOptionsButtonClicked();
                }
            }
        });
    }


    public void setTitle(CharSequence txt){
        titleTxt.setText(txt);
        showNotificationView(View.GONE);
    }


     public void setActionBarBgColor(int res){
        actionbarMainLyt.setBackgroundResource(res);
        notificationView.setBackgroundResource(res);
        notificationTextLabel.setBackgroundResource(res);
    }

    public void setTextColor(int res , String titletype){
        if(titletype.equals(TitleType)) {
            titleTxt.setTextColor(res);
        }else if(titletype.equals(NavigationType)){
            backButtonTxt.setTextColor(res);
        }else if(titletype.equals(OptionType)){
            ColorStateList color = new ColorStateList( new int[][]{
                    new int[]{android.R.attr.state_enabled},
                    new int[]{}},
                    //color list
                    new int[] {res, R.color.edittext_hint_clr});
            optionsButtonTxt.setTextColor(color);

            setOptionButtonEnable(true);
        }
    }


    //ColorStateList optionTestColor = new ColorStateList();


    public void setNavigationText(CharSequence txt){
        backButtonTxt.setText(txt);
    }
    public void setNavigationTextVisibility(int visible){
        backButtonTxt.setVisibility(visible);
    }


    public void setOptionButtonVisibility(int visibility){
        optionsButtonTxt.setVisibility(visibility);
    }

    public void setOptionButtonLabel(String optionstxt){
        optionsButtonTxt.setVisibility(VISIBLE);
        optionsButtonTxt.setText(optionstxt);
    }

    public void setOptionButtonEnable(boolean enable){
        optionsButtonTxt.setEnabled(enable);
    }

    public void setNavigationType(int navigationType){
        if(navigationType == ACTION_BAR_NAVIGATION_TYPE_BLUE_BACK){
            backButtonTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.blue_arrow), null, null, null);

        }else if(navigationType == ACTION_BAR_NAVIGATION_TYPE_WHITE_BACK){
            backButtonTxt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.white_arrow), null, null, null);

        }else if(navigationType == ACTION_BAR_NAVIGATION_TYPE_NONE){
            backButtonTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        }
    }



    public void showNotificationView(int visibility){
        notificationView.setVisibility(visibility);
    }

    public void setNotificationTitleVisibility(int visibility){
        notificationTextLabel.setVisibility(visibility);
    }

    public void setNotificationTitleTxt(String notificationTxt){
        notificationTextLabel.setVisibility(VISIBLE);
        notificationTextLabel.setText(notificationTxt);

    }

}
