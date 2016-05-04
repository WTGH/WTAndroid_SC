package com.wetrain.client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wetrain.client.Constants;
import com.wetrain.client.R;
import com.wetrain.client.Utills;
import com.wetrain.client.activity.MainActivity;
import com.wetrain.client.customviews.CustomActionBar;

import java.util.Collections;

/**
 * Created by Administrator on 12/28/15.
 */
public class WorkoutTypeFragment extends WeTrainBaseFragment {


    private View lastSelectedOption;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workout_screen, container, false);

        return  view;
    }



    class WorkOutAdapter extends BaseAdapter{
        private LayoutInflater mLayoutInflater;
        int width ;
        int height;
        public WorkOutAdapter() {
            mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Collections.shuffle(Constants.WORKOUT_TITLES);

            width = getResources().getDisplayMetrics().widthPixels;
            height = getResources().getDisplayMetrics().heightPixels;
            width /=  2;
            float topBarHeight = (((AppCompatActivity)getActivity()).getSupportActionBar().getHeight());
            float bottomBarHeight = getResources().getDimension(R.dimen.bottom_tab_height);

            height -=  (Utills.getStatusBarHeight(getActivity()) + (int) topBarHeight + (int) bottomBarHeight);
            height /= 3;


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
                v = mLayoutInflater.inflate(R.layout.workout_screen_adapter_view, null);
                holder = new ViewHolder();

                holder.workoutTitle = (TextView) v.findViewById(R.id.workout_type_header_txt);
                holder.workoutSubTitle = (TextView) v.findViewById(R.id.workout_type_sub_txt);
                holder.workoutIcon = (ImageView) v.findViewById(R.id.workout_type_img);

                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, height);
                v.setLayoutParams(lp);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();

            }


            holder.workoutTitle.setText("" + Constants.WORKOUT_TITLES.get(position));
            holder.workoutSubTitle.setText("" + Constants.GET_WORKOUT_SUB_TITLE(Constants.WORKOUT_TITLES.get(position)));
            holder.workoutIcon.setImageResource(Constants.GET_WORKOUT_DRAWABLE_ICON(Constants.WORKOUT_TITLES.get(position)));
            /*Glide.with(WorkoutTypeFragment.this).load(Constants.GET_WORKOUT_DRAWABLE_ICON(Constants.WORKOUT_TITLES.get(position)))
                    .crossFade()
                    .fitCenter()
                    .override(50, 50)
                    .into(holder.workoutIcon);*/
            v.setId(position);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //To avoid double click
                    if(!canTriggerClickEvent()) return;

                    if (getTag().equals(FragmentHolder.FragmentTags.WorkoutFragmentTag.name())) {
                        WorkoutLocationFragment.WORKOUT_TYPE = v.getId();
                        FragmentHolder.setFragment(getActivity(), null, FragmentHolder.FragmentTags.WorkoutLocationFragmentTag, false, false);

                    } else if(getTag().equals(FragmentHolder.FragmentTags.WorkoutDescriptionFragmentTag.name())) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(WorkoutInfoFragment.BUNDLE_WORKOUT_TYPE_KEY, v.getId());
                        FragmentHolder.setFragment(getActivity(), bundle, FragmentHolder.FragmentTags.WorkorderInfoFragment, false, false);

                    }
                }
            });

            return v;
        }

        public class ViewHolder {
            TextView workoutTitle;
            TextView workoutSubTitle;
            ImageView workoutIcon;

        }
    }


    @Override
    protected void onFragmentResumed() {

        ((MainActivity) getActivity()).setStatusBarColor(android.R.color.black);
        ((MainActivity)getActivity()).findViewById(R.id.bottom_bar_lyt).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setTitle("Select Workout");
        ((MainActivity)getActivity()).setActionBarBgColor(android.R.color.black);
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.TitleType);
        ((MainActivity)getActivity()).setNavigationTextVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setNavigationText("Back");
        ((MainActivity)getActivity()).setTextColor(getResources().getColor(android.R.color.white), CustomActionBar.NavigationType);
        ((MainActivity) getActivity()).setNavigationType(CustomActionBar.ACTION_BAR_NAVIGATION_TYPE_WHITE_BACK);
        ((MainActivity)getActivity()).setOptionButtonVisibility(View.GONE);

        if(getTag().equals(FragmentHolder.FragmentTags.WorkoutDescriptionFragmentTag.name())) {
            ((MainActivity)getActivity()).setTitle("Workout Description");

        }

        ((GridView)getView().findViewById(R.id.workout_listview)).setAdapter(new WorkOutAdapter());

    }


}
