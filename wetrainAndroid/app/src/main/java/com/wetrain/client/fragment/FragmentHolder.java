package com.wetrain.client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.wetrain.client.R;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by test on 10/20/15.
 */
public class FragmentHolder {
    private static final String TAG = "FragmentHolder";
    private static final int FRAGMENT_CONTAINER_LAYOUT = R.id.fragment_container;
    private static CopyOnWriteArrayList<String> fragmentList;

    public static final int LEFT_RIGHT_FRAGMENT_ANIM = 1;
    public static final int TOP_BOTTOM_FRAGMENT_ANIM = 2;


    public static enum FragmentTags {
        SplashScreenFragmentTag,
        HomeFragmenttag,
        DurationFragmentTag,
        AccountFragmentTag,
        LoginFragmentTag,
        TutorialFragmentTag,
        CreditsFragmentTag,
        SignUpFragmentTag,
        FirstTimeTutorialTag,
        WorkoutFragmentTag,
        FeedBackFragmentTag,
        TermsServiceFragmentTag,
        UserProfileFragmentTag,
        WorkoutLocationFragmentTag,
        SearchTrainerFragmentTag,
        TrainerScreenFragmentTag,
        WorkoutDescriptionFragmentTag,
        MotivativeMeSearchTrainerFragmentTag,
        MotivateMeFinishingVideoFragmentTag,
        ScheduleDatePickerFragmentTag,
        WorkorderInfoFragment
    }


    public static boolean setFragment(FragmentActivity fragmentActivity, Bundle bundle, FragmentTags fragmentTags, boolean keepStack, boolean revereseAnimation){

        if(fragmentActivity == null) return false;

        if(fragmentList == null){
            fragmentList = new CopyOnWriteArrayList<String>();
        }

        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.name());


