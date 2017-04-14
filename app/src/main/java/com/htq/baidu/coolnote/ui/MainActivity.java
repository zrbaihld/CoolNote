package com.htq.baidu.coolnote.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.db.NoteDatabase;
import com.htq.baidu.coolnote.entity.NotebookData;
import com.htq.baidu.coolnote.entity.UpdataEvent;
import com.htq.baidu.coolnote.entity.User;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.BmobConstants;
import com.htq.baidu.coolnote.utils.Constants;
import com.htq.baidu.coolnote.utils.PhotoUtil;
import com.htq.baidu.coolnote.utils.SPUtils;
import com.htq.baidu.coolnote.utils.StringUtils;
import com.htq.baidu.coolnote.utils.SystemUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //此处不能定义为全局的，否则在调用changFragment函数时会报java.lang.IllegalStateException: commit already called
//    private   FragmentManager fm=getSupportFragmentManager();
//     private  FragmentTransaction ft=fm.beginTransaction();

    //    protected   FloatingActionButton fab;
    private boolean isFirstUse;
    protected DrawerLayout drawer;
    private long mBackPressedTime = 0;
    private NoteBookFragment noteBookFragment;
    private ImageView headIcon;
    private View baseView;

    public FloatingActionMenu fab;
    public static String FATHER = "";
    private String s_title = "颖火虫笔记";

    private Toolbar toolbar;
    private View exitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        isFirstUse = new SystemUtils(MainActivity.this).isFirstUse();
        if (isFirstUse) {
            initIntruduceData();
        }
        initMainFragment();
        initUi();
        initHead();
        initBgPic();
        //  User.refreshAvatarFromLocal(BmobConstants.MyAvatarDir+"avatarIcon.png",headIcon);
        //注册EventBus
        EventBus.getDefault().register(this);
    }

    /**
     * 用于刷新头像的
     *
     * @param event
     */
    @Subscribe
    public void onEventBackgroundThread(UpdataEvent event) {
        if (event.getType() == UpdataEvent.UPDATE_USER_INFOS) {
            User.initDefaultAvatar(this, headIcon);
            //Snackbar.make(headIcon,"update",Snackbar.LENGTH_LONG).show();
            // mPresenter.setUserNickName();


        }
    }
