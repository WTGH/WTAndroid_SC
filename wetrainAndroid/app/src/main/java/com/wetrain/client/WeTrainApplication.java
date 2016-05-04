package com.wetrain.client;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;

/**
 * Created by test on 1/20/16.
 */
public class WeTrainApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);


        /*
         * Facebook
         */
        //FacebookSdk.sdkInitialize(getApplicationContext());

        ParseFacebookUtils.initialize(this);


    }
}
