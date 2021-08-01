package com.beetalab.stickynote.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.Utils.BaseActivity;
import com.beetalab.stickynote.Utils.NotifyUtils;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.config.StickyNoteApplication;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.fragment.NoteFragment;
import com.beetalab.stickynote.fragment.SettingsFragment;
import com.beetalab.stickynote.fragment.TagsFragment;
import com.beetalab.stickynote.fragment.ToDoFragment;
import com.beetalab.stickynote.listener.Commiunicator;
import com.beetalab.stickynote.model.ToDoModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.ref.ReferenceQueue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.beetalab.stickynote.activity.AddTodoActivity.EXTRA_KEY_VIEW_TYPE;
import static com.beetalab.stickynote.config.Constant.DAY_OF_MILLIES;


public class HomeActivity extends BaseActivity implements ToDoFragment.Commuinicator, Commiunicator {

    public static final int REQ_CODE_CREATE_TO_DO = 100;
    public static final int REQ_CODE_CREATE_NOTE = 101;
    public static final int REQ_CODE_CREATATE_TAG = 102;
    public static final int REQ_CODE_UPDATE_TODO = 103;


    public static final String TAG_TODO = "todo_frag";
    public static final String TAG_NOTE = "note_frag";
    public static final String TAG_CATEGORY = "tag_frag";
    public static final String TAG_SETTING ="setting_tag";

    public static final String KEY_IS_NOTIFICATION = "notification";
    public static final String KEY_DATA = "data";

    private int currentFragment = -1;

    private final int FRAGMENT_TODO = 0;
    private final int FRAGMENT_NOTE = 1;
    private final int FRAGMENT_TAG = 2;
    private final int FRAGMENT_SETTING =3;

    private TextView toolbarTitle;
    BottomNavigationView bottomNavigationView;
    private ConstraintLayout parentView;
    private Toolbar toolbar;

    private boolean is_auto_delete_enable;

