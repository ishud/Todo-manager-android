package com.beetalab.stickynote.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.Utils.BaseActivity;
import com.beetalab.stickynote.Utils.NotifyUtils;
import com.beetalab.stickynote.adapter.TagAdapterHorizontal;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.TagModel;
import com.beetalab.stickynote.model.ToDoModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddTodoActivity extends BaseActivity implements TagAdapterHorizontal.OnItemClick{

    public static final int VIEW_TYPE_TODO = 0;
    public static final int VIEW_TYPE_TODO_EDIT = 1;

    public static final String EXTRA_KEY_VIEW_TYPE = "view_type";
    public static final String EXTRA_KEY_DATA = "data";


    private Toolbar toolbar;
    private TextView toolbarTitle,dateTxt;
    private EditText edtTitle,edtDescription;
    private RecyclerView tagList;
    private Button btnCreate;
    private SwitchCompat notifySwitch;

    private TagAdapterHorizontal adapter;

    private TagModel selectedModel;
    private String tag_id_by_selected = null;
    private String taskDate;
    private long taskDateAsLong;
    private long today;
    String dateOfSelect = "";
    private String quaryDate;

    private boolean IS_24_HOUR_SELECT = false;
    private SharedPreferences mPref;

    private ToDoModel model;
    private boolean isEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        mPref = AppUtils.getSharedPreference(this);
        IS_24_HOUR_SELECT = mPref.getBoolean(Constant.SETTING_KEY_TIME_24_ENABLE,false);
        getExtras(getIntent());
        initView();
        setTagList();
    }

    private void getExtras(Intent intent) {

        if(intent.hasExtra(EXTRA_KEY_VIEW_TYPE) && intent.hasExtra(EXTRA_KEY_DATA)){
            isEdit =  intent.getIntExtra(EXTRA_KEY_VIEW_TYPE,0) == VIEW_TYPE_TODO_EDIT;
            model = (ToDoModel) intent.getSerializableExtra(EXTRA_KEY_DATA);
        }

    }

    private void setTagList() {

        new Thread(){
            @Override
            public void run() {
                Database database = Database.getInstance(AddTodoActivity.this);
                final List<TagModel> allTags = database.getDao().getAllTags();

              runOnUiThread(() -> setTagAdapter(allTags,model));

            }
        }.start();

    }

    private void setShowData(){

        edtTitle.setText(model.getTask_title());
        edtDescription.setText(model.getDescription());

        dateTxt.setText(model.getTask_date());
        notifySwitch.setChecked(model.isNotify());

    }

    private void setTagAdapter(List<TagModel> allTags,ToDoModel model) {
        int pos  = -1;
        if(model != null){

            for(int i =0;i<allTags.size(); i++){
                if(allTags.get(i).getTag_id().equals(model.getTag_id())){
                    pos = i;
                    break;
                }
            }
        }

        adapter = new TagAdapterHorizontal(this,allTags,this, pos);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        tagList.setLayoutManager(manager);
        tagList.setAdapter(adapter);

        if(isEdit)
        {
            selectedModel = allTags.get(pos);
            tag_id_by_selected = selectedModel.getTag_id();
            manager.scrollToPosition(pos);
        }

        //dataList.addAll(tagModels);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {

        toolbar = findViewById(R.id.add_task_toolbar);
        toolbarTitle = findViewById(R.id.add_task_title_txt);
        dateTxt = findViewById(R.id.date_time_txt);
        edtDescription = findViewById(R.id.description_edt);
        edtTitle = findViewById(R.id.title_edt);
        tagList = findViewById(R.id.tags_list);
        btnCreate = findViewById(R.id.btn_create);
        notifySwitch = findViewById(R.id.notify_switch);

        makeToolbarTitleCenter(toolbar,toolbarTitle);

            if(!isEdit){

                toolbarTitle.setText(getString(R.string.new_task));
                btnCreate.setText(getString(R.string.create));

                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                final Date day = calendar.getTime();
                today = day.getTime();
                final String stringDate = AppUtils.formateDate(day);
                quaryDate = AppUtils.getformateDate(calendar.getTime(),"MM_dd_yyyy");
                dateTxt.setText(stringDate);
                taskDate = stringDate;

            }
            else{
                taskDate = model.getTask_date();
                taskDateAsLong = model.getDate_as_long();

                toolbarTitle.setText(getString(R.string.view_task));
                btnCreate.setText(getString(R.string.update));
                today = model.getDate_as_long();
                Calendar c = Calendar.getInstance(Locale.getDefault());
                c.setTimeInMillis(model.getDate_as_long());

                setShowData();

                quaryDate = model.getQuary_date();
                dateTxt.setText(model.getTask_date());

                enableEditMode(false);
            }



        edtTitle.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        edtDescription.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> finish());

        setTextWatters();

        btnCreate.setOnClickListener(view -> {

            if(isEdit)
                editTodo();
            else
                createNewTodo();

        });

        dateTxt.setOnClickListener(view -> showDateTimePicker());

    }

    private void setTextWatters(){

        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtTitle.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtDescription.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void createNewTodo() {

        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if(title.equals("")){
            edtTitle.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(this,R.drawable.ic_baseline_error_outline_24),null);
           return;
        }

        if(description.equals("")){
            edtDescription.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(this,R.drawable.ic_baseline_error_outline_24),null);
            return;
        }

        if(selectedModel == null){
            Toast.makeText(this, "Please Select Tag", Toast.LENGTH_SHORT).show();
            return;
        }

        final long dl = AppUtils.getDateasLong(taskDate, "EE dd MMM yyyy hh : mm a");

        Calendar c = Calendar.getInstance(TimeZone.getDefault());

        if(dl < c.getTimeInMillis()){
            Toast.makeText(this, "Please Select correct date", Toast.LENGTH_SHORT).show();
            return;
        }

        saveDataTodb(title,description,notifySwitch.isChecked());

    }

    private void updateDatabase(String title, String description, boolean checked) {

        new Thread(){

            @Override
            public void run() {
                if(taskDateAsLong == 0){
                    taskDateAsLong =  AppUtils.getDateasLong(taskDate,"EE dd MMM yyyy hh : mm a");
                }

                SimpleDateFormat format = new SimpleDateFormat("EE dd MMM yyyy hh : mm a");
                try {
                    final Date parse = format.parse(taskDate);
                    if(parse != null){

                        quaryDate = AppUtils.getformateDate(parse,"MM_dd_yyyy");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Database database = Database.getInstance(AddTodoActivity.this);

                ToDoModel doModel = new ToDoModel();
                model.setDescription(description);
                model.setTag_color(selectedModel.getTag_color());
                model.setTag_id(selectedModel.getTag_id());
                model.setNotify(checked);
                model.setTask_date(taskDate);
                model.setDate_as_long(taskDateAsLong);
                model.setTask_title(title);
                model.setTag_name(selectedModel.getTag_name());
                model.setQuary_date(quaryDate);

                database.getDao().updateTodo(model);


                if(!selectedModel.getTag_id().equals(tag_id_by_selected)){
                    final TagModel tagById = database.getDao().getTagById(tag_id_by_selected);
                    if(tagById.getTask_count() > 0) {
                        tagById.setTask_count(tagById.getTask_count() - 1);
                        database.getDao().updateTag(tagById);
                    }

                    setTagTask(selectedModel);
                }

                setAlarm(doModel);

                runOnUiThread(() -> {
                    setResult(RESULT_OK);
                    finish();
                });

            }
        }.start();
    }

    private void saveDataTodb(String title, String description, boolean checked) {

        new Thread(){

            @Override
            public void run() {

                if(taskDateAsLong == 0){
                  taskDateAsLong =  AppUtils.getDateasLong(taskDate,"EE dd MMM yyyy hh : mm a");
                }

                SimpleDateFormat format = new SimpleDateFormat("EE dd MMM yyyy hh : mm a");
                try {
                    final Date parse = format.parse(taskDate);
                    if(parse != null){

                        quaryDate = AppUtils.getformateDate(parse,"MM_dd_yyyy");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Database database = Database.getInstance(AddTodoActivity.this);

                ToDoModel model = new ToDoModel();
                model.setDescription(description);
                model.setTag_color(selectedModel.getTag_color());
                model.setTag_id(selectedModel.getTag_id());
                model.setNotify(checked);
                model.setCreatedDate(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                model.setIs_complete(false);
                model.setTask_date(taskDate);
                model.setDate_as_long(taskDateAsLong);
                model.setTask_title(title);
                model.setTag_name(selectedModel.getTag_name());

                model.setQuary_date(quaryDate);

                database.getDao().insertNewTodo(model);
                setTagTask(selectedModel);

                setAlarm(model);

                runOnUiThread(() -> {
                    setResult(RESULT_OK);
                    finish();
                });

            }
        }.start();

    }

    private void setAlarm(ToDoModel model) {

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(model.getDate_as_long());

        Calendar calendar1 = Calendar.getInstance(Locale.getDefault());

        if(calendar.getTime().after(calendar1.getTime())){
            NotifyUtils.setAlarm(this,model.getDate_as_long(),model);
        }

    }

    private void setTagTask(TagModel selectedModel) {
      Thread thread =  new Thread(){

            @Override
            public void run() {

                Database database = Database.getInstance(AddTodoActivity.this);
                final TagModel tagById = database.getDao().getTagById(selectedModel.getTag_id());

                if(tagById != null){

                    tagById.setTask_count(tagById.getTask_count()+1);
                    database.getDao().updateTag(tagById);
                }

            }
        };

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.start();
    }

    private void showDateTimePicker(){

        Calendar calendar = Calendar.getInstance(Locale.getDefault());


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                dateOfSelect = i +" "+ (i1 +1) +" " + i2;
                quaryDate = i+"_"+(i1 +1)+"_"+i2;
                showTimePicker(dateOfSelect,calendar);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));

        datePickerDialog.show();

    }

    private void showTimePicker( String datePicker, Calendar calendar) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                dateOfSelect += " " + i + " : " + i1;
                taskDate = AppUtils.getDateStringFormStringSource(dateOfSelect);
                dateTxt.setText(taskDate);

            }
        },calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),IS_24_HOUR_SELECT);

        timePickerDialog.show();
    }

    @Override
    public void onItemSelect(int pos, TagModel data) {
        selectedModel = data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(isEdit) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
            return true;
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menu_edit){
            toolbarTitle.setText(R.string.edit_todo);
            enableEditMode(true);
            adapter.setEditPos();
        }

        return super.onOptionsItemSelected(item);
    }

    private void enableEditMode(boolean active){
        btnCreate.setVisibility(active? View.VISIBLE : View.INVISIBLE);
        edtDescription.setEnabled(active);
        edtTitle.setEnabled(active);
        notifySwitch.setEnabled(active);
        dateTxt.setEnabled(active);
    }

    private void editTodo(){

        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if(title.equals("")){
            edtTitle.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(this,R.drawable.ic_baseline_error_outline_24),null);
            return;
        }

        if(description.equals("")){
            edtDescription.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(this,R.drawable.ic_baseline_error_outline_24),null);
            return;
        }

        if(selectedModel == null){
            Toast.makeText(this, "Please Select Tag", Toast.LENGTH_SHORT).show();
            return;
        }

        final long dl = AppUtils.getDateasLong(taskDate, "EE dd MMM yyyy hh : mm a");

        Calendar c = Calendar.getInstance(TimeZone.getDefault());

        if(dl < c.getTimeInMillis()){
            Toast.makeText(this, "Please Select correct date", Toast.LENGTH_SHORT).show();
            return;
        }

        updateDatabase(title,description,notifySwitch.isChecked());

    }
}