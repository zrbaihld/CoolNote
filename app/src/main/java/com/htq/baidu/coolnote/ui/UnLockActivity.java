package com.htq.baidu.coolnote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.utils.SystemUtils;
import com.htq.baidu.coolnote.widget.patternlock.LockPatternView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by htq on 2016/8/12.
 */
public class UnLockActivity extends AppCompatActivity {

    @BindView(R.id.lockView)
    LockPatternView mLockPatternView;

    private String mPasswordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        ButterKnife.bind(this);
        Toast.makeText(getApplicationContext(), "密码正确", Toast.LENGTH_SHORT).show();
        mLockPatternView.setActivityContext(UnLockActivity.this);
        mLockPatternView.setLockListener(new LockPatternView.OnLockListener() {
            String password = new SystemUtils(UnLockActivity.this).getString("password");

            @Override
            public void getStringPassword(String password) {
                mPasswordStr = password;
            }

            @Override
            public boolean isPassword() {
                if (mPasswordStr.equals(password)) {
                    Snackbar.make(mLockPatternView, "密码正确",  Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(UnLockActivity.this, MainActivity.class);
                    startActivity(intent);
                    UnLockActivity.this.finish();
                    return true;
                } else {
                    Snackbar.make(mLockPatternView, "密码不正确",  Snackbar.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }
}
