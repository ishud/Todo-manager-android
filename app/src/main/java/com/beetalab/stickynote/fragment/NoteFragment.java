package com.beetalab.stickynote.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.activity.AddNoteActivity;
import com.beetalab.stickynote.adapter.NoteAdapter;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.NoteModel;
import com.beetalab.stickynote.model.ToDoModel;

import java.util.ArrayList;
import java.util.List;


public class NoteFragment extends Fragment implements NoteAdapter.OnItemClick {

    public static final int REQ_CODE_UPDATE_NOTE = 1002;

    private View view;
    private FragmentActivity activity;
    private RecyclerView listView;

    private final List<NoteModel> noteModels = new ArrayList<>();
    private NoteAdapter adapter;

    public NoteFragment() {
    }

    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_note, container, false);
        listView = view.findViewById(R.id.note_view_list);
        readNotes();
        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE_UPDATE_NOTE){
            if(resultCode == Activity.RESULT_OK){
                readNotes();
            }
        }
    }



    @Override
    public void onAttach( Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    private void setNoteAdapter(){
        adapter = new NoteAdapter(activity,noteModels,NoteFragment.this);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
       // RecyclerView.LayoutManager manager = new LinearLayoutManager(activity,RecyclerView.VERTICAL,false);
        listView.setLayoutManager(manager);
        listView.setAdapter(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(listView);

    }
    public void readNotes(){
        new Thread(){

            @Override
            public void run() {

                Database database = Database.getInstance(activity);
                final List<NoteModel> allNotes = database.getDao().getAllNotes();

                if(allNotes.size() > 0){
                    noteModels.clear();
                    noteModels.addAll(allNotes);
                   activity.runOnUiThread(() -> {
                       view.findViewById(R.id.no_item_found).setVisibility(View.GONE);
                        setNoteAdapter();
                   });
                }
                else{
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.findViewById(R.id.no_item_found).setVisibility(View.VISIBLE);
                        }
                    });
                }

            }
        }.start();
    }


    @Override
    public void onClick(int pos, NoteModel data,TextView view,TextView view1) {

        Intent intent = new Intent(activity, AddNoteActivity.class);
        intent.putExtra(AddNoteActivity.EXTRA_KEY_TYPE,1);
        intent.putExtra(AddNoteActivity.EXTRA_KEY_DATA,data);
       startActivityForResult(intent,REQ_CODE_UPDATE_NOTE);

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove( RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder,  RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped( RecyclerView.ViewHolder viewHolder, int direction) {
            final NoteModel model = noteModels.get(viewHolder.getAdapterPosition());
            noteModels.remove(model);
            adapter.notifyDataSetChanged();

            if(noteModels.size() == 0){
                view.findViewById(R.id.no_item_found).setVisibility(View.VISIBLE);
            }

            deleteNote(model);

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteNote(NoteModel model){
        new Thread(){

            @Override
            public void run() {
                Database db = Database.getInstance(activity);
                db.getDao().deleteNote(model);
            }
        }.start();
    }
}