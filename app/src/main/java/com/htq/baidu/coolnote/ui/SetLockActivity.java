package com.htq.baidu.coolnote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.utils.SystemUtils;
import com.htq.baidu.coolnote.widget.patternlock.LockPatternView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by htq on 2016/8/12.
 */
public class SetLockActivity extends AppCompatActivity {

    @BindView(R.id.tv_activity_set_lock_title)
    TextView mTitleTv;

    @BindView(R.id.lockView)
    LockPatternView mLockPatternView;

    @BindView(R.id.btn_password_clear)
    Button mClearBtn;

    private String mPassword;
    private boolean isFirst = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_lock);
        ButterKnife.bind(this);
        setTitle("应用加锁");

        initUI();
    }

    private void initUI()
    {
        mLockPatternView.setActivityContext(SetLockActivity.this);
        mLockPatternView.setLockListener(new LockPatternView.OnLockListener() {
            @Override
            public void getStringPassword(String password) {
                if (isFirst) {
                    mPassword = password;
                    mTitleTv.setText("再次输入手势密码");
                    isFirst = false;
                    mClearBtn.setVisibility(View.VISIBLE);
                } else {
                    if (password.equals(mPassword)) {
                        new SystemUtils(SetLockActivity.this).set("password",password);
                        new SystemUtils(SetLockActivity.this).setBoolean("isSetLock", true);
                        startActivity(new Intent(SetLockActivity.this, MainActivity.class));
                        SetLockActivity.this.finish();
                    }else {
                        isFirst =true;
                        Snackbar.make( mTitleTv,"两次密码不一致，请重新设置",Snackbar.LENGTH_LONG).show();
                        mPassword = "";

                        mTitleTv.setText("设置手势密码");
                        mClearBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public boolean isPassword() {
                return false;
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPassword = "";
                isFirst = true;
                mClearBtn.setVisibility(View.GONE);
            }
        });
    }
//    private void initData()
//    {
//
//    }
}
