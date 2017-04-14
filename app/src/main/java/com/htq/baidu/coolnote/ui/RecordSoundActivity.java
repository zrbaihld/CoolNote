package com.htq.baidu.coolnote.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danielkim.soundrecorder.fragments.RecordFragment;
import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.db.NoteDatabase;
import com.htq.baidu.coolnote.entity.NotebookData;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.StringUtils;
import com.htq.baidu.coolnote.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/12.
 */

public class RecordSoundActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragmetn)
    FrameLayout fragmetn;
    @BindView(R.id.base_toolbar_right_tv)
    TextView base_toolbar_right_tv;
    @BindView(R.id.note_title_edit)
    EditText editText;

    private String path;
    private long mElapsedMillis = 0;
    private NotebookData editData;
    private NoteDatabase noteDb;
    private RecordFragment recordFragment;

    private MsgReceiver msgReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordsound);
        ButterKnife.bind(this);
        toolbar.setTitle("录音");
        base_toolbar_right_tv.setText("保存");
        base_toolbar_right_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(path)) {
                    Toast.makeText(RecordSoundActivity.this, "没有录音", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(RecordSoundActivity.this, "没有输入标题", Toast.LENGTH_LONG).show();
                    return;
                }
                save();
            }
        });

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        noteDb = new NoteDatabase(this);
        if (editData == null) {
            editData = new NotebookData();
        }
        if (StringUtils.isEmpty(editData.getDate())) {
            editData.setDate(StringUtils.getDataTime("yyyy/MM/dd"));
        }
        initFragment();

        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        recordFragment = RecordFragment.newInstance(0);
        ft.replace(R.id.fragmetn, recordFragment, null);
        ft.commit();
    }

    /**
     * 保存已编辑内容到数据库
     */
    private void save() {
        setNoteProperty();
        noteDb.save(editData);
        Toast.makeText(RecordSoundActivity.this, "保存成功", Toast.LENGTH_LONG).show();
    }

    /**
     * 封装NotebookData
     */
    private void setNoteProperty() {
        if (editData.getId() == 0) {
            editData.setId(-1
                    * StringUtils.toInt(
                    StringUtils.getDataTime("dddHHmmss"), 0));
        }
        String userId = AccountUtils.getUserId(this);
        editData.setUserId(userId);
        editData.setUnixTime(StringUtils.getDataTime("yyyy-MM-dd HH:mm:ss"));
        editData.setLevel(editData.getLevel());
        editData.setSoundpath(path);
        editData.setFather(MainActivity.FATHER);
        editData.setmElapsedMillis(mElapsedMillis+"");
        editData.setTitle(editText.getText().toString());
        editData.setObjectId(editData.getObjectId());
    }

    /**
     * 广播接收器
     *
     * @author len
     */
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            path = intent.getStringExtra("FilePath");
            mElapsedMillis = intent.getLongExtra("mElapsedMillis",0);
        }

    }
}
