package com.beetalab.stickynote.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.BaseActivity;
import com.beetalab.stickynote.Utils.TagUtils;
import com.beetalab.stickynote.config.StickyNoteApplication;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.NoteModel;

import java.util.Random;

public class AddNoteActivity extends BaseActivity {

    public static final String EXTRA_KEY_TYPE = "view_type";
    public static final String EXTRA_KEY_DATA = "data";

    public static final int VIEW_TYPE_TODO = 0;
    public static final int VIEW_TYPE_TODO_EDIT = 1;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private EditText edtTitle,edtNote;

    private boolean isEdit;
    private NoteModel data;

    boolean isTextChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        getExtras(getIntent());
        initView();
    }

    private void getExtras(Intent intent) {

        if(intent.hasExtra(EXTRA_KEY_TYPE)){
            isEdit = intent.getExtras().getInt(EXTRA_KEY_TYPE) == VIEW_TYPE_TODO_EDIT;
            data =(NoteModel) intent.getSerializableExtra(EXTRA_KEY_DATA);
        }
    }

    private void initView() {

        toolbar = findViewById(R.id.add_note_toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        edtNote = findViewById(R.id.note_edt);
        edtTitle = findViewById(R.id.title_edt);


        if(isEdit){
            edtNote.setText(data.getNote());
            edtTitle.setText(data.getTitle());
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             finish();
            }
        });


        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.save_item){

                saveNewNote(false);
            }

            if(item.getItemId() == R.id.menu_copy){
                copyNote();
            }

            if(item.getItemId() == R.id.menu_share){
                shareNote();
            }

            return false;
        });

        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }



    private void shareNote() {
        String val = edtTitle.getText().toString() +"\n\n\n" + edtNote.getText().toString();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
       // shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_TEXT, val);
        startActivity(shareIntent);
    }

    private void copyNote() {

        String val = edtTitle.getText().toString() +"\n\n\n" + edtNote.getText().toString();
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("note",val);
        manager.setPrimaryClip(data);

    }

    private void saveNewNote(boolean force) {
        String title = edtTitle.getText().toString();
        String note = edtNote.getText().toString();
        if(title == null || title.equals("")){
            if(!force)
                Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(note == null || note.equals("")){
            if(!force)
                Toast.makeText(this, "Please write note", Toast.LENGTH_SHORT).show();
            return;
        }

        else  if(isEdit)
            updateDataBAse(title,note);
        else
            saveToDb(title,note);

    }

    private void saveToDb(String title, String note) {

        new Thread(){

            @Override
            public void run() {

                Database db = Database.getInstance(AddNoteActivity.this);

                NoteModel model = new NoteModel();
                model.setId(System.currentTimeMillis());
                model.setTitle(title);
                model.setNote(note);

               Random r = new Random();
                final int i = r.nextInt(TagUtils.tags.length);

                String color = TagUtils.tags[i].getTag_color();
                model.setColor(color);

                db.getDao().insertNewNote(model);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(((StickyNoteApplication)getApplication()).isAdShowed){
                            setResult(Activity.RESULT_OK);
                           finish();
                            return;
                        }

                        loadInstratialAds(AddNoteActivity.this);
//                        setResult(RESULT_OK);
//                        finish();
                    }
                });

            }
        }.start();

    }

    private void updateDataBAse(String title, String note) {

        new Thread(){

            @Override
            public void run() {

                Database db = Database.getInstance(AddNoteActivity.this);

                data.setTitle(title);
                data.setNote(note);

                db.getDao().updateNote(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK);
                        finish();
                    }
                });

            }
        }.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save,menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}