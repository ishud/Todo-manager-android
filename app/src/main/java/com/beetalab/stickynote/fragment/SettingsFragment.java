package com.beetalab.stickynote.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.beetalab.stickynote.BuildConfig;
import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.activity.AboutActivity;
import com.beetalab.stickynote.activity.DetailsActivity;
import com.beetalab.stickynote.activity.PremiumActivity;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.config.StickyNoteApplication;
import com.beetalab.stickynote.listener.Commiunicator;

import static com.beetalab.stickynote.config.Constant.MSG_PRO_ACTIVATION;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences mPref;
    private FragmentActivity activity;

    private String DAY_OF_FRIST;
    private int priority;
    private StickyNoteApplication application;
    private boolean is_pro;

    private Commiunicator farCommiunicator;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        activity = getActivity();
        initVariable();

        application = ((StickyNoteApplication)activity.getApplication());

        setProLabel();
        setFristDayWeek();
        setNotificationPriority();

        handleProClick();
        handleAppVerionClick();
        handleBubbleSwitch();
        handleTimeSwitch();
        handleWeekDayclick();
        handleEnableAppLock();
        handleHelpClick();
        handleAboutClick();
        handleNotificationPrority();
        setAutoDelete();
    }

    private void setProLabel() {

        getPreferenceManager().findPreference("pro_key").setVisible(!is_pro);
    }

    public void setFarCommiunicator(Commiunicator commiunicator){
        this.farCommiunicator = commiunicator;
    }

    private void handleNotificationPrority() {

        ListPreference listPreference = (ListPreference)getPreferenceManager().findPreference("notification_priority");

        if(listPreference == null)
            return;

        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            application.isSettingsChanged = true;
            if(newValue.equals("1")){
                mPref.edit().putInt(Constant.SETTING_NOTIFICATION_PRORITY,1).apply();
                preference.setSummary("Default");
                preference.setDefaultValue(1);

            }
            else if(newValue.equals("2")) {
                mPref.edit().putInt(Constant.SETTING_NOTIFICATION_PRORITY, 2).apply();
                preference.setSummary("High");
                preference.setDefaultValue(2);
            }
            else
            {
                mPref.edit().putInt(Constant.SETTING_NOTIFICATION_PRORITY,3).apply();
                preference.setSummary("Low");
                preference.setDefaultValue(3);

            }

            return true;
        });
    }

    private void setNotificationPriority() {
        ListPreference listPreference = (ListPreference)getPreferenceManager().findPreference("notification_priority");
        if(listPreference != null){
            switch (priority){
                case 1: listPreference.setSummary("Default");break;
                case 2: listPreference.setSummary("High");break;
                case 3: listPreference.setSummary("Low");break;
            }
        }
    }

    private void setFristDayWeek() {
        ListPreference listPreference = (ListPreference)getPreferenceManager().findPreference("week_start_date_key");
        if(listPreference != null)
            listPreference.setSummary(DAY_OF_FRIST.equals(Constant.DAY_MONDAY) ? Constant.DAY_MONDAY:Constant.DAY_SUNDAY);
    }

    private void handleAboutClick() {

        getPreferenceManager().findPreference("about_key").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(activity, AboutActivity.class);
               // intent.putExtra(DetailsActivity.CONTENT_TYPE,0);
                startActivity(intent);
                return true;
            }
        });



    }

    private void handleHelpClick() {

        getPreferenceManager().findPreference("help_key").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(activity, DetailsActivity.class);
                intent.putExtra(DetailsActivity.CONTENT_TYPE,1);
                startActivity(intent);
                return true;
            }
        });


    }

    private void handleEnableAppLock() {
        SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat)getPreferenceManager().findPreference("app_lock_key");
        if(switchPreferenceCompat == null)
            return;

        switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mPref.edit().putBoolean(Constant.SETTING_ENABLE_APP_LOCK,(boolean)newValue).apply();
                application.isSettingsChanged = true;

                return true;
            }
        });
    }

    private void handleWeekDayclick() {

        ListPreference listPreference = (ListPreference)getPreferenceManager().findPreference("week_start_date_key");

        if(listPreference == null)
            return;
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                application.isSettingsChanged = true;
                if(newValue.equals("1")){
                    mPref.edit().putString(Constant.SETTING_KEY_WEEK_DAY,Constant.DAY_MONDAY).apply();
                    preference.setSummary("Monday");
                    preference.setDefaultValue(1);

                }
                else {
                    mPref.edit().putString(Constant.SETTING_KEY_WEEK_DAY, Constant.DAY_SUNDAY).apply();
                    preference.setSummary("Sunday");
                    preference.setDefaultValue(2);
                }

                return true;
            }
        });

    }

    private void handleTimeSwitch() {
        SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat)getPreferenceManager().findPreference("time_use_24");

        if(switchPreferenceCompat == null)
            return;

        switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                application.isSettingsChanged = true;
                mPref.edit().putBoolean(Constant.SETTING_KEY_TIME_24_ENABLE,(boolean)newValue ).apply();
                return true;
            }
        });
    }

    private void handleBubbleSwitch() {

        SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat)getPreferenceManager().findPreference("notification_bubble_key");

        if(switchPreferenceCompat == null)
            return;

        switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                application.isSettingsChanged = true;
                mPref.edit().putBoolean(Constant.SETTING_KEY_BUBBLE_ENABLE,(boolean)newValue ).apply();
                return true;
            }
        });

    }

    private void handleAppVerionClick() {

        getPreferenceManager().findPreference("verion_key").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final String versionName = BuildConfig.VERSION_NAME;
                Toast.makeText(activity, versionName, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    private void handleProClick() {
        getPreferenceManager().findPreference("pro_key").setOnPreferenceClickListener(preference -> {

            Intent intent = new Intent(activity,PremiumActivity.class);
            startActivityForResult(intent,PremiumActivity.REQ_CODE_UPGRADE_PREMIUM);
            activity.overridePendingTransition(0,0);

//            if (farCommiunicator != null)
//                farCommiunicator.oncommiunicate(MSG_PRO_ACTIVATION);

            return true;
        });

    }


    private void setAutoDelete(){
        SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat)getPreferenceManager().findPreference("auto_delete");

        if(switchPreferenceCompat == null)
            return;

        switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                application.isSettingsChanged = true;
                mPref.edit().putBoolean(Constant.SETTING_AUTO_DELETE,(boolean)newValue ).apply();
                return true;
            }
        });

    }

    @Override
    public void onAttach( Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    private void initVariable() {
        mPref = AppUtils.getSharedPreference(activity);
        DAY_OF_FRIST = mPref.getString(Constant.SETTING_KEY_WEEK_DAY,Constant.DAY_MONDAY);
        priority = mPref.getInt(Constant.SETTING_NOTIFICATION_PRORITY,2);
        is_pro = mPref.getBoolean(Constant.PREF_KEY_IS_PRO_USER,false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PremiumActivity.REQ_CODE_UPGRADE_PREMIUM && resultCode == Activity.RESULT_OK){
            getPreferenceManager().findPreference("pro_key").setVisible(false);
        }
    }
}