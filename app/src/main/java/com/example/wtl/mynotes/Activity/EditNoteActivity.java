package com.example.wtl.mynotes.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wtl.mynotes.DB.NotesDB;
import com.example.wtl.mynotes.R;
import com.example.wtl.mynotes.Tool.HideScreenTop;

import java.util.Date;

public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView edit_back;//返回
    private TextView edit_time;//时间
    private TextView edit_over;//完成
    private EditText edit_content;//内容

    private ImageView editcolor;//背景颜色
    private ImageView editbold;//加粗
    private ImageView editoblique;//斜体
    private ImageView editcenter;//大字
    private ImageView editpoint;//重点
    private ImageView editpicture;//相册

    private NotesDB notesDB;//初始化数据库
    private SQLiteDatabase writebase;//写数据库

    private Animation animation_show;//淡出动画
    private Animation animation_hide;//淡入动画

    private boolean isBold = false;
    private boolean isLean = false;
    private boolean isBig = false;
    private boolean isPoint = false;
    private int start;
    private int count;

    private boolean change_ov = true;//判断是修改还是新建
    private String change_time;//修改的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        HideScreenTop.HideScreenTop(getWindow());
        Montior();
        animation_show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edit_show);
        animation_hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edit_hide);
        //EditText动态监听
        edit_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edit_content.getText().toString().equals("")) {
                    edit_over.setVisibility(View.VISIBLE);
                    edit_over.startAnimation(animation_show);
                }
                start = i;
                count = i2;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edit_content.getText().toString().equals("")) {
                    edit_over.setVisibility(View.GONE);
                    edit_over.startAnimation(animation_hide);
                }
                editchange(editable);
            }
        });
        edit_time.setText(getTime());
        notesDB = new NotesDB(this);
        writebase = notesDB.getWritableDatabase();
        Intent intent = getIntent();
        String state = intent.getStringExtra("State");
        if (state.equals("change")) {
            showcontent();
        }
    }

    private void Montior() {
        edit_back = (ImageView) findViewById(R.id.edit_back);
        edit_time = (TextView) findViewById(R.id.edit_time);
        edit_over = (TextView) findViewById(R.id.edit_over);
        edit_content = (EditText) findViewById(R.id.edit_content);

        editcolor = (ImageView) findViewById(R.id.editcolor);
        editbold = (ImageView) findViewById(R.id.editbold);
        editoblique = (ImageView) findViewById(R.id.editoblique);
        editcenter = (ImageView) findViewById(R.id.editcenter);
        editpicture = (ImageView) findViewById(R.id.editpicture);
        editpoint = (ImageView) findViewById(R.id.editpoint);

        edit_back.setOnClickListener(this);
        edit_over.setOnClickListener(this);
        edit_content.setOnClickListener(this);

        editcolor.setOnClickListener(this);
        editbold.setOnClickListener(this);
        editoblique.setOnClickListener(this);
        editcenter.setOnClickListener(this);
        editpicture.setOnClickListener(this);
        editpoint.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
        switch (view.getId()) {
            case R.id.edit_back:
                startActivity(intent);
                overridePendingTransition(R.anim.activity_right_out, R.anim.activity_right_in);//设置activity的平移动画
                finish();
                break;
            case R.id.edit_over:
                if (change_ov) {
                    //写数据库
                    ContentValues cv = new ContentValues();
                    cv.put(NotesDB.CONTENT, edit_content.getText().toString());
                    cv.put(NotesDB.TIME, getTime());
                    writebase.insert(NotesDB.TABLE_NAME, null, cv);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_right_out, R.anim.activity_right_in);
                    finish();
                } else {
                    Log.d("asdasd",edit_content.getText().toString());
                    ContentValues cv = new ContentValues();
                    cv.put(NotesDB.TIME, getTime());
                    cv.put(NotesDB.CONTENT, edit_content.getText().toString());
                    writebase.update(NotesDB.TABLE_NAME, cv, NotesDB.TIME + "=?", new String[]{change_time});
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_right_out, R.anim.activity_right_in);
                    finish();
                }
                break;
            case R.id.editbold:
                if (editbold.getDrawable().getCurrent().getConstantState().
                        equals(this.getResources().getDrawable(R.mipmap.editbold).getConstantState())) {
                    editbold.setImageResource(R.mipmap.touchblod);
                    isBold = true;
                } else {
                    editbold.setImageResource(R.mipmap.editbold);
                    isBold = false;
                }
                break;
            case R.id.editoblique:
                if (editoblique.getDrawable().getCurrent().getConstantState().
                        equals(this.getResources().getDrawable(R.mipmap.editoblique).getConstantState())) {
                    editoblique.setImageResource(R.mipmap.touchoblique);
                    isLean = true;
                } else {
                    editoblique.setImageResource(R.mipmap.editoblique);
                    isLean = false;
                }
                break;
            case R.id.editcenter:
                if (editcenter.getDrawable().getCurrent().getConstantState().
                        equals(this.getResources().getDrawable(R.mipmap.editbig).getConstantState())) {
                    editcenter.setImageResource(R.mipmap.toucheditbig);
                    isBig = true;
                } else {
                    editcenter.setImageResource(R.mipmap.editbig);
                    isBig = false;
                }
                break;
            case R.id.editpoint:
                if (editpoint.getDrawable().getCurrent().getConstantState().
                        equals(this.getResources().getDrawable(R.mipmap.editpoint).getConstantState())) {
                    editpoint.setImageResource(R.mipmap.toucheditpoint);
                    isPoint = true;
                } else {
                    editpoint.setImageResource(R.mipmap.editpoint);
                    isPoint = false;
                }
                break;
            case R.id.editcolor:
                if (editcolor.getDrawable().getCurrent().getConstantState().
                        equals(this.getResources().getDrawable(R.mipmap.editcolor).getConstantState())) {
                    editcolor.setImageResource(R.mipmap.toucheditcolor);
                } else {
                    editcolor.setImageResource(R.mipmap.editcolor);
                }
                break;
        }
    }

    /*
    * 获取当前时间
    * */
    private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date();
        String time = format.format(date);
        return time;
    }

    /*
    * 监听返回键
    * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(intent);
            overridePendingTransition(R.anim.activity_right_out, R.anim.activity_right_in);
            finish();
        }
        return false;
    }

    //实现即时文本字体改变
    private void editchange(Editable s) {
        if (isBold) {
            s.setSpan(new StyleSpan(Typeface.BOLD), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (isLean) {
            s.setSpan(new StyleSpan(Typeface.ITALIC), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (isBig) {
            s.setSpan(new RelativeSizeSpan(2.0f), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (isPoint) {
            s.setSpan(new ForegroundColorSpan(Color.BLUE), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    //进入该活动应显示的值并修改
    private void showcontent() {
        change_ov = false;
        Intent intent = getIntent();
        String postion = intent.getStringExtra("Postion");
        String sql = "select*from notes where time= '" + postion + "'";
        Cursor cursor = writebase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndex("content"));
            edit_content.setText(content);
        }
        change_time = postion;
    }

}
