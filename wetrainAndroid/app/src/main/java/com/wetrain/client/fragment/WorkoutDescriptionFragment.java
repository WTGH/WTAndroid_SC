package com.wetrain.client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

import java.util.Collections;
import java.util.Random;

/**
 * Created by Administrator on 1/11/16.
 */
public class WorkoutDescriptionFragment extends WeTrainBaseFragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.workoutdescription_screen,null);


        ((GridView)view.findViewById(R.id.workout_descriptions_gridview)).setAdapter(new WorkOutAdapter());

        ((GridView)view.findViewById(R.id.workout_descriptions_gridview)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.WorkoutLocationFragmentTag, false, false);
            }
        });


        ((LinearLayout)view.findViewById(R.id.down_arrow_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((GridView)view.findViewById(R.id.workout_descriptions_gridview)).smoothScrollByOffset(3);

            }
        });
        return view;

    }

    @Override
    protected void onFragmentResumed() {


        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setTitle("Our Workouts");
        ((MainActivity)getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.TitleType);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);
        ((MainActivity)getActivity()).setOptionsButtonLabel("Train Now");
        ((MainActivity)getActivity()).setNavigationText("Back");
        ((MainActivity) getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.NavigationType);
        ((MainActivity) getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_WHITE_BACK);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.OptionType);



        ((GridView)getView().findViewById(R.id.workout_descriptions_gridview)).setAdapter(new WorkOutAdapter());




    }






    class WorkOutAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;


        public WorkOutAdapter() {
            Random random = new Random(Constants.WORKOUT_TITLES.size());
            Collections.shuffle(Constants.WORKOUT_TITLES, random);

            mLayoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Constants.WORKOUT_TITLES.size();
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

                v = mLayoutInflater.inflate(R.layout.workoutdescription_screen_adapter, null);
                holder = new ViewHolder();

                holder.workout_type = (TextView) v.findViewById(R.id.workout_type_header_txt);
                holder.workout_subtype = (TextView) v.findViewById(R.id.contentdescription_txt);
                holder.workoutimg = (ImageView) v.findViewById(R.id.workout_type_img);

                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }


            holder.workout_type.setText("" + Constants.WORKOUT_TITLES.get(position).toString());
            holder.workout_subtype.setText("" + Constants.GET_WORKOUT_DESC(Constants.WORKOUT_TITLES.get(position)));
            holder.workoutimg.setImageResource( Constants.GET_WORKOUT_DRAWABLE_ICON(Constants.WORKOUT_TITLES.get(position)));
            holder.workoutimg.setAlpha(45);


            holder.workout_subtype.setMovementMethod(new ScrollingMovementMethod());

            final ViewHolder finalHolder = holder;
            View.OnTouchListener aListener = new View.OnTouchListener() {
                long startTouchTime = 0;

                @Override
                public boolean onTouch(View v,
                                       MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.e("ACTION_DOWN",
                                    "ACTION_DOWN, intercept true");
                            startTouchTime = (System
                                    .currentTimeMillis());
                            // To scroll the textview set this
                            finalHolder.workout_subtype
                                    .setMovementMethod(new ScrollingMovementMethod());
                            v.getParent().requestDisallowInterceptTouchEvent(
                                            true);
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.e("ACTION_UP",
                                    "Call onitemclick"
                                            + ((System
                                            .currentTimeMillis()) - startTouchTime));

                            if ((System.currentTimeMillis())
                                    - startTouchTime < 200)
                                // To call gridview onItemClick here

                            break;
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        Log.e("ACTION_MOVE",
                                "ACTION_MOVE, intercept true");

                        v.getParent()
                                .requestDisallowInterceptTouchEvent(
                                        true);
                    } else {
                        Log.e("Other", "Intercept false");
                        v.getParent()
                                .requestDisallowInterceptTouchEvent(
                                        true);

                    }
                    return false;
                }
            };

            holder.workout_subtype.setOnTouchListener(aListener);


            return v;
        }
        public class ViewHolder {

            TextView workout_type, workout_subtype;
            ImageView workoutimg;

        }
    }
}
