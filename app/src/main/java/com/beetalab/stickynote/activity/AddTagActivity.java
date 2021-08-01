package com.beetalab.stickynote.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.Utils.BaseActivity;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.db.Database;
import com.beetalab.stickynote.model.TagModel;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import static com.beetalab.stickynote.activity.PremiumActivity.REQ_CODE_UPGRADE_PREMIUM;

public class AddTagActivity extends BaseActivity {

    private EditText titleEdt;
    private View pallet;
    private Button btnCreate;
    private Toolbar toolbar;
    private TextView titleTxt;

    int color = -1;

    private boolean is_pro;
    private String tag_name ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        is_pro = AppUtils.getSharedPreference(this).getBoolean(Constant.PREF_KEY_IS_PRO_USER,false);
        initView();
    }

    private void initView() {

        titleEdt = findViewById(R.id.title_edt);
        pallet = findViewById(R.id.color_pallete);
        btnCreate = findViewById(R.id.btn_create);
        toolbar = findViewById(R.id.toolbar);
        titleTxt = findViewById(R.id.toolbar_title);

        pallet.setBackgroundColor(Color.BLUE);

        titleEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        makeToolbarTitleCenter(toolbar,titleTxt);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> finish());

        pallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shoePicker();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTag();
            }
        });

    }

    private void shoePicker() {
        ColorPickerDialogBuilder
                .with(this)
                .density(12)
                .setTitle("Select Tag Color")
                .initialColor(Color.BLUE)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        color = selectedColor;
                    }
                })
                .setPositiveButton("Pick", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                        color = lastSelectedColor;
                        changeBackground(color);
                        d.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })
                .build().show();
    }

    private void changeBackground(int lastSelectedColor) {
        pallet.setBackgroundColor(lastSelectedColor);
    }

    private void setTag(){

        String  tagname = titleEdt.getText().toString().trim();
        if(tagname.equals(""))
        {
            Toast.makeText(this, "Tag name required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(tagname.length() < 3){
            Toast.makeText(this, "Tag name required minimum three letters", Toast.LENGTH_SHORT).show();
            return;
        }
        tag_name = tagname;
        saveTag(tagname);

    }

    private void saveTag(String tagname) {

        new Thread(){

            @Override
            public void run() {

                Database db = Database.getInstance(AddTagActivity.this);

                final int customTagCount = db.getDao().getCustomTagCount(true);

                if(!is_pro){
                    if(customTagCount >= 3){
                        Intent intent = new Intent(AddTagActivity.this,PremiumActivity.class);
                        startActivityForResult(intent,REQ_CODE_UPGRADE_PREMIUM);
                        overridePendingTransition(0,0);
                        return;
                    }
                }


                TagModel tagModel = new TagModel();
                tagModel.setTask_count(0);
                String hexColor = String.format("#%06X", (Color.BLUE & color));
                tagModel.setTag_color(hexColor);
                tagModel.setTag_id("tag_" + System.currentTimeMillis());
                tagModel.setTag_is_custom(true);
                tagModel.setTag_name(tagname);

                db.getDao().insertTag(tagModel);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loadInstratialAds(AddTagActivity.this);

                        setResult(RESULT_OK);
                        finish();
                    }
                });


            }
        }.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE_UPGRADE_PREMIUM && resultCode == RESULT_OK){
            saveTag(tag_name);
        }
    }
}