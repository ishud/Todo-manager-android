package com.beetalab.stickynote.config;

public class Constant {

    //db tabels
    public static final String TABLE_NAME_TO_DO = "tbl_to_do";
    public static final String TABLE_NAME_NOTE ="tbl_note";
    public static final String TABLE_NAME_TAGS = "tbl_tags";


    //notification channel
    public static final String NOTIFICATION_CHANNEL_ALARM = "channel_alarm";

    //prefrence key
    public static final String PREF_KEY_IS_FRIST_TIME = "frist_time";


    //date pattern
    public static final String DATE_PATTERN_MEDIUM = "EE dd MMM yyyy hh : mm a";

    //intent key
    public static final String KEY_EXTRA_DATA = "data";

    //setting pref key
    public static final String SETTING_KEY_BUBBLE_ENABLE = "bubble_enable";
    public static final String SETTING_KEY_TIME_24_ENABLE ="time_24_enable";
    public static final String SETTING_KEY_WEEK_DAY ="week_first_day";
    public static final String SETTING_ENABLE_APP_LOCK ="app_lock_enable";
    public static final String SETTING_KEY_NOTIFICATION_VIBRATION_ENABLE = "vib_enable";
    public static final String SETTING_NOTIFICATION_PRORITY ="not_priority";
    public static final String SETTING_AUTO_DELETE = "auto_delete";


    //weak first day
    public static final String DAY_MONDAY = "monday";
    public static final String DAY_SUNDAY ="sunday";

    //html msg
    public static final String MSG_COMPLETE = "<p>Mark this <b>%s</b> as completed task?</p>";
    public static final String MSG_INCOMPLETE = "<p>Mark this <b>%s</b> as uncompleted task?</p>";

    //req code
    public static final int REQ_CODE_BUBLE_TODO = 1002;

    public static final long DAY_OF_MILLIES =  86400000;

    public static final String CONTENT_ABOUT ="<Html><center> <h5 color=#000>BeetaLab</h5> </center></Html>";
    public static final String CONTENT_HELP ="<html>  <ul>\n" +
            "       <li><b> New Task</b></li>\n" +
            "       Press on plus mark and enter your planing, date time, note and select one tag from tag list then tap on remind me and craete the task.\n" +
            "       <br/><br/>\n" +
            "       \n" +
            "       <li><b> Complete Task</b></li>\n" +
            "       Long press on the task and mark as complete.\n" +
            "       <br/><br/>\n" +
            "       \n" +
            "       <li><b> Delete Task</b></li>\n" +
            "       Swipe to right the task.\n" +
            "       <br/><br/>\n" +
            "       \n" +
            "       <li><b> New Tag</b></li>\n" +
            "       Go to my tags and press on plus mark then add a new tag.\n" +
            "       <br/><br/>\n" +
            "       \n" +
            "       <li><b> New Note</b></li>\n" +
            "       Go to my notes and press on plus mark then add a new note and save it.\n" +
            "       <br/><br/>\n" +
            "       \n" +
            "       <li><b> Settings</b></li>\n" +
            "\n" +
            "\t<ul>\n" +
            "\n" +
            "\t<li> If you want to notify task as notification check Show notification switch.</li>\n" +
            "\t</lul>\n" +
            "      <br/>\n" +
            "      <li> If you want to use vibration and sound check the Use vibration and sound switch.</li><br/>\n" +
            "      <li> If you want to see 24 hour time format check the Use 24 hour time switch.</li><br/>\n" +
            "       <li>If you want to change first day of the week you can tap on the First day of week and check sunday or monday.</li><br/>\n" +
            "      <li> If you want to secure your app with the lock check the Enable app lock switch.</li><br/>\n" +
            "      <li> If you want to auto delete your data check Auto delete data switch.</li>\n" +
            "       <br/><br/>\n" +
            "   </ul>\n" +
            "       </html>";

    public static final int MSG_PRO_ACTIVATION = 202;

    public static final String PRODUCT_UPGRADE_PREMIUM = "upgrade_premium";

    public static final String PREF_KEY_IS_PRO_USER = "pro_user";
    public static final String ADMOB_ID= "ca-app-pub-7465387162062313~5564459621";
    public static final String ADMOB_INASTRATIAL_ID ="ca-app-pub-7465387162062313/9312132947";
    public static final String ADMOB_NATIVE_ID = "ca-app-pub-7465387162062313/6615840934";

}