        if(!(fragmentTags.equals(FragmentTags.HomeFragmenttag) || fragmentTags.equals(FragmentTags.AccountFragmentTag) || fragmentTags.equals(FragmentTags.SplashScreenFragmentTag) || fragmentTags.equals(FragmentTags.SearchTrainerFragmentTag)) || revereseAnimation) {
            if(revereseAnimation){
                fragmentTransaction.setCustomAnimations(R.anim.left_to_right_exit, R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.right_to_left_enter);

            } else {
                if(getAnimTypeByFragment(fragmentTags) == LEFT_RIGHT_FRAGMENT_ANIM) {
                    fragmentTransaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.right_to_left_enter, R.anim.left_to_right_exit, R.anim.right_to_left_exit);

                }else{
                    fragmentTransaction.setCustomAnimations(R.anim.alert_top_botom_anim, R.anim.empty_anim);

                }

            }
        }


        if (fragmentList.size() > 0) {
            String tag = fragmentList.get(fragmentList.size() - 1);
            Fragment backFragment = fragmentManager.findFragmentByTag(tag);
            fragmentTransaction.hide(backFragment);
        }



        if (fragment == null) {
            switch (fragmentTags) {
                case SplashScreenFragmentTag:
                    fragment = new SplashScreenFragment();
                    break;
                case HomeFragmenttag:
                    fragment =new HomeFragment();
                    break;
                case DurationFragmentTag:
                    fragment = new DurationScreenFragment();
                    break;
                case AccountFragmentTag:
                   fragment = new MyAccountFragment();
                    break;
                case LoginFragmentTag:
                  fragment = new LoginFragment();
                    break;
                case TutorialFragmentTag:
                 fragment = new TutorialScreenFragment();
                    break;
                case FeedBackFragmentTag:
                  fragment = new FeedBackScreen();
                    break;
                case SignUpFragmentTag:
                    fragment = new SignUpScreenFragment();
                    break;
                case FirstTimeTutorialTag:
                    fragment = new TutorialScreenFragment();
                    break;
                case WorkoutFragmentTag:
                    fragment = new WorkoutTypeFragment();
                    break;
                case UserProfileFragmentTag:
                    fragment = new UserProfileInfoFragment();
                    break;
                case WorkoutLocationFragmentTag:
                    fragment = new WorkoutLocationFragment();
                    break;
                case SearchTrainerFragmentTag:
                    fragment = new SearchTrainerFragment();
                    break;
                case TrainerScreenFragmentTag:
                    fragment = new TrainerScreenFragment();
                    break;
                case WorkoutDescriptionFragmentTag:
                    fragment = new WorkoutTypeFragment();
                    break;
                case MotivativeMeSearchTrainerFragmentTag:
                    fragment = new SearchTrainerFragment();
                    break;
                case MotivateMeFinishingVideoFragmentTag:
                    fragment = new MotivateFinishingVideoScreen();
                    break;
                case CreditsFragmentTag:
                    fragment = new UpdateCreditCardFragmant();
                    break;
                case ScheduleDatePickerFragmentTag:
                    fragment = new ScheduleTimePickerFragment();
                    break;
                case WorkorderInfoFragment:
                    fragment = new WorkoutInfoFragment();
                    break;
                case TermsServiceFragmentTag:
                    fragment = new TermsConditionFragment();
                    break;

            }

            Log.d("TEST", fragmentList+" Fragment="+fragmentTransaction+"|"+fragment);

            if (fragment == null) return false;
            if (bundle != null)
                fragment.setArguments(bundle);

            fragmentTransaction.add(FRAGMENT_CONTAINER_LAYOUT, fragment, fragmentTags.name());

            fragmentList.add(fragmentTags.name());

        } else {
            fragmentTransaction.show(fragment);

            //reorder the fragment list
            fragmentList.remove(fragmentTags.name());
            fragmentList.add(fragmentTags.name());

        }


        try {
            //fragmentTransaction.commit();
            fragmentTransaction.commitAllowingStateLoss();

        }catch(Exception e){
            return false;
        }

        return true;

    }

    private static int getAnimTypeByFragment(FragmentTags fragmentTag){
        if(fragmentTag.equals(FragmentTags.LoginFragmentTag) || fragmentTag.equals(FragmentTags.SignUpFragmentTag) ||
                fragmentTag.equals(FragmentTags.UserProfileFragmentTag) || fragmentTag.equals(FragmentTags.CreditsFragmentTag)){
            return TOP_BOTTOM_FRAGMENT_ANIM;
        }

        return LEFT_RIGHT_FRAGMENT_ANIM;
    }

    public static void onBackPressed(FragmentActivity activity){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTags lastFragmentTag = FragmentHolder.getLastFragmentTag();



        if (lastFragmentTag != null) {


            Fragment lastFragment = fragmentManager.findFragmentByTag(lastFragmentTag.name());
            if(lastFragment != null){
                if(lastFragment.getView() != null) {
                    //HIDE SOFT KEY PAD IF SHOWN
                    InputMethodManager imm = (InputMethodManager) lastFragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(lastFragment.getView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            }

            if(activity.findViewById(R.id.keypad_mask_layout) != null){
                activity.findViewById(R.id.keypad_mask_layout).setVisibility(View.GONE);
            }


            if(lastFragmentTag.equals(FragmentHolder.FragmentTags.TrainerScreenFragmentTag)){
                TrainerScreenFragment trainerFragment = (TrainerScreenFragment) fragmentManager.findFragmentByTag(FragmentHolder.FragmentTags.TrainerScreenFragmentTag.name());
                if(trainerFragment != null) {
                    trainerFragment.onActionBarCancelBtnClicked();
                    return;
                }
            }

            if(lastFragmentTag.equals(FragmentTags.HomeFragmenttag) || lastFragmentTag.equals(FragmentTags.AccountFragmentTag)){
                activity.finish();
                //clear fragment list obj
                fragmentList.clear();
                fragmentList = null;
                return;

            } else if(lastFragmentTag.equals(FragmentTags.SplashScreenFragmentTag) || lastFragmentTag.equals(FragmentTags.SearchTrainerFragmentTag)){
                return;

            }

            removeFragment(fragmentManager, lastFragmentTag.name());
            showLastFragment(fragmentManager, getAnimTypeByFragment(lastFragmentTag) == LEFT_RIGHT_FRAGMENT_ANIM);

        }
    }

    public static void removeFragment(FragmentManager fragmentManager, String tag){
        if(fragmentManager == null) return;

        fragmentList.remove(tag);

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if(fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(!tag.equals(FragmentTags.SplashScreenFragmentTag.name())) {
                if(getAnimTypeByFragment(FragmentTags.valueOf(tag)) == LEFT_RIGHT_FRAGMENT_ANIM) {
                    fragmentTransaction.setCustomAnimations(R.anim.left_to_right_exit, R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.right_to_left_enter);
                }else{
                    fragmentTransaction.setCustomAnimations(R.anim.empty_anim, R.anim.alert_bottom_top_anim);
                }
            }
            fragmentTransaction.remove(fragment);
            try {
                //fragmentTransaction.commit();
                fragmentTransaction.commitAllowingStateLoss();
            }catch (Exception e){
            }
        }
    }

    private static void showLastFragment(FragmentManager fragmentManager, boolean animate){
        if(fragmentList.size() > 0) {
            Fragment fragment = fragmentManager.findFragmentByTag(fragmentList.get(fragmentList.size() - 1));
            if(fragment != null){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(animate) {
                    fragmentTransaction.setCustomAnimations(R.anim.left_to_right_exit, R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.right_to_left_enter);
                }

                fragmentTransaction.show(fragment);
                try {
                    //fragmentTransaction.commit();
                    fragmentTransaction.commitAllowingStateLoss();

                }catch(Exception e){
                }
            }
        }
    }

    public static FragmentTags getLastFragmentTag(){
        if(fragmentList != null){
            if(fragmentList.size() > 0){
               return  FragmentTags.valueOf(fragmentList.get(fragmentList.size() - 1));
            }
        }

        return null;
    }

    public static boolean hideTrainTabFragments(FragmentManager fragmentManager){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        if(fragmentList != null){
            if(fragmentList.size() > 0) {
                for (int tra = fragmentList.size() - 1; tra >= 0; tra--) {
                    Fragment fragment = fragmentManager.findFragmentByTag(fragmentList.get(tra));

                    if (isTrainTabFragment(fragment.getTag())) {
                        fragmentTransaction.hide(fragment);

                    }
                }

                CopyOnWriteArrayList<String> fragmentTampList = new CopyOnWriteArrayList<String>();

                for (int acc = 0; acc < fragmentList.size(); acc++) {
                    String fragmentTag = fragmentList.get(acc);
                    if(isAccountTabFragment(fragmentTag)){
                        fragmentTampList.add(fragmentTag);
                    }
                }

                try{
                    //fragmentTransaction.commit();
                    fragmentTransaction.commitAllowingStateLoss();
                }catch(Exception e){

                }


                if(fragmentTampList.isEmpty()){
                    return false;
                }

                fragmentList.removeAll(fragmentTampList);
                fragmentList.addAll(fragmentTampList);

                showLastFragment(fragmentManager, false);
            }
        }

        return true;
    }

    public static boolean hideAccountTabFragments(FragmentManager fragmentManager){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragmentList != null){
            if(fragmentList.size() > 0) {
                for (int acc = fragmentList.size() - 1; acc >= 0; acc--) {
                    Fragment fragment = fragmentManager.findFragmentByTag(fragmentList.get(acc));

                    if (isAccountTabFragment(fragment.getTag())) {
                        fragmentTransaction.hide(fragment);

                    }
                }

                CopyOnWriteArrayList<String> fragmentTampList = new CopyOnWriteArrayList<String>();

                for (int tra = 0; tra < fragmentList.size(); tra++) {
                    String fragmentTag = fragmentList.get(tra);
                    if(isTrainTabFragment(fragmentTag)){
                        fragmentTampList.add(fragmentTag);
                    }
                }

                try{
                    //fragmentTransaction.commit();
                    fragmentTransaction.commitAllowingStateLoss();
                }catch(Exception e){

                }

                if(fragmentTampList.isEmpty()){
                    return false;
                }

                fragmentList.removeAll(fragmentTampList);
                fragmentList.addAll(fragmentTampList);


                showLastFragment(fragmentManager, false);
            }
        }

        return true;
    }

    public static boolean isAccountTabFragment(String fragmentTag){
        if (fragmentTag.equals(FragmentHolder.FragmentTags.AccountFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.UserProfileFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.CreditsFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.TutorialFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.WorkoutDescriptionFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.FeedBackFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.WorkorderInfoFragment.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.TermsServiceFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.LoginFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.SignUpFragmentTag.name())){

            return true;
        }
        return false;
    }


    public static boolean isTrainTabFragment(String fragmentTag){
        if (fragmentTag.equals(FragmentTags.DurationFragmentTag.name()) ||
                fragmentTag.equals(FragmentTags.HomeFragmenttag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.WorkoutFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.WorkoutLocationFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.SearchTrainerFragmentTag.name()) ||
                fragmentTag.equals(FragmentHolder.FragmentTags.MotivativeMeSearchTrainerFragmentTag.name()) ||
                fragmentTag.equals(FragmentTags.ScheduleDatePickerFragmentTag.name()) ||
                fragmentTag.equals(FragmentTags.TrainerScreenFragmentTag.name())) {

            return true;
        }
        return false;
    }

    public static void removeTrainTabFragmants(FragmentManager fragmentManager, FragmentTags exceptFragment, boolean animate){
        if(fragmentManager == null) return;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(animate)
            fragmentTransaction.setCustomAnimations(R.anim.left_to_right_exit, R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.right_to_left_enter);


        if(fragmentList != null) {
            if (fragmentList.size() > 0) {
                CopyOnWriteArrayList<String> fragmentTampList = new CopyOnWriteArrayList<String>();

                for (int tra = fragmentList.size() - 1; tra >= 0; tra--) {
                    String fragmentTag = fragmentList.get(tra);

                    if(isTrainTabFragment(fragmentTag)){
                        if(fragmentTag.equals(exceptFragment.name()) || fragmentTag.equals(FragmentTags.HomeFragmenttag.name())){
                            break;
                        }
                        fragmentTampList.add(fragmentTag);
                        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

                        fragmentTransaction.remove(fragment);

                    }
                }

                Log.d("TEST", "remove fragment list=" + fragmentTampList);

                try{
                    //fragmentTransaction.commit();
                    fragmentTransaction.commitAllowingStateLoss();
                    fragmentList.removeAll(fragmentTampList);
                }catch(Exception e){

                }
            }
        }
    }


    public static void removeAccountTabFragmants(FragmentManager fragmentManager, FragmentTags exceptFragment, boolean animate){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(animate)
            fragmentTransaction.setCustomAnimations(R.anim.left_to_right_exit, R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.right_to_left_enter);


        if(fragmentList != null) {
            if (fragmentList.size() > 0) {
                CopyOnWriteArrayList<String> fragmentTampList = new CopyOnWriteArrayList<String>();

                for (int tra = fragmentList.size() - 1; tra >= 0; tra--) {
                    String fragmentTag = fragmentList.get(tra);

                    if(isAccountTabFragment(fragmentTag)){
                        if(fragmentTag.equals(exceptFragment.name())){
                            break;
                        }

                        fragmentTampList.add(fragmentTag);
                        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

                        fragmentTransaction.remove(fragment);

                    }

                }

                try{
                    //fragmentTransaction.commit();
                    fragmentTransaction.commitAllowingStateLoss();
                    fragmentList.removeAll(fragmentTampList);
                }catch(Exception e){

                }

            }
        }
    }

}
