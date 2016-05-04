package com.wetrain.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.wetrain.client.Utills;

/**
 * Created by test on 2/16/16.
 */
public class ParsePushMsgReceiver extends ParsePushBroadcastReceiver{
    public static final String APP_FROM_TRAINER_ACCEPT_PUSH_MSG = "App_From_Trainer_Accept_PushMessage";


    @Override
    protected void onPushReceive(Context context, Intent intent) {
        try {
            String channel = intent.getExtras().getString("com.parse.Channel");
            Utills.addEditScheduleDataInPref(context, channel, System.currentTimeMillis());

        }catch (Exception e){
            Log.d("ParsePushMsgReceiver", "Error"+e.getMessage());
        }
        super.onPushReceive(context, intent);

    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        try {
            String channel = intent.getExtras().getString("com.parse.Channel");
            Utills.removeScheduleDataInPref(context, channel);

        }catch (Exception e){
            Log.d("ParsePushMsgReceiver", "Error"+e.getMessage());
        }
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        //set custom intent action when open notification
        Bundle intentExtras = intent.getExtras();
        intent = AlarmReceiver.getNotificationIntent(context, null);
        intent.putExtra(APP_FROM_TRAINER_ACCEPT_PUSH_MSG, true);
        intent.putExtras(intentExtras);

        context.startActivity(intent);

        //super.onPushOpen(context, intent);

    }

}
