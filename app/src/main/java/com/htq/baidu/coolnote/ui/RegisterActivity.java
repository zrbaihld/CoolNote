package com.htq.baidu.coolnote.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.entity.User;
import com.htq.baidu.coolnote.utils.CommonUtils;
import com.htq.baidu.coolnote.utils.MD5Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by htq on 2016/9/4.
 */
public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText accountEt;
    @BindView(R.id.et_password)
    EditText pwdEt;
    @BindView(R.id.et_email)
    EditText twicePwdEt;
    @BindView(R.id.btn_register)
    Button registerBtn;
    @BindView(R.id.img_back)
    ImageButton imgBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       // requestWindowFeature(Window.FEATURE_NO_TITLE);
//        /*set it to be full screen*/
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.img_back)
    public void back() {
        finish();
    }


    @OnClick(R.id.btn_register)
    public void register(){
        String name =accountEt.getText().toString();
        String password = pwdEt.getText().toString();
        String pwd_again = twicePwdEt.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Snackbar.make(registerBtn,"账号不能为空",Snackbar.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Snackbar.make(registerBtn,"密码不能为空",Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!pwd_again.equals(password)) {
            Snackbar.make(registerBtn,"两次密码输入不一致",Snackbar.LENGTH_LONG).show();
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            Snackbar.make(registerBtn,"网络连接出错",Snackbar.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User bu = new User();
        bu.setUsername(name);
        bu.setPassword(MD5Util.MD5(password));
        //将user和设备id进行绑定aa
 //       bu.setUserSex(true);
//        bu.setDeviceType("android");
//        bu.setInstallId(BmobInstallation.getInstallationId(this));
        bu.signUp(RegisterActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                progress.dismiss();
                Snackbar.make(registerBtn,"注册成功",Snackbar.LENGTH_LONG).show();
                // 将设备与username进行绑定
              //  userManager.bindInstallationForRegister(bu.getUsername());
                // 启动主页
              //  Log.i("test","enter mainActivity");
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Snackbar.make(registerBtn,"注册失败:",Snackbar.LENGTH_LONG).show();
                progress.dismiss();
            }
        });
    }
}
