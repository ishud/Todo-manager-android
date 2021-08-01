package com.beetalab.stickynote.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.beetalab.stickynote.model.ToDoModel;
import com.beetalab.stickynote.service.NotifyUserservice;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class NotifyUtils {

    public static void setAlarm(Context context, long time, ToDoModel data){

        final long currentTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        if(time <= currentTime)
            return;

        String gson = new Gson().toJson(data);

        Intent alaIntent = new Intent(context, NotifyUserservice.class);
        alaIntent.putExtra(NotifyUserservice.KEY_INTENT_DATA,gson);
        alaIntent.putExtra(NotifyUserservice.KEY_NOTIFY_EVENT,NotifyUserservice.EVENT_TYPE_TODO);

        PendingIntent intent = PendingIntent.getBroadcast(context,
                (int) data.getCreatedDate(),alaIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC,time,intent);
    }

    public static void clearAlarm(Context context,ToDoModel data){
        String gson = new Gson().toJson(data);

        Intent alaIntent = new Intent(context, NotifyUserservice.class);
        alaIntent.putExtra(NotifyUserservice.KEY_INTENT_DATA,gson);
        alaIntent.putExtra(NotifyUserservice.KEY_NOTIFY_EVENT,NotifyUserservice.EVENT_TYPE_TODO);

        PendingIntent intent = PendingIntent.getBroadcast(context, (int) data.getCreatedDate(),alaIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(intent);
    }

}
