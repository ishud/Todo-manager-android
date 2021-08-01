package com.beetalab.stickynote.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.activity.ToDoWithTagActivity;
import com.beetalab.stickynote.adapter.TagAdapter;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.TagModel;

import java.util.ArrayList;
import java.util.List;


public class TagsFragment extends Fragment implements TagAdapter.OnItemClick{

    private View view;
    private FragmentActivity activity;

    private TagAdapter adapter;

    private List<TagModel> dataList = new ArrayList<>();
    private RecyclerView listView;

    private Dialog dialog;

    public TagsFragment() {
        // Required empty public constructor
    }

    public static TagsFragment newInstance() {
        TagsFragment fragment = new TagsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_reminder, container, false);
        listView = view.findViewById(R.id.reminder_list);

        if(dataList.size() <= 0){
            readTags();
        }
        else
            setAdapter(dataList);

        return view;
    }

    @Override
    public void onAttach( Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    public void readTags(){
        new Thread(){

            @Override
            public void run() {
                Database db = Database.getInstance(activity);
                final List<TagModel> allTags = db.getDao().getAllTags();

                if(allTags.size() > 0){
                    dataList.clear();
                    activity.runOnUiThread(() -> setAdapter(allTags));
                }

            }
        }.start();
    }


    private void setAdapter(List<TagModel> tagModels){
        dataList.addAll(tagModels);
        adapter = new TagAdapter(activity,dataList,this);
        GridLayoutManager manager = new GridLayoutManager(activity,2,RecyclerView.VERTICAL,false);
        listView.setLayoutManager(manager);
        listView.setAdapter(adapter);

    }

    @Override
    public void onClick(int pos, TagModel data,int type) {

        if(type == -4){
            showremoveDialog(data,pos);
        }
        else{
            Intent intent = new Intent(activity, ToDoWithTagActivity.class);
            intent.putExtra(Constant.KEY_EXTRA_DATA,data);
            startActivity(intent);
        }
    }

    private void showremoveDialog(TagModel data,int pos) {

        View v = LayoutInflater.from(activity).inflate(R.layout.remove_tag_dialog,null,false);
        AlertDialog.Builder builder = new  AlertDialog.Builder(activity);
        builder.setView(v);

        TextView tit = v.findViewById(R.id.remove_dialog_txt);
        ProgressBar pr = v.findViewById(R.id.dialog_progress);
        TextView done = v.findViewById(R.id.conf_txt);

        tit.setText(String.format(getString(R.string.remove_message_tag),data.getTag_name()));
        done.setText("Confirm");
        pr.setVisibility(View.GONE);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tit.setText("Please Wait...");
                done.setVisibility(View.GONE);
                pr.setVisibility(View.VISIBLE);
                updateToProgress(tit,pr,done,data,pos);
            }
        });


        dialog = builder.create();
        dialog.show();

    }

    private void updateToProgress(TextView tit, ProgressBar pr, TextView done, TagModel data, int pos) {

//        tit.setText("Please Wait...");
//        done.setVisibility(View.GONE);
//        pr.setVisibility(View.VISIBLE);

        new Thread(){

            @Override
            public void run() {

                Database db = Database.getInstance(activity);
                db.getDao().deleteTag(data);
                db.getDao().deleteTodoByTagId(data.getTag_id());

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog != null && dialog.isShowing())
                            dialog.dismiss();

                          dataList.remove(data);
                          adapter.notifyItemRemoved(pos);

                        }
                });


            }
        }.start();

    }


}