/**
 * 设置界面标题
 */

    public void setTitleChild(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            toolbar.setTitle(title);
            Log.e("exce", "!TextUtils.isEmpty(title)  ");
        } else {
            toolbar.setTitle(s_title);
        }
    }

    /**
     * 初始化ui
     */
    private void initUi() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        exitBtn = findViewById(R.id.floating_action_menu);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof TextView) {
                TextView tv = (TextView) toolbar.getChildAt(i);
                tv.setEllipsize(TextUtils.TruncateAt.START);
                tv.setMaxEms(10);
            }
        }
        toolbar.setTitle(s_title);

        fab = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);


        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        baseView = navigationView.getHeaderView(0);
        headIcon = (ImageView) baseView.findViewById(R.id.imageView);

        headIcon.setOnClickListener(headIconOnTouchListener);


    }

    /**
     * 头像点击跳转到用户资料界面
     */
    private View.OnClickListener headIconOnTouchListener = new View.OnClickListener() {
        @Override
        public void onClick(View vt) {

            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(intent);

        }
    };

    /**
     * 换肤
     */
    private void initBgPic() {

        SystemUtils systemUtils = new SystemUtils(this);
        String path = systemUtils.getPath();
        if (path != null) {
            Bitmap bitmap = systemUtils.getBitmapByPath(this, path);
            if (bitmap != null) {
                drawer.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));

            }
        }
    }

    /**
     * 设置头像
     */
    private void initHead() {

        User.initDefaultAvatar(this, headIcon);


    }

    /**
     * 加载Fragment
     */
    private void initMainFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        noteBookFragment = new NoteBookFragment();
        ft.replace(R.id.main_fraglayout, noteBookFragment, null);
        ft.commit();
    }

    private boolean isnote = true;

    /**
     * 变换Fragment
     * @param fragment
     */
    protected void changeFragment(Fragment fragment) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fraglayout, fragment, null);
        //    ft.addToBackStack(fragment.toString());
        ft.commit();

    }

    /**
     * 判断是否是第一次运行app 然后初始化数据库
     */
    private void initIntruduceData() {
        NoteDatabase noteDb = new NoteDatabase(MainActivity.this);
        noteDb.insertIntroduce(this);
        new SystemUtils(MainActivity.this).set("isFirstUse", "false");
    }

    /**
     * 点击返回的时候判断
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!TextUtils.isEmpty(FATHER) && isnote) {
                noteBookFragment.back();
            } else {
                long curTime = SystemClock.uptimeMillis();
                if ((curTime - mBackPressedTime) < (2 * 1000)) {
                    finish();
                    System.exit(0);
                } else {
                    mBackPressedTime = curTime;
                    Snackbar.make(drawer, "双击退出程序", Snackbar.LENGTH_LONG).show();
                }

            }


            //   super.onBackPressed();
        }
    }

    /**
     * 侧边栏的点击操作
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_change_bg) {
            isnote = false;
            setTitle("更换皮肤");
            ChangeBgFragment changeBgFragment = new ChangeBgFragment();
            changeFragment(changeBgFragment);
            fab.hideMenu(true);
        } else if (id == R.id.nav_home) {
            isnote = true;
            setTitleChild(FATHER);
            initMainFragment();

        } else if (id == R.id.nav_setting) {
            isnote = false;
            setTitle("设置");
            SettingFragment settingFragment = new SettingFragment();
            changeFragment(settingFragment);
            fab.hideMenu(true);

        } else if (id == R.id.nav_share) {
            SystemUtils.shareApp(this);

        }
//        else if (id == R.id.nav_about) {
//            isnote = false;
//            setTitle("关于应用");
//            AboutAppFragment aboutAppFragment = new AboutAppFragment();
//            changeFragment(aboutAppFragment);
//            fab.hideMenu(true);
//        }
        else if (id == R.id.nav_exit) {
            finish();
            System.exit(0);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 新建的点击操作
     * @param menuItem
     */
    @OnClick({R.id.menu_item_text_font, R.id.menu_item_clock, R.id.menu_item_pic,R.id.menu_item_video})
    public void click(FloatingActionButton menuItem) {
        switch (menuItem.getId()) {
            case R.id.menu_item_text_font://新建分类
                Log.e("exce", "新建分类");
                final EditText et = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("新建分类")
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = et.getText().toString();
                                save(s);
                                Log.e("exce", "新建分类  " + s);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();


                break;
            case R.id.menu_item_clock://新建笔记
                Log.e("exce", "新建笔记");
                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(NoteEditFragment.NOTE_FROMWHERE_KEY,
                        NoteEditFragment.QUICK_DIALOG);
                intent.putExtra(Constants.BUNDLE_KEY_ARGS, bundle);
                startActivity(intent);
                break;
            case R.id.menu_item_pic://新建图片

                Log.e("exce", "新建图片");
                showAvatarPop();
                break;
            case R.id.menu_item_video://新建录音
                Log.e("exce", "新建录音");
                startActivity(new Intent(this,RecordSoundActivity.class));
                break;
        }


    }

    private NotebookData editData;
    private NoteDatabase noteDb;

    /**
     * 保存到数据库
     * @param classis
     */
    private void save(String classis) {
        editData = new NotebookData();
        if (noteDb == null) {
            noteDb = new NoteDatabase(this);
        }
        setNoteProperty(classis);
        noteDb.save(editData);

        noteBookFragment.initData();
    }

    /**
     * 封装NotebookData的操作
     * @param classis
     */
    private void setNoteProperty(String classis) {
        if (editData.getId() == 0) {
            editData.setId(-1
                    * StringUtils.toInt(
                    StringUtils.getDataTime("dddHHmmss"), 0));
        }
        if (StringUtils.isEmpty(editData.getDate())) {
            editData.setDate(StringUtils.getDataTime("yyyy/MM/dd"));
        }
        String userId = AccountUtils.getUserId(this);
        editData.setUserId(userId);

        editData.setUnixTime(StringUtils.getDataTime("yyyy-MM-dd HH:mm:ss"));
        editData.setFather(FATHER);
        editData.setObjectId(editData.getObjectId());
        if (TextUtils.isEmpty(classis)) {
            editData.setImgpath(path);
        } else {
            editData.setClassified(classis);
        }


    }


    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;
    protected int mScreenWidth;
    protected int mScreenHeight;

    public String filePath = "";

    /**
     * 显示选择相册还是相机的pop
     */
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
        avatorPop.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);

        Log.e("exce", " avatorPop.showAtLocation ");
    }

    /**
     * 跳转到图片裁剪的界面
     * @return void
     * @throws跳转到
     * @Title: startImageAction
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
        intent.putExtra("circleCrop", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    Bitmap newBitmap;
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;

    /**
     * activity自带的跳转接收返回数据 用来接受相册 和相机 还有裁剪完以后传回来的数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Snackbar.make(exitBtn, "SD卡不可用", Snackbar.LENGTH_LONG).show();
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
                        Snackbar.make(exitBtn, "SD卡不可用", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    isFromCamera = false;
                    uri = data.getData();
                    startImageAction(uri, 200, 200,
                            BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
                } else {
                    Snackbar.make(exitBtn, "照片获取失败", Snackbar.LENGTH_LONG).show();

                }

                break;
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                // TODO sent to crop
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                if (data == null) {
                    // Toast.makeText(this, , Toast.LENGTH_SHORT).show();
                    Snackbar.make(exitBtn, "取消选择", Snackbar.LENGTH_LONG).show();
                    return;
                } else {
                    saveCropAvator(data);
                    SPUtils.put(this, "isSetAvatar", true);

                }

                // 初始化文件路径
                filePath = "";
                // 上传头像

                break;
            default:
                break;

        }
    }

    String path;
    String avatarFile = "avatarIcon.png";

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
                        .format(new Date()) + ".png";
                path = BmobConstants.MyAvatarDir + filename;//filename;

//                PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
//                        bitmap, true);
                PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
                        bitmap, true);
                Log.e("exce", " saveBitmap ");
                save("");
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } else {
                Log.e("exce", " bitmap != null ");
            }
        } else {
            Log.e("exce", " extras != null ");
        }
    }


}
