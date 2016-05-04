package com.wetrain.client.activity;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wetrain.client.R;

import java.util.List;

/**
 * Created by test on 2/2/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String INTENT_SCHEDULE_ALERT = "intent_schedule_alert_notification";
    public static int SCHEDULE_NOTIFICATION_TOKEN;
    public static final String INTENT_FROM_NOTIFICATION = "IntentFromScheduleNotification";

    /*public static final String SCHEDULE_WORKOUT_TYPE = "ScheduleWorkoutType";
    public static final String SCHEDULE_WORKOUT_LENGTH = "ScheduleWorkoutLength";
    public static final String SCHEDULE_WORKOUT_SCHEDULE_TIME = "ScheduleWorkoutScheculeTime";*/
    public static final String SCHEDULE_ALARM_REQUEST_CODE = "ScheduleAlarmRequestCode";
    public static final String SCHEDULE_PARSE_OBJCT_ID = "ScheduleParseObjectId";



    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            Bundle alarmBundle = intent.getExtras();
            int alarmCode= alarmBundle.getInt(SCHEDULE_ALARM_REQUEST_CODE, SCHEDULE_NOTIFICATION_TOKEN++);
            Log.d("TEST", alarmBundle.toString() + "\n AlarmReceiver data=" + alarmCode);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);




            PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmCode, getNotificationIntent(context, alarmBundle), PendingIntent.FLAG_CANCEL_CURRENT );

            //Set notification information:
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setContentTitle("WeTrain")
                    .setTicker("Hey! It's almost time to workout")
                    .setContentText("Hey! It's almost time to workout")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(true)
                    .setAutoCancel(true);

            //Send the notification:
            Notification notification = notificationBuilder.build();
            notificationManager.notify(alarmCode, notification);


            if(isApplicationInForeground(context)) {
                Intent alertIntent = getNotificationIntent(context, alarmBundle);
                alertIntent.putExtra(INTENT_SCHEDULE_ALERT, true);
                context.startActivity(alertIntent);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Intent getNotificationIntent(Context context, Bundle alarmBundle){
        Intent notificationIntent = new Intent(context, MainActivity.class);
        /*alarmBundle.putBoolean(INTENT_FROM_NOTIFICATION, true);
        notificationIntent.putExtras(alarmBundle);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);*/

        if(alarmBundle != null) {
            alarmBundle.putBoolean(INTENT_FROM_NOTIFICATION, true);
            notificationIntent.putExtras(alarmBundle);
        }
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_TASK_ON_HOME);

        return notificationIntent;
    }


    public boolean isApplicationInForeground(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(!pm.isScreenOn()){
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

}
