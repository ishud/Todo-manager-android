package com.beetalab.stickynote.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.Utils.BaseActivity;
import com.beetalab.stickynote.Utils.NotifyUtils;
import com.beetalab.stickynote.adapter.GroupTodoAdapter;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.AdView;
import com.beetalab.stickynote.model.BaseModel;
import com.beetalab.stickynote.model.GroupTodo;
import com.beetalab.stickynote.model.TagModel;
import com.beetalab.stickynote.model.ToDoModel;
import com.beetalab.stickynote.service.NotifyUserservice;
import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static com.beetalab.stickynote.config.Constant.KEY_EXTRA_DATA;

public class ToDoWithTagActivity extends BaseActivity implements GroupTodoAdapter.ItemLongClickListener
    ,GroupTodoAdapter.ItemClickListener{

    private static final int REQ_CODE_EDIT_TODO = 1001;


    private Toolbar toolbar;
    private RecyclerView listView;
    private TextView title;

    private TagModel tagModel;
    private ProgressBar progressBar;
    private GroupTodoAdapter adapter;

    private boolean is_pro;

    private List<Object> dataList = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        getExtras(getIntent());
        initView();

        is_pro = AppUtils.getSharedPreference(this).getBoolean(Constant.PREF_KEY_IS_PRO_USER,false);

        if(tagModel != null){
            title.setText(tagModel.getTag_name());
            makeToolbarTitleCenter(toolbar,title);
            setData();
        }
        else
            finish();

    }

    private void setData() {

      new Thread(){

          @SuppressLint("NotifyDataSetChanged")
          @Override
          public void run() {
              dataList.clear();
              Database database = Database.getInstance(ToDoWithTagActivity.this);
              final List<ToDoModel> todoByTagId = database.getDao().getTodoByTagId(tagModel.getTag_id());
              if (todoByTagId.size() == 0) {
                  runOnUiThread(() -> {
                      progressBar.setVisibility(View.GONE);
                      if(adapter != null)
                         adapter.notifyDataSetChanged();
                      findViewById(R.id.no_item_found).setVisibility(View.VISIBLE);});
              }
              else{

                  final List<String> allQuaryDate = database.getDao().getAllQuaryDate(tagModel.getTag_id(),
                          Calendar.getInstance(Locale.getDefault()).getTimeInMillis());

                  Calendar calendar = Calendar.getInstance(Locale.getDefault());
                  Set<String> uniqDates = new HashSet<>();
                  List<Object> tempData = new ArrayList<>();

                  for(String val : allQuaryDate){
                      final boolean add = uniqDates.add(val);
                  }

                  for(int i = 0; i<todoByTagId.size(); i++){
                      final ToDoModel model = todoByTagId.get(i);
                      if(model.getDate_as_long() < calendar.getTimeInMillis())
                          todoByTagId.remove(model);
                  }


                  final Object[] objects = uniqDates.toArray();

//                  for(int index =objects.length-1; index >= 0; index--){
//                        String val1 = (String) objects[index];
//                      tempData.add(new GroupTodo(val1));
//
//                      for(int i = 0; i<todoByTagId.size(); i++){
//
//                          final ToDoModel model = todoByTagId.get(i);
//                          if(model.getDate_as_long() < calendar.getTimeInMillis())
//                              continue;
//
//                          if(val1.equals(model.getQuary_date())){
//                              tempData.add(model);
//                          }
//                      }
//                  }

                  for(int index =0; index < objects.length; index++){
                      String val1 = (String) objects[index];
                      tempData.add(new GroupTodo(val1));

                      for(int i = 0; i<todoByTagId.size(); i++){

                          final ToDoModel model = todoByTagId.get(i);
                          if(model.getDate_as_long() < calendar.getTimeInMillis())
                              continue;

                          if(val1.equals(model.getQuary_date())){
                              tempData.add(model);
                          }
                      }
                  }
                  dataList.clear();
                  dataList.addAll(tempData);

                  if(!is_pro)
                      dataList.add(0,new AdView());

                    runOnUiThread(() -> setAdapter(dataList));
              }

          }
      }.start();

    }

    private void setAdapter(List adapterDta) {

        progressBar.setVisibility(View.GONE);

        if(adapterDta.size() < 2)
        {
            findViewById(R.id.no_item_found).setVisibility(View.VISIBLE);
            return;
        }

        adapter = new GroupTodoAdapter(this,adapterDta,this,this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        listView.setLayoutManager(manager);
        listView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(listView);
    }

    private void getExtras(Intent intent) {

        if(intent.hasExtra(KEY_EXTRA_DATA)){
            tagModel = (TagModel) intent.getSerializableExtra(KEY_EXTRA_DATA);
        }
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        listView = findViewById(R.id.list_view);
        progressBar = findViewById(R.id.progress_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this,R.drawable.ic_baseline_arrow_back_24));

        toolbar.setNavigationOnClickListener(item -> finish());

    }

    private void showCompleteDialog(ToDoModel data,int pos) {

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

                data.setIs_complete(!data.isIs_complete());
                ((ToDoModel)dataList.get(pos)).setIs_complete(data.isIs_complete());
                adapter.notifyDataSetChanged();

                updateDataBase(data);
                d.dismiss();
            }
        });

    }

    private void updateDataBase(ToDoModel data) {

        new Thread(){
            @Override
            public void run() {
                Database database = Database.getInstance(ToDoWithTagActivity.this);
                if(data.isIs_complete())
                    NotifyUtils.clearAlarm(ToDoWithTagActivity.this,data);
                else
                    NotifyUtils.setAlarm(ToDoWithTagActivity.this,data.getDate_as_long(),data);

                database.getDao().updateTodo(data);

            }
        }.start();

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove( RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder,  RecyclerView.ViewHolder target) {
            final Object o = dataList.get(viewHolder.getAdapterPosition());
            return o instanceof AdView;
        }

        @Override
        public void onSwiped( RecyclerView.ViewHolder viewHolder, int direction) {
            final Object o = dataList.get(viewHolder.getAdapterPosition());

            if(o != null && o instanceof ToDoModel)
                deleteTodo((ToDoModel)o);
            else{
                dataList.remove((AdView)o);
                adapter.notifyItemRemoved(0);
            }
        }

        @Override
        public void onChildDraw( Canvas c,  RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteTodo(ToDoModel data)  {

        Thread t = new Thread(){

            @Override
            public void run() {

                Database db = Database.getInstance(ToDoWithTagActivity.this);
                db.getDao().deleteToDo(data);
                final TagModel tagById = db.getDao().getTagById(data.getTag_id());

                if(tagById != null && tagById.getTask_count() > 0){
                    tagById.setTask_count(tagById.getTask_count() -1);
                    db.getDao().updateTag(tagById);
                }

                NotifyUtils.clearAlarm(ToDoWithTagActivity.this,data);
                setData();
            }
        };

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE_EDIT_TODO && resultCode == RESULT_OK){
            setData();
        }

    }

    @Override
    public void onLongClick(ToDoModel data,int pos) {
        showCompleteDialog(data,pos);
    }

    @Override
    public void onItemClick(ToDoModel data) {
        if(data != null && !data.isIs_complete()){
            Intent intent = new Intent(ToDoWithTagActivity.this, AddTodoActivity.class);
            intent.putExtra(AddTodoActivity.EXTRA_KEY_VIEW_TYPE,AddTodoActivity.VIEW_TYPE_TODO_EDIT);
            intent.putExtra(AddTodoActivity.EXTRA_KEY_DATA,data);
            startActivityForResult(intent,REQ_CODE_EDIT_TODO);

        }
    }
}