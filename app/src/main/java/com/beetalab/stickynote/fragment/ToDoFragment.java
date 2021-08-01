package com.beetalab.stickynote.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.Utils.NotifyUtils;
import com.beetalab.stickynote.activity.AddTodoActivity;
import com.beetalab.stickynote.adapter.DateListAdapter;
import com.beetalab.stickynote.adapter.TodoListAdapter;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.AdView;
import com.beetalab.stickynote.model.BaseModel;
import com.beetalab.stickynote.model.TagModel;
import com.beetalab.stickynote.model.ToDoModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ToDoFragment extends Fragment implements TodoListAdapter.OnListItemClick,TodoListAdapter.OnItemLongClick {

    public static final int REQ_CODE_EDIT_TODO = 1001;

    private Commuinicator listener;

    private View view;
    private RecyclerView toDoListView;
    private FragmentActivity activity;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TodoListAdapter adapter;

    //data
    List<BaseModel> dataList = new ArrayList<>();
    private String today = null;

    private SharedPreferences mPref;

    private String FRIST_DAY;

    private boolean is_pro;

    public ToDoFragment() {
        // Required empty public constructor
    }

    public void setListener(Commuinicator listener) {
        this.listener = listener;
    }

    public static ToDoFragment newInstance(String param1, String param2) {
        ToDoFragment fragment = new ToDoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        activity = getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_to_do, container, false);
         toDoListView = view.findViewById(R.id.to_do_list_view);
          activity = getActivity();

        initSettings();
        setDateList();
        readTodoList(today);
        return view;
    }

    private void initSettings() {
        mPref = AppUtils.getSharedPreference(activity);
        is_pro = mPref.getBoolean(Constant.PREF_KEY_IS_PRO_USER,false);
        FRIST_DAY = mPref.getString(Constant.SETTING_KEY_WEEK_DAY,Constant.DAY_MONDAY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_EDIT_TODO:
                if(resultCode == Activity.RESULT_OK){
                    readTodoList(today);
                }

                break;
        }

    }

    private void setDateList(){
        DateFormat format = new SimpleDateFormat("MM_dd_yyyy");
        Calendar calendar = Calendar.getInstance();

        if(FRIST_DAY.equals(Constant.DAY_MONDAY)) {
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }
        else {
            calendar.setFirstDayOfWeek(Calendar.SUNDAY);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        }

        String[] days = new String[7];
        for (int i = 0; i < 7; i++)
        {
            days[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar calendar1 = Calendar.getInstance();
        int d = calendar1.get(Calendar.DAY_OF_MONTH);

        DateFormat format1 = new SimpleDateFormat("dd");
        final String format2 = format1.format(calendar.getTime());
        int index = 0;

        String checkDate = String.valueOf(d);

        if(d<10){
           checkDate = "0"+checkDate;
        }

        for(int i = 0; i < days.length; i++){
           if( days[i].split("_")[1].equals(checkDate)){
               index = i;
               today = days[i];
               break;
            }
        }


        DateListAdapter adapter = new DateListAdapter(days,activity,index, FRIST_DAY);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        adapter.setPos(index);
        GridLayoutManager layoutManager = new GridLayoutManager(activity,7,RecyclerView.VERTICAL,false);
        final RecyclerView viewById = view.findViewById(R.id.date_list);
        viewById.setLayoutManager(layoutManager);
        viewById.setAdapter(adapter);
        adapter.setListener(new DateListAdapter.OnClickListener() {
            @Override
            public void onClick(String day) {
              listener.onSend(day);
               dataList.clear();
                readTodoList(day);
                today = day;
            }
        });
    }

    private void setToDoListViewAdapter(){

        adapter = new TodoListAdapter(activity, dataList,ToDoFragment.this, ToDoFragment.this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(activity,RecyclerView.VERTICAL,false);
        toDoListView.setLayoutManager(manager);
        toDoListView.setAdapter(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(toDoListView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove( RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder,  RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped( RecyclerView.ViewHolder viewHolder, int direction) {
            final BaseModel baseModel = dataList.get(viewHolder.getAdapterPosition());
            if(baseModel instanceof ToDoModel){
                final ToDoModel model = (ToDoModel)baseModel ;
                dataList.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
                deleteTodo(model);

                if(dataList.size() == 0)
                    view.findViewById(R.id.no_item_found).setVisibility(View.VISIBLE);
                else if(dataList.size() == 1 && dataList.get(0) instanceof AdView){
                    dataList.clear();
                    adapter.notifyDataSetChanged();
                    view.findViewById(R.id.no_item_found).setVisibility(View.VISIBLE);
                }
            }
            else{
                dataList.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onChildDraw( Canvas c,  RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteTodo(ToDoModel model) {

        new Thread(){

            @Override
            public void run() {

                Database db = Database.getInstance(activity);

                final TagModel tagById = db.getDao().getTagById(model.getTag_id());

                if(tagById.getTask_count() > 0)
                {
                    tagById.setTask_count(tagById.getTask_count()-1);
                    db.getDao().updateTag(tagById);
                    db.getDao().deleteToDo(model);
                }


            }
        }.start();
    }



    @Override
    public void onClick(ToDoModel data, int position) {

        if(data != null && !data.isIs_complete()){
            Intent intent = new Intent(activity, AddTodoActivity.class);
            intent.putExtra(AddTodoActivity.EXTRA_KEY_VIEW_TYPE,AddTodoActivity.VIEW_TYPE_TODO_EDIT);
            intent.putExtra(AddTodoActivity.EXTRA_KEY_DATA,data);
            startActivityForResult(intent,REQ_CODE_EDIT_TODO);

        }

    }

    private void showCompleteDialog(ToDoModel data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View v = LayoutInflater.from(activity).inflate(R.layout.complete_dialog,null,false);
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
                Database database = Database.getInstance(activity);
                data.setIs_complete(!data.isIs_complete());

                if(data.isIs_complete())
                    NotifyUtils.clearAlarm(activity,data);
                else
                   NotifyUtils.setAlarm(activity,data.getDate_as_long(),data);

                database.getDao().updateTodo(data);
                readTodoList(today);
            }
        }.start();

    }


    private void readTodoList(String quaryDate){

        new Thread(){
            @Override
            public void run() {

                Database db = Database.getInstance(activity);
                final List<ToDoModel> toDoModels = db.getDao().readAllTodo(quaryDate);

                if(toDoModels.size() > 0){
                    dataList.clear();

                    if(is_pro){
                        dataList.addAll(toDoModels);
                    }
                    else {

                        boolean isAdPut = false;
                        for (int i = 0; i < toDoModels.size(); i++) {
                            dataList.add(toDoModels.get(i));
                            if (toDoModels.size() < 5 && isAdPut) {
                                dataList.add(i + 1, new AdView());
                                isAdPut = true;
                            } else {
                                if (i % 5 == 0) {
                                    dataList.add(i + 1, new AdView());
                                }
                            }
                        }
                    }
//                    dataList.addAll(toDoModels);
                    activity.runOnUiThread(() ->{
                      view.findViewById(R.id.no_item_found).setVisibility(View.GONE);
                      setToDoListViewAdapter();});
                }
                else{
                    activity.runOnUiThread(() -> {
                        if(adapter != null)
                            adapter.notifyDataSetChanged();
                        view.findViewById(R.id.no_item_found).setVisibility(View.VISIBLE);
                    });
                }

            }
        }.start();

    }

    public void setNewAddedData(){
        readTodoList(today);
    }

    @Override
    public void onLongClick(ToDoModel data, int position) {
        showCompleteDialog(data);
    }

    public interface Commuinicator{
        void onSend(String day);
    }

}