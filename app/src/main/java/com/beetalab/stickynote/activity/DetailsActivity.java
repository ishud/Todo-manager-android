package com.beetalab.stickynote.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.BaseActivity;
import com.beetalab.stickynote.config.Constant;

public class DetailsActivity extends BaseActivity {

    public static final String CONTENT_TYPE = " content_type";
    
    public static final int CONTENT_ABOUT = 0;
    public static final int CONTENT_HELP = 1;

    private TextView content;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        initView();
        getExtras(getIntent());
    }

    @SuppressLint("SetTextI18n")
    private void getExtras(Intent intent) {

        final int stringExtra = intent.getIntExtra(CONTENT_TYPE,-1);

        if(stringExtra == 0){
            toolbarTitle.setText(R.string.about_developer);
            content.setText(Html.fromHtml(Constant.CONTENT_ABOUT));
        }
        else if(stringExtra == 1){
            toolbarTitle.setText(getString(R.string.help));
            content.setText(Html.fromHtml(Constant.CONTENT_HELP));
        }
        else{
            finish();
        }

        makeToolbarTitleCenter(toolbar,toolbarTitle);

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        content = findViewById(R.id.details_text);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> finish());

    }
}