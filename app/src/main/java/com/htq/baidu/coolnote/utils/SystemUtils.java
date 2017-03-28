package com.htq.baidu.coolnote.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;

import com.htq.baidu.coolnote.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by htq on 2016/8/10.
 */
public class SystemUtils {

    private Context context;
    private  String PREF_NAME = "creativelocker.pref";
    public  final String KEY_NOTE_DRAFT = "KEY_NOTE_DRAFT";
    private  final String BG_PIC_PATH ="bg_pic_path";

    public SystemUtils(Context context)
    {
        this.context=context;
    }

    public  String getNoteDraft() {
        return getPreferences().getString(
                KEY_NOTE_DRAFT , "");
    }


    public  void setNoteDraft(String draft) {
        set(KEY_NOTE_DRAFT , draft);
    }

    public  void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public  SharedPreferences getPreferences() {
        SharedPreferences pre =context.getSharedPreferences(PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        return pre;
    }

    public boolean isFirstUse()
    {
       if(getString("isFirstUse")==null)
       {
           return true;
       }
        return false;
    }
public  boolean isTarn()
{
   return getBoolean("isTran");
}
    public String getString(String str)
    {
        SharedPreferences share= getPreferences();
        return share.getString(str,null);
    }
    public boolean getBoolean(String str)
    {
        SharedPreferences share= getPreferences();
        return share.getBoolean(str,false);
    }
    public void setBoolean(String str,boolean bool)
    {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(str,bool);
        editor.commit();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SharedPreferences getPreferences(String prefName) {
        return context.getSharedPreferences(prefName,
                Context.MODE_MULTI_PROCESS);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenW(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenH(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 保存背景皮肤图片的地址
     * @author: htq
     */
    public  void saveBgPicPath(String path)
    {
        set(BG_PIC_PATH,path);

    }

    public  String getPath() {
        return getString(BG_PIC_PATH);
    }

    public Bitmap getBitmapByPath(Activity aty, String path) {
        AssetManager am = aty.getAssets();
        Bitmap bitmap = null;
        InputStream is =null;
        try {
            is = am.open("bkgs/" + path);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * @author htq_
     * @param mActivity
     * bolg:bolg.csdn.net/htq__
     */
    public  static void shareApp(Activity mActivity)
    {
        String shareAppContent="各位亲爱的小伙伴们，我发现了一款非常好用且颜值爆表的记事本App，分享给大家，记得关注作者的博客http://blog.csdn.net/htq__，福利多多哦！";

        new File(mActivity.getFilesDir(), "share.jpg").deleteOnExit();
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream = mActivity.openFileOutput(
                    "share.jpg", 1);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap pic=BitmapFactory.decodeResource(mActivity.getResources(),R.mipmap.app_icon);
        pic.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);


        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("sms_body", shareAppContent);
        intent.putExtra("android.intent.extra.TEXT",shareAppContent);
        intent.putExtra("android.intent.extra.STREAM",
                Uri.fromFile(new File(mActivity.getFilesDir(), "share.jpg")));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(Intent.createChooser(intent,"好东西要与小伙伴们一起分享"));
    }

    public  static void shareNote(Activity mActivity,String noteContent)
    {

//        new File(mActivity.getFilesDir(), "share.jpg").deleteOnExit();
//        FileOutputStream fileOutputStream=null;
//        try {
//            fileOutputStream = mActivity.openFileOutput(
//                    "share.jpg", 1);
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        Bitmap pic=BitmapFactory.decodeResource(mActivity.getResources(),R.mipmap.app_icon);
//        pic.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);


        Intent intent = new Intent("android.intent.action.SEND");
      //  intent.setType("image/*");
        intent.setType("text/plain");
      //  intent.putExtra("sms_body", noteContent);
        intent.putExtra("android.intent.extra.TEXT",noteContent);
//        intent.putExtra("android.intent.extra.STREAM",
//                Uri.fromFile(new File(mActivity.getFilesDir(), "share.jpg")));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(Intent.createChooser(intent,"好东西要与小伙伴们一起分享"));
    }
    /**
     * 分享功能
     * @param msgTitle
     *            消息标题
     * @param msgText
     *            消息内容
     * @param imgPath
     *            图片路径，不分享图片则传null
     */
    public static void shareMsg(Activity mActivity, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(intent,"好东西要与小伙伴们一起分享"));
    }
}
