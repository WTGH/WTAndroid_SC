package com.wetrain.client.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;


/**
 * Created by Administrator on 12/24/15.
 */
public class TutorialScreenFragment extends WeTrainBaseFragment implements CustomActionBar.ActionBarOptionsButtonClickListener {

    private LinearLayout pageIndicatorBtnLayout;
    private int currentSelectedPage;
    private ViewPager myPager;
    private TextView optiontextview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTag().equals(FragmentHolder.FragmentTags.FirstTimeTutorialTag.name())) {
            ((MainActivity) getActivity()).setActionBarVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tutorials_screen, container, false);

        myPager = (ViewPager) view.findViewById(R.id.wetrain_pager);
        pageIndicatorBtnLayout = (LinearLayout) view.findViewById(R.id.home_screen_page_indicator_btn_layout);

        optiontextview = (TextView)getActivity().findViewById(R.id.actionbar_options_btn);



        ((MainActivity) getActivity()).setActionbarButtonClickListener(this);

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                loadUi(view);
            }
        });

        ((MainActivity) getActivity()).setOptionButtonVisibility(View.GONE);

        return view;
    }


    private void loadUi(final View view) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), textArray);


        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);
       /* if (getActivity() != null) {
            optiontextview.setEnabled(false);
            optiontextview.setClickable(false);
        }*/


        myPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                resetPageIndicatorBtn(position);

                /*if (position == 6) {
                    optiontextview.setEnabled(true);
                    optiontextview.setClickable(true);
                    ((MainActivity) getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), Constants.OptionType);


                } else {
                    optiontextview.setEnabled(false);
                    optiontextview.setClickable(false);
                    ((MainActivity) getActivity()).setTextColor(getResources().getColor(R.color.top_bar_disable_txt_clr), Constants.OptionType);

                }*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


     /*   if(getIntent()!=null) {
            if (getIntent().getStringExtra("SCREENNAME").equals("viewtutorial")) {
                findViewById(R.id.actionbar_navigation_bar_txtview).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                    }
                });
            }
        }
        else {

            findViewById(R.id.actionbar_navigation_bar_txtview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }*/


 /*       findViewById(R.id.actionbar_navigation_bar_txtview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(getIntent()!=null) {
                        if (getIntent().getStringExtra("SCREENNAME").equals("viewtutorial")) {

                            finish();

                        } else {
                            Intent intent = new Intent(getApplicationContext(), DurationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

            }
        });*/

    }

    private void resetPageIndicatorBtn(int pageIndex) {

        Button pageIndicatorBtn = (Button) pageIndicatorBtnLayout.getChildAt(currentSelectedPage);
        if (pageIndicatorBtn != null)
            pageIndicatorBtn.setSelected(false);

        currentSelectedPage = pageIndex;

        pageIndicatorBtn = (Button) pageIndicatorBtnLayout.getChildAt(currentSelectedPage);

        if (pageIndicatorBtn != null)
            pageIndicatorBtn.setSelected(true);

    }

    private int textArray[] = {R.string.step1, R.string.step2,
            R.string.step3, R.string.step4,
            R.string.step5, R.string.step6,
            R.string.step7};

    @Override
    protected void onFragmentResumed() {


        ((MainActivity) getActivity()).setOptionsButtonLabel("Train Now");
        ((MainActivity) getActivity()).setOptionButtonVisibility(View.GONE);

        ((MainActivity) getActivity()).setStatusBarColor(R.color.account_top_bar_clr);
        if (getTag().equals(FragmentHolder.FragmentTags.FirstTimeTutorialTag.name())) {
            ((MainActivity) getActivity()).setTitle("");
            ((MainActivity) getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_NONE);
            ((MainActivity) getActivity()).setNavigationTextVisibility(View.GONE);
            ((MainActivity) getActivity()).setActionBarBgColor(android.R.color.white);

        }  else if (getTag().equals(FragmentHolder.FragmentTags.TutorialFragmentTag.name())) {
            ((MainActivity) getActivity()).setTitle("");
            ((MainActivity) getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_BLUE_BACK);
            ((MainActivity) getActivity()).setNavigationTextVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).setNavigationText("My Account");
            ((MainActivity) getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.NavigationType);
            ((MainActivity) getActivity()).setActionBarBgColor(R.color.account_top_bar_clr);
            ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.black), CustomActionBar.TitleType);
            ((MainActivity) getActivity()).setTextColor(getResources().getColor(R.color.top_bar_text_clr), CustomActionBar.OptionType);

        }


        checkExistWorikoutStatus();



    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void checkExistWorikoutStatus(){
        //check exist user workout session
        if(ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().getParseObject("client") != null) {
                final ParseObject clientParseObj = ParseUser.getCurrentUser().getParseObject("client");
                if(clientParseObj != null) {
                    ((MainActivity) getActivity()).showProgressDialog();

                    clientParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            final ParseObject workoutParseObj = clientParseObj.getParseObject("workout");
                            if (workoutParseObj != null) {
                                workoutParseObj.fetchInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        ((MainActivity) getActivity()).closeProgerssDialog();
                                        if(workoutParseObj.containsKey("status")) {
                                            if (workoutParseObj.get("status") != null) {
                                                String status = workoutParseObj.containsKey("status") ? workoutParseObj.getString("status") : "";

                                                if (status.equals(Constants.WORKOUT_REQUEST_STATE.Matched.toString()) || status.equals(Constants.WORKOUT_REQUEST_STATE.Searching.toString())) {
                                                    ((MainActivity) getActivity()).setOptionButtonVisibility(View.GONE);

                                                }else{
                                                    ((MainActivity) getActivity()).setOptionButtonVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }
                                });
                            }else{
                                ((MainActivity) getActivity()).closeProgerssDialog();

                            }
                        }
                    });
                }


            }
        } else {
            ((MainActivity) getActivity()).setOptionButtonVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onOptionsButtonClicked() {

        FragmentHolder.removeAccountTabFragmants(getFragmentManager(), FragmentHolder.FragmentTags.AccountFragmentTag, false);
        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.HomeFragmenttag, false, false);
        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.DurationFragmentTag, false, false);


       /* if (getTag().equals(FragmentHolder.FragmentTags.FirstTimeTutorialTag.name())) {

            FragmentHolder.setFragment(getActivity(),null, FragmentHolder.FragmentTags.HomeFragmenttag,false, false);

        } else if (getTag().equals(FragmentHolder.FragmentTags.TutorialFragmentTag.name())) {

            getActivity().onBackPressed();
        }*/

    }


    class ViewPagerAdapter extends PagerAdapter {


        Activity activity;
        int textArray[];
        private int totalPage = 0;

        public ViewPagerAdapter(Activity act, int[] txtarray) {
            textArray = txtarray;
            activity = act;
            totalPage = getTotalPageCount();
            myPager.setOffscreenPageLimit(totalPage);

        }

        @Override
        public Object instantiateItem(View container, int position) {
            TextView view = new TextView(activity);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT));
            view.setGravity(Gravity.CENTER);
            view.setPadding(25, 0, 25, 0);
            view.setText(textArray[position]);
            view.setTextColor(getResources().getColor(android.R.color.black));
            view.setTextSize(18);
            ((ViewPager) container).addView(view, 0);


            return view;

        }

        private int getTotalPageCount() {
            int pageCount = 7;
            setPageIndicatorButton(pageCount);//show page Indicator
            return pageCount;
        }


        @Override
        public int getCount() {
            return textArray.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }


        private void setPageIndicatorButton(int pageSize) {

            Log.d("HOME_SCREEN", ">>>>>>>>>>" + pageSize + "<<<<<<<<<<");


            pageIndicatorBtnLayout.removeAllViews();

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.home_screen_page_indicator_view_size), getResources().getDimensionPixelOffset(R.dimen.home_screen_page_indicator_view_size));


            for (int i = 0; i < pageSize; i++) {

                Log.v("CHECK", "BYN");

                Button pageIndicatorBtn = new Button(getActivity());
                pageIndicatorBtn.setBackgroundResource(R.drawable.options_view_pager_indicator);

                if (i % 2 == 0) {
                    lp.setMargins(0, 0, 20, 0);
                }

                pageIndicatorBtn.setId(i);
                pageIndicatorBtn.setLayoutParams(lp);

                if (i == 0)
                    pageIndicatorBtn.setSelected(true);

                pageIndicatorBtnLayout.addView(pageIndicatorBtn);

            }

        }


    }
}
