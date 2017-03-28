package com.htq.baidu.coolnote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.entity.User;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.MD5Util;
import com.htq.baidu.coolnote.utils.SPUtils;
import com.htq.baidu.coolnote.utils.SystemUtils;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by htq on 2016/8/12.
 */
public class WelcomeActivity extends AppCompatActivity {
    private boolean isSetLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        //初始化bmob
//        String libName = "bmob";
//        System.loadLibrary(libName );

        isSetLock = new SystemUtils(WelcomeActivity.this).getBoolean("isSetLock");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;
                if (isSetLock) {

                    intent = new Intent(WelcomeActivity.this, UnLockActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                } else {
                    autoLogin();
                    //intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                }

            }
        }, 2000);
    }
    private void autoLogin()
    {
            if(SPUtils.contains(this,"user_name"))
            {
                String name=(String)SPUtils.get(this,"user_name","");
                String pwd=(String)SPUtils.get(this,"pwd","");
                User user = new User();
                user.setUsername(name);
                user.setPassword(pwd);
                user.login(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                      //  Snackbar.make(loginBtn,"登录成功！",Snackbar.LENGTH_SHORT).show();
                        goToHomeActivity();

                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        //Snackbar.make(loginBtn,"登录失败！",Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(WelcomeActivity.this,msg, Toast.LENGTH_SHORT).show();
                        goToLoginActivity();//自动登陆失败，用户手动登陆
                    }
                });
            }
        else
            {
                goToLoginActivity();
            }
    }
    private void goToHomeActivity()
    {
        Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();

    }
    private void goToLoginActivity()
    {
        Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();

    }
}
