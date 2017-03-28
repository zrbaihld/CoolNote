package com.htq.baidu.coolnote.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.entity.User;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.MD5Util;
import com.htq.baidu.coolnote.utils.SPUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by htq on 2016/9/3.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_login)
    Button loginBtn;
    @BindView(R.id.btn_register)
    TextView registerBtn;
    @BindView(R.id.login_name)
    EditText loginName;
    @BindView(R.id.login_pwd)
    EditText loginPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initDrawableSize();

    }


    private void initDrawableSize()
    {
        Drawable accountDraw=getResources().getDrawable(R.drawable.login_icon_account);
        accountDraw.setBounds(0,0,45,45);
        Drawable passwordDraw=getResources().getDrawable(R.drawable.login_icon_password);
        passwordDraw.setBounds(0,0,45,45);
        loginName.setCompoundDrawables(accountDraw,null,null,null);
        loginPwd.setCompoundDrawables(passwordDraw,null,null,null);
    }
    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                goToRegisterActivity();
                break;
        }
    }




    private void login() {
        final String name=loginName.getText().toString();
        final String pwd=loginPwd.getText().toString();
        final ProgressDialog progress = new ProgressDialog(
                LoginActivity.this);
        progress.setMessage("正在登陆...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        User user = new User();
        user.setUsername(name);
        user.setPassword(MD5Util.MD5(pwd));

       // user.setPassword(pwd);
    //    Snackbar.make(loginBtn,"登录成功！",Snackbar.LENGTH_SHORT).show();
        user.login(this, new SaveListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
              //  Snackbar.make(loginBtn,"登录成功！",Snackbar.LENGTH_SHORT).show();
                //将用户信息保存至本地
                SPUtils.put(LoginActivity.this,"user_name",name);
                SPUtils.put(LoginActivity.this,"pwd",MD5Util.MD5(pwd));
                User user2;
                user2 = BmobUser.getCurrentUser(LoginActivity.this, User.class);
                //将登陆信息保存本地
                AccountUtils.saveUserInfos(LoginActivity.this, user2, MD5Util.MD5(pwd));
                goToHomeActivity();

            }

            @Override
            public void onFailure(int code, String msg) {
                progress.dismiss();
                Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_LONG).show();
               // Snackbar.make(loginBtn,"登录失败！",Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private void goToHomeActivity() {

        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void goToRegisterActivity() {
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
