package com.htq.baidu.coolnote.entity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.app.BaseApplication;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.BmobConstants;
import com.htq.baidu.coolnote.utils.ImageLoadOptions;
import com.htq.baidu.coolnote.utils.SPUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class User extends BmobUser implements Serializable{

    private String nickname;
    private String sex;
    private String notePwd;
    private String headUrl;

    public String getUserNickname() {
        if (!TextUtils.isEmpty(nickname))
            return nickname;
        else
            return "";
    }

    public void setUserNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserSex() {
        if (!TextUtils.isEmpty(sex))
            return sex;
        else
            return "";
    }

    public void setUserSex(String sex) {
        this.sex = sex;
    }

    public String getUserNotePwd() {
        if (!TextUtils.isEmpty(notePwd))
            return notePwd;
        else
            return "";
    }

    public void setUserNotePwd(String notePwd) {
        this.notePwd = notePwd;
    }

    public String getUserHeadUrl() {
        if (!TextUtils.isEmpty(headUrl))
            return headUrl;
        else
            return "";
    }

    public void setUserHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public void Login(String account ,String pwd ,final Context context)
    {


    }
    static public void setUserHeadFromCache(Context context, ImageView img){
        String mSavePath=context.getCacheDir().getAbsolutePath();
        File saveFile = new File(mSavePath, "userHead");
        Bitmap bitmap = BitmapFactory.decodeFile(saveFile.getAbsolutePath());
        img.setImageBitmap(bitmap);

    }

    /**
     * 更新头像 refreshAvatar
     *
     * @return void
     * @throws
     */
    static public void refreshAvatarFromServe(String avatar,ImageView headIcon) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar,headIcon,
                    ImageLoadOptions.getOptions());
        } else {
            headIcon.setImageResource(R.mipmap.mine_avatar);
        }

    }
    static public void refreshAvatarFromLoca(Context context,ImageView headIcon) {
       // if (avatar != null && !avatar.equals("")) {
            //imageLoader.init();
            File saveFile = new File(BmobConstants.MyAvatarDir, "avatarIcon.png");
            if(saveFile.exists())//先从本地获取
            {
                Bitmap bitmap = BitmapFactory.decodeFile(saveFile.getAbsolutePath());
                headIcon.setImageBitmap(bitmap);
            }else//从网络上获取，如果失败显示默认图片
            {
                initDefaultAvatar(context, headIcon);
            }
//            Bitmap bitmap = BitmapFactory.decodeFile(saveFile.getAbsolutePath());
//            headIcon.setImageBitmap(bitmap);
//            ImageLoader.getInstance().displayImage(/*"file://"+*/avatar,headIcon,
//                    ImageLoadOptions.getOptions());
//        } else {
//            headIcon.setImageResource(R.drawable.default_head);
//        }
    }

    static public void initDefaultAvatar(Context context,ImageView headIcon)
    {
     //   String defaultPath= BmobConstants.MyAvatarDir+"avatarIcon.png";
        if(!setUserHeadFromServer(context, headIcon))
        {
            headIcon.setImageResource(R.drawable.default_head);
        }
//        boolean isSetAvatar= (boolean) SPUtils.get(context,"isSetAvatar",false);
//        if(!isSetAvatar)
//
//        else
//        {
//            Log.i("head","get user icon form srver");
//            setUserHeadFromServer(context, headIcon);
////            ImageLoader.getInstance().displayImage("file://"+defaultPath,headIcon,
////                    ImageLoadOptions.getOptions());
//        }

    }

    static  public void autoLogin(Context context, final OnResponseListener listener)
    {
        if(SPUtils.contains(context,"user_name"))
        {
            String name=(String)SPUtils.get(context,"user_name","");
            String pwd=(String)SPUtils.get(context,"pwd","");
            BmobUser user = new BmobUser();
            user.setUsername(name);
            user.setPassword(pwd);
            user.login(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    if(listener!=null)
                    {
                        Response response = new Response();
                        response.setIsSucces(true);
                        listener.onResponse(response);
                    }
                    //  Snackbar.make(loginBtn,"登录成功！",Snackbar.LENGTH_SHORT).show();

                }
                @Override
                public void onFailure(int code, String msg) {
                    if(listener!=null)
                    {
                        Response response = new Response();
                        response.setIsSucces(false);
                        response.setMsg(msg);
                        listener.onResponse(response);
                    }
                }
            });
        }
    }

    static public void getUserNotes(Context context, String user_id,final OnResponseListener listener){
      //  Bmob.initialize(context, Constants.APPLICATION_ID);
        final List<NotebookData> list = new ArrayList<NotebookData>();
        BmobQuery<NotebookData> query = new BmobQuery<NotebookData>();

        query.addWhereEqualTo(NotebookData.NOTE_USER_ID, user_id);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //按更新日期降序排列
        query.order("-updatedAt");
        //执行查询方法
        query.findObjects(context, new FindListener<NotebookData>() {
            @Override
            public void onSuccess(List<NotebookData> object) {
                if (listener != null) {
                    Response response = new Response();
                    response.setIsSucces(true);
                    response.setNoteItemList(object);
                    listener.onResponse(response);
                }
            }

            @Override
            public void onError(int code, String msg) {
                Response response = new Response();
                response.setIsSucces(false);
                response.setMsg(msg);
                listener.onResponse(response);

            }
        });
    }
    /**
     * 更新用户表单中的头像列
     *
     * @param picUrl 图片的url
     */
    private void updateUserHead(Context context, String picUrl, final OnResponseListener listener) {
        String oldUrl = AccountUtils.getUserHeadUrl(context);
        if (!TextUtils.isEmpty(oldUrl) && !oldUrl.equals("")) {
            //删除服务器上的旧头像
            deleteUserOldHead(context, oldUrl);
        }
        AccountUtils.saveUserHeadUrl(context, picUrl);
        User user = new User();
        user.setUserHeadUrl(picUrl);
        user.update(context, AccountUtils.getUserId(context), new UpdateListener() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    Response response = new Response();
                    response.setIsSucces(true);
                    listener.onResponse(response);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    Response response = new Response();
                    response.setIsSucces(false);
                    response.setMsg(msg);
                    listener.onResponse(response);
                }
            }
        });
    }

    /**
     * 删除服务器上的旧头像
     *
     * @param oldUrl 旧头像的url
     */
    private void deleteUserOldHead(Context context, String oldUrl) {
        BmobFile file = new BmobFile();
        file.setUrl(oldUrl);//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        file.delete(context, new DeleteListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int code, String msg) {
            }
        });
    }



    static public boolean setUserHeadFromServer(Context context, ImageView headIcon) {

        String photoUrl = AccountUtils.getUserHeadUrl(context).trim();

       // Toast.makeText(context,photoUrl,Toast.LENGTH_LONG).show();
        if (!TextUtils.isEmpty(photoUrl) && !photoUrl.equals("")) {
            ImageLoader.getInstance().displayImage(photoUrl,headIcon,
                    ImageLoadOptions.getOptions());
            return true;
        }
        return false;//说明用户第一次使用该App或者未修改head
    }

//    static public String  getUserHeadPath()
//    {
//        String path=null;
//        return path;
//    }
}