    private StickyNoteApplication application;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this);

        application = ((StickyNoteApplication)getApplication());

        final SharedPreferences sharedPreference = AppUtils.getSharedPreference(this);
        is_auto_delete_enable = sharedPreference.getBoolean(Constant.SETTING_AUTO_DELETE,true);
        getExtras(getIntent());
        initView();

        currentFragment = FRAGMENT_TODO;
        loadFragment(ToDoFragment.newInstance("",""));
        initNavigation();

        if(is_auto_delete_enable)
            deleteOldData();
    }

    private void getExtras(Intent intent) {

        if(intent.hasExtra(HomeActivity.KEY_IS_NOTIFICATION) && intent.hasExtra(HomeActivity.KEY_DATA)){

           ToDoModel data = (ToDoModel) intent.getSerializableExtra(HomeActivity.KEY_DATA);
           if(data != null){
               showCompleteDialog(data);
           }
        }

    }

    private void showCompleteDialog(ToDoModel data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.complete_dialog,null,false);
        builder.setView(v);

        String s;
        if(data.isIs_complete()) {
            s = String.format(Constant.MSG_INCOMPLETE, data.getTask_title());
        }
        else{
            s = String.format(Constant.MSG_COMPLETE, data.getTask_title());
        }
        ((TextView) v.findViewById(R.id.title_text)).setText(Html.fromHtml(s));

        Dialog d = builder.create();
        d.show();

        v.findViewById(R.id.done_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataBase(data);
                d.dismiss();
            }
        });

    }

    private void updateDataBase(ToDoModel data) {

        new Thread(){
            @Override
            public void run() {
                Database database = Database.getInstance(HomeActivity.this);
                data.setIs_complete(!data.isIs_complete());

                if(data.isIs_complete())
                    NotifyUtils.clearAlarm(HomeActivity.this,data);
                else
                    NotifyUtils.setAlarm(HomeActivity.this,data.getDate_as_long(),data);

                database.getDao().updateTodo(data);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyFragment(REQ_CODE_CREATE_TO_DO);
                    }
                });

            }
        }.start();

    }

    private void initView() {
        parentView = findViewById(R.id.parent_view);
        toolbarTitle = findViewById(R.id.tool_bar_title);
        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        toolbarTitle.setText(R.string.plans);
        makeToolbarTitleCenter(toolbar,toolbarTitle);
        bottomNavigationView = (BottomNavigationView)  findViewById(R.id.main_bottom_navigation);

        setSupportActionBar((Toolbar)findViewById(R.id.main_toolbar));
        getSupportActionBar().setTitle("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_add_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.tool_bar_add){
            showAddActivity(currentFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void showAddActivity(int currentFragment) {

        if(currentFragment == FRAGMENT_TODO){
            Intent intent = new Intent(this,AddTodoActivity.class);
            intent.putExtra(EXTRA_KEY_VIEW_TYPE,AddTodoActivity.VIEW_TYPE_TODO);
            startActivityForResult(intent,REQ_CODE_CREATE_TO_DO);
        }
        else if(currentFragment == FRAGMENT_NOTE){
            Intent intent = new Intent(this,AddNoteActivity.class);
            intent.putExtra(EXTRA_KEY_VIEW_TYPE,AddNoteActivity.VIEW_TYPE_TODO);
            startActivityForResult(intent,REQ_CODE_CREATE_NOTE);
        }

        if(currentFragment == FRAGMENT_TAG){
            Intent intent = new Intent(this, AddTagActivity.class);
            intent.putExtra(EXTRA_KEY_VIEW_TYPE,AddTodoActivity.VIEW_TYPE_TODO);
            startActivityForResult(intent, REQ_CODE_CREATATE_TAG);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case REQ_CODE_CREATE_TO_DO:
                if(resultCode == RESULT_OK){
                    notifyFragment(REQ_CODE_CREATE_TO_DO);
                }
                break;

            case REQ_CODE_CREATE_NOTE:
                if(resultCode == RESULT_OK){
                    notifyFragment(REQ_CODE_CREATE_NOTE);
                }
                break;

            case REQ_CODE_CREATATE_TAG:

                if(resultCode == RESULT_OK){
                    notifyFragment(REQ_CODE_CREATATE_TAG);
                }
                break;
        }

    }

    private void notifyFragment(int reqCodeCreateToDo) {

        if(reqCodeCreateToDo == REQ_CODE_CREATE_TO_DO || reqCodeCreateToDo ==REQ_CODE_UPDATE_TODO){
            FragmentManager fragmentManager = getSupportFragmentManager();
            final ToDoFragment fragmentByTag = (ToDoFragment)fragmentManager.findFragmentByTag(TAG_TODO);

            if(fragmentByTag != null)
                fragmentByTag.setNewAddedData();
        }

        if(reqCodeCreateToDo == REQ_CODE_CREATE_NOTE){
            FragmentManager fragmentManager = getSupportFragmentManager();
            final NoteFragment fragmentByTag = (NoteFragment)fragmentManager.findFragmentByTag(TAG_NOTE);

            if(fragmentByTag != null)
                fragmentByTag.readNotes();
        }

        if(reqCodeCreateToDo == REQ_CODE_CREATATE_TAG){
            FragmentManager fragmentManager = getSupportFragmentManager();
            final TagsFragment fragmentByTag = (TagsFragment)fragmentManager.findFragmentByTag(TAG_CATEGORY);

            if(fragmentByTag != null)
                fragmentByTag.readTags();
        }

    }

    private void initNavigation(){


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                if(item.getItemId() == R.id.menu_todo){
                    if(currentFragment !=FRAGMENT_TODO) {
                        findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);
                        parentView.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.activity_background));

//                        if(currentFragment == FRAGMENT_SETTING && application.isSettingsChanged)
//                            loadAd();

                        currentFragment = FRAGMENT_TODO;
                        toolbarTitle.setText(R.string.plans);
                        loadFragment(ToDoFragment.newInstance("", ""));
                    }
                    return true;
                }
                if(item.getItemId() == R.id.menu_notes){

                    if(currentFragment != FRAGMENT_NOTE) {
                        findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);
                        parentView.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.activity_background));

