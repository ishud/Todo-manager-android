package com.beetalab.stickynote.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.activity.AddTodoActivity;
import com.beetalab.stickynote.activity.HomeActivity;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.ToDoModel;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.beetalab.stickynote.activity.HomeActivity.KEY_DATA;
import static com.beetalab.stickynote.activity.HomeActivity.KEY_IS_NOTIFICATION;
import static com.beetalab.stickynote.config.Constant.DAY_OF_MILLIES;
import static com.beetalab.stickynote.config.Constant.NOTIFICATION_CHANNEL_ALARM;
import static com.beetalab.stickynote.config.Constant.REQ_CODE_BUBLE_TODO;

public class NotifyUserservice extends BroadcastReceiver {

    public static final int EVENT_TYPE_TODO = 0;

    public static final String KEY_INTENT_DATA = "intent_data";
    public static final String KEY_NOTIFY_EVENT = "event";

    private SharedPreferences mPref;
    private boolean isBubleEnable;
    private boolean isSoundEnable;
    private int prority;
    private int chanelPrority;

    private  Context context;

    @Override
    public void onReceive(Context context, Intent intent) {


        this.context = context;
        setVariables();
        final int event = intent.getExtras().getInt(KEY_NOTIFY_EVENT);

        switch (event){
            case EVENT_TYPE_TODO:
                if(isBubleEnable)
                    notifyToDo(intent.getExtras().getString(KEY_INTENT_DATA));
                break;
        }


    }
    

    private void setVariables(){
        mPref = AppUtils.getSharedPreference(context);
        isBubleEnable = mPref.getBoolean(Constant.SETTING_KEY_BUBBLE_ENABLE,true);
        isSoundEnable = mPref.getBoolean(Constant.SETTING_KEY_NOTIFICATION_VIBRATION_ENABLE,true);
        prority = mPref.getInt(Constant.SETTING_NOTIFICATION_PRORITY,2);



    }

    private void notifyToDo(String string) {

        final ToDoModel model = new Gson().fromJson(string, ToDoModel.class);

        createNotificationChanel(model);

        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(KEY_IS_NOTIFICATION,true);
        intent.putExtra(KEY_DATA,model);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.notification_layout);

        ;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),getIcon(model));
        remoteViews.setImageViewBitmap(R.id.notification_image,bitmap);
        remoteViews.setTextViewText(R.id.notification_title,model.getTask_title());
        remoteViews.setTextViewText(R.id.notification_description,model.getDescription());


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ALARM);
     //   builder.setContent(remoteViews);
        builder.setContentTitle(model.getTask_title());
        builder.setContentText(model.getDescription());
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        if(prority == 1)
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        else if(prority == 3){
            builder.setPriority(NotificationCompat.PRIORITY_LOW);
        }
        else
            builder.setPriority(NotificationCompat.PRIORITY_MAX);


        if(isSoundEnable)
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);


        builder.setSmallIcon(getIcon(model));

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

    private void createNotificationChanel(ToDoModel model){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ALARM,KEY_NOTIFY_EVENT,NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(model.getDescription());
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);

           NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

    }

    private int getIcon(ToDoModel model){
        switch (model.getTag_id()){
            case "tag_1" :
                return R.drawable.image_work;
            case "tag_2" :
                     return R.drawable.icon_learning;
            case "tag_3" :
                   return R.drawable.image_shpoing;
            case "tag_4" :
               return R.drawable.image_art;
            case "tag_5" :
                    return R.drawable.image_movie;
            case "tag_6" :
                return  R.drawable.image_meeting;
            case "tag_7" :
                   return R.drawable.image_dating;
            case "tag_8" :
                   return R.drawable.image_sports;
            case "tag_9" :
                       return R.drawable.image_fitness;
            case "tag_10" :
                       return R.drawable.image_dining;
            case "tag_11" :
                       return R.drawable.image_helh;
            default:   return R.drawable.image_cutom_tag;
        }
    }

}
