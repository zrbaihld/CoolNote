package com.htq.baidu.coolnote.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.util.DisplayMetrics;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;



import com.htq.baidu.coolnote.R;

import com.htq.baidu.coolnote.entity.UpdataEvent;
import com.htq.baidu.coolnote.entity.User;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.BmobConstants;

import com.htq.baidu.coolnote.utils.PhotoUtil;
import com.htq.baidu.coolnote.utils.SPUtils;


import org.greenrobot.eventbus.EventBus;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by htq on 2016/9/4.
 */

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.mine_avatar)
    ImageView headIcon;
    @BindView(R.id.rl_nick)
    RelativeLayout rlNick;
    @BindView(R.id.rl_sex)
    RelativeLayout rlSex;
    @BindView(R.id.rl_email)
    RelativeLayout rlEmail;
    @BindView(R.id.rl_account)
    RelativeLayout rlAccount;
    @BindView(R.id.exit_btn)
    Button exitBtn;
    @BindView(R.id.img_back)
    ImageButton imgBtnBack;

    private String userSex = "";
    private final int UPDATE_USER_DIARYPWD = 1, UPDATE_USER_EMAIL = 2, UPDATE_USER_NICKNAME = 3;
    private PopupWindow mPopupWindow;
    protected int mScreenWidth;
    protected int mScreenHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        /*set it to be full screen*/
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        User.initDefaultAvatar(this,headIcon);
                //refreshAvatarFromLocal(BmobConstants.MyAvatarDir+"avatarIcon.png",headIcon);
        //userManager = BmobUserManager.getInstance(this);
    }

    @OnClick(R.id.img_back)
    public void back() {
        finish();
    }

    @OnClick({R.id.mine_avatar, R.id.rl_nick, R.id.rl_sex, R.id.rl_email, R.id.rl_account, R.id.exit_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_avatar:
                showAvatarPop();
                break;
            case R.id.rl_nick:
                AlertDialog nickNameDialog = buildeChangeInfoDialog(UPDATE_USER_NICKNAME);
                nickNameDialog.show();
                break;
            case R.id.rl_sex:
                changeUserSex();
                break;
            case R.id.rl_email:
                AlertDialog emailDialog = buildeChangeInfoDialog(UPDATE_USER_EMAIL);
                emailDialog.show();
                break;
            case R.id.rl_account:
              //  login();
                break;
            case R.id.exit_btn:
             //   goToRegisterActivity();
                break;
        }
    }

    private void changeUserSex() {
        final String[] sexItems = {"男", "女"};

        AlertDialog.Builder sexBuilder = new AlertDialog.Builder(UserInfoActivity.this)
                .setTitle("选择性别")
                .setSingleChoiceItems(sexItems, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userSex = sexItems[which];
                    }
                });
        sexBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  mPresenter.upDateInfo(UserBiz.UPDATE_USER_SEX, userSex);
            }
        });
        sexBuilder.create().show();
    }

    public AlertDialog buildeChangeInfoDialog(final int chageType) {
        View layout;
        final EditText et_pwd;
        final EditText et_change;
        ImageView icon;
        String dialogTitle = "";
        //载入,初始化自定义对话框布局
        layout = getLayoutInflater().inflate(R.layout.change_info_dialog_layout, null);
     //   et_pwd = (EditText) layout.findViewById(R.id.ed_pwd_confirm);
        et_change = (EditText) layout.findViewById(R.id.ed_change_info);
        icon = (ImageView) layout.findViewById(R.id.iv_change_info_icon);
        switch (chageType) {
            case UPDATE_USER_DIARYPWD:
                dialogTitle = "修改日记密码";
                et_change.setHint("新的日记密码");
                Drawable drawable1 = getDrawableByResource(R.drawable.diary_pwd);
                icon.setImageDrawable(drawable1);
                break;
            case UPDATE_USER_EMAIL:
                dialogTitle = "修改邮箱";
                et_change.setHint("设置您的邮箱");
                Drawable drawable2 = getDrawableByResource(R.drawable.email);
                icon.setImageDrawable(drawable2);
                break;
            case UPDATE_USER_NICKNAME:
                dialogTitle = "修改昵称";
                et_change.setHint("设置您的昵称");
                Drawable drawable3 = getDrawableByResource(R.drawable.nickname);
                icon.setImageDrawable(drawable3);
                break;
        }

        //设置对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     //   String pwd = et_pwd.getText().toString();
//                        if (pwd.equals(AccountUtils.getUserPwd(UserInfoActivity.this))) {
//                            //  upDateInfo(chageType, et_change.getText().toString());
//                        } else {
//                            Toast.makeText(UserInfoActivity.this, "密码不正确，重新输入", Toast.LENGTH_LONG).show();
//                        }
                    }
                });
        //弹出对话框
        return builder.create();
    }

    public Drawable getDrawableByResource(int id) {
        return getResources().getDrawable(id);
    }


    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;

    public String filePath = "";
    private void showAvatarPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // TODO Auto-generated method stub
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                File dir = new File(BmobConstants.MyAvatarDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 原图
                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date()));
                filePath = file.getAbsolutePath();// 获取相片的保存路径
                Uri imageUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
            }
        });
        layout_choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
              //  ShowLog("点击相册");
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_choose.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
            }
        });

        avatorPop = new PopupWindow(view, mScreenWidth, 600);
        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(exitBtn, Gravity.BOTTOM, 0, 0);
    }


    /**
     * @Title: startImageAction
     * @return void
     * @throws
     */
    private void startImageAction(Uri uri, int outputX, int outputY,
                                  int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
       // intent.putExtra("output", Uri.fromFile(new File(BaseApplication.avatarPath)));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
      //  intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//直接输出文件
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    Bitmap newBitmap;
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Snackbar.make(exitBtn,"SD卡不可用",Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    isFromCamera = true;
                    File file = new File(filePath);
                    degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                //    Log.i("life", "拍照后的角度：" + degree);
                    startImageAction(Uri.fromFile(file), 200, 200,
                            BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
                }
                break;
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                Uri uri = null;
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Snackbar.make(exitBtn,"SD卡不可用",Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    isFromCamera = false;
                    uri = data.getData();
                    startImageAction(uri, 200, 200,
                            BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
                } else {
                    Snackbar.make(exitBtn,"照片获取失败",Snackbar.LENGTH_LONG).show();

                }

                break;
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                // TODO sent to crop
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                if (data == null) {
                    // Toast.makeText(this, , Toast.LENGTH_SHORT).show();
                    Snackbar.make(exitBtn,"取消选择",Snackbar.LENGTH_LONG).show();
                    return;
                } else {
                    saveCropAvator(data);
                    SPUtils.put(this,"isSetAvatar",true);

                }

                // 初始化文件路径
                filePath = "";
                // 上传头像
                uploadAvatar();

                break;
            default:
                break;

        }
    }

    private void uploadAvatar() {
        //BmobLog.i("头像地址：" + path);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                String url = bmobFile.getFileUrl(UserInfoActivity.this);
                updateUserAvatar(url);
                AccountUtils.saveUserHeadUrl(UserInfoActivity.this,url);
                toUpdateUserInfo(url);
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub

            }


            @Override
            public void onFailure(int arg0, String msg) {
                // TODO Auto-generated method stub
                Snackbar.make(exitBtn,"头像上传失败：" + msg+String.valueOf(arg0),Snackbar.LENGTH_LONG).show();
              //  ShowToast("头像上传失败：" + msg+String.valueOf(arg0));
            }
        });
    }

    private void updateUserAvatar(final String url) {
        User  u =new User();
        u.setUserHeadUrl(url);
        updateUserData(u,new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Snackbar.make(exitBtn,"头像更新成功！",Snackbar.LENGTH_SHORT).show();
                //ShowToast();
                // 更新头像
                User.refreshAvatarFromServe(url,headIcon);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Snackbar.make(exitBtn,"头像更新失败：" + msg+String.valueOf(code),Snackbar.LENGTH_SHORT).show();
                //ShowToast();
            }
        });
    }



    private void updateUserData(User user,UpdateListener listener){
        BmobUser current = user.getCurrentUser(UserInfoActivity.this);
        user.setObjectId(current.getObjectId());
        user.update(this, listener);
    }

    String path;
    String  avatarFile="avatarIcon.png";
    /**
     * 保存裁剪的头像
     *
     * @param data
     */
    private void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
        //    Log.i("life", "avatar - bitmap = " + bitmap);
            if (bitmap != null) {
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                headIcon.setImageBitmap(bitmap);

                // 保存图片
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date())+".png";
                path = BmobConstants.MyAvatarDir + avatarFile;//filename;

//                PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
//                        bitmap, true);
                PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, avatarFile,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    /**
     * 刷新其他界面的头像
     */
    public void toUpdateUserInfo(String url) {
        UpdataEvent event = new UpdataEvent();
        event.setType(UpdataEvent.UPDATE_USER_INFOS);
       // event.setString(BmobConstants.MyAvatarDir+avatarFile);
        event.setString(url);
        EventBus.getDefault().post(event);
    }
}