//                        if(currentFragment == FRAGMENT_SETTING && application.isSettingsChanged)
//                            loadAd();

                        currentFragment = FRAGMENT_NOTE;
                        toolbarTitle.setText("My Notes");
                        loadFragment(NoteFragment.newInstance("", ""));
                    }
                    return true;
                }

                if(item.getItemId() == R.id.menu_reminder){

                    if(currentFragment != FRAGMENT_TAG) {
                        findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);
                        parentView.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));

//                        if(currentFragment == FRAGMENT_SETTING && application.isSettingsChanged)
//                            loadAd();

                        currentFragment = FRAGMENT_TAG;
                        toolbarTitle.setText("My Tags");
                        loadFragment(TagsFragment.newInstance());
                    }
                    return true;
                }

                if(item.getItemId() == R.id.menu_settings) {

                    if (currentFragment != FRAGMENT_SETTING){
                        findViewById(R.id.main_toolbar).setVisibility(View.GONE);
                        parentView.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.white));
                         currentFragment = FRAGMENT_SETTING;
                        toolbarTitle .setText("");
                        loadFragment(new SettingsFragment());
                }
                    return true;
                }
                return false;
            }
        });

    }

    private void loadFragment(Fragment fragment) {



        if(currentFragment == FRAGMENT_TODO){
            getSupportFragmentManager().beginTransaction().add(fragment,TAG_TODO)
                    .replace(R.id.fragment_container,fragment).commit();
        }

        if(currentFragment == FRAGMENT_NOTE){
            getSupportFragmentManager().beginTransaction().add(fragment,TAG_NOTE)
                    .replace(R.id.fragment_container,fragment).commit();
        }

        if(currentFragment == FRAGMENT_TAG){
            getSupportFragmentManager().beginTransaction().add(fragment, TAG_CATEGORY)
                    .replace(R.id.fragment_container,fragment).commit();
        }

        if(currentFragment == FRAGMENT_SETTING){
            getSupportFragmentManager().beginTransaction().add(fragment, TAG_SETTING)
                    .replace(R.id.fragment_container,fragment).commit();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if(fragment instanceof ToDoFragment)
        {
            ((ToDoFragment)fragment).setListener(this);
        }
        else if(fragment instanceof SettingsFragment){
            ((SettingsFragment)fragment).setFarCommiunicator(this);
        }
    }

    @Override
    public void onSend(String day) {

//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
//
//        try {
//            final Date parse = dateFormat.parse(day);
//
//            if(parse == null)
//            {
//                toolbarTitle.setText(R.string.todo_title_list);
//                return;
//            }
//
//            Calendar c = Calendar.getInstance(Locale.getDefault());
//            c.setTime(parse);
//
//            SimpleDateFormat df = new SimpleDateFormat("MMMM");
//            final String format = df.format(c.getTime());
//            toolbarTitle.setText(format);
//            makeToolbarTitleCenter(toolbar,toolbarTitle);
//
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

    }


    private void deleteOldData(){

        new Thread(){

            @Override
            public void run() {

                Database database = Database.getInstance(HomeActivity.this);
                Calendar calendar = Calendar.getInstance(Locale.getDefault());

                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                if(day == 1){
                    final long l = calendar.getTimeInMillis() - (DAY_OF_MILLIES*3);
                    database.getDao().deleteExpriredTodo(l);
                }
            }
        }.start();

    }

    private void loadAd(){

        loadInstratialAds(this);
        application.isSettingsChanged = false;

    }

    @Override
    public void onBackPressed() {

        if(currentFragment != FRAGMENT_TODO) {
            loadFragment(new ToDoFragment());
            bottomNavigationView.setSelectedItemId(R.id.menu_todo);
        }
        else
        {
//            loadInstratialAds(HomeActivity.this);
            finish();
        }

    }

    @Override
    public void oncommiunicate(int what) {

//        if(what == Constant.MSG_PRO_ACTIVATION){
//            createBottomSheet();
//        }

    }

}