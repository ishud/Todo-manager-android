package com.beetalab.stickynote.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.beetalab.stickynote.R;
import com.bumptech.glide.Glide;

import java.util.Calendar;

public class AboutActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView yearTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        imageView = findViewById(R.id.dev_logo);
        yearTxt = findViewById(R.id.id_txt);

        Calendar calendar = Calendar.getInstance();
        final int i = calendar.get(Calendar.YEAR);

        String s = "BeetaLab\u00a9 " + i;
        yearTxt.setText(s);

        Glide.with(this).load(R.drawable.dev_logo).into(imageView);

    }
}