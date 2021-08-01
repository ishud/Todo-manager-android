package com.beetalab.stickynote.config;

import android.app.Application;

public class StickyNoteApplication extends Application {

    public boolean isAdShowed = false;
    public boolean isSettingsChanged = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
