package com.beetalab.stickynote.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.Utils.TagUtils;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.TagModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class StartActiviy extends AppCompatActivity {

    private SharedPreferences mPref;
    private boolean appLockEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_activiy);

        // Glide.with(this).load(R.drawable.new_todo_icon).into((ImageView) findViewById(R.id.icon_image));
//
//        mPref = AppUtils.getSharedPreference(this);
//        appLockEnable = mPref.getBoolean(Constant.SETTING_ENABLE_APP_LOCK,false);
//
//        final SharedPreferences sharedPreference = AppUtils.getSharedPreference(this);
//
//        if(sharedPreference.getBoolean(Constant.PREF_KEY_IS_FRIST_TIME,true)){
//            saveTagsTodb();
//            sharedPreference.edit().putBoolean(Constant.PREF_KEY_IS_FRIST_TIME,false).apply();
//            startActivity(new Intent(this,HomeActivity.class));
//            finish();
//            return;
//        }
//
//        if(appLockEnable)
//            showLock();
//        else {
//            updateTaskCount();
//            startActivity(new Intent(this, HomeActivity.class));
//            finish();
//        }
    }

    private void showLock() {

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                updateTaskCount();
                startActivity(new Intent(StartActiviy.this,HomeActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                updateTaskCount();
                startActivity(new Intent(StartActiviy.this,HomeActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(StartActiviy.this, "Authentication failed", Toast.LENGTH_LONG).show();
            }
        });


        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sticky Note")
                .setSubtitle("Authenticate Access Application")
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void saveTagsTodb()  {
        Thread t =   new Thread(){
            @Override
            public void run() {
                Database db = Database.getInstance(StartActiviy.this);
                db.getDao().insertTag(TagUtils.tags);
            }
        };

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.start();
    }

    private void updateTaskCount()  {

        Thread t = new Thread(){

            @Override
            public void run() {

                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                Database db = Database.getInstance(StartActiviy.this);
                final List<TagModel> allTags = db.getDao().getAllTags();

                for(TagModel tag : allTags){

                    final int taskCount = db.getDao().getTaskCount(calendar.getTimeInMillis(), tag.getTag_id());
                    tag.setTask_count(taskCount);
                    db.getDao().updateTag(tag);
                }
            }
        };
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.start();

    }

}