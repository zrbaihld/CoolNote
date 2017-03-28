package com.htq.baidu.coolnote.utils;

import android.content.Context;
import android.text.TextUtils;

import com.htq.baidu.coolnote.entity.User;


/**
 * 用户信息管理类
 */
public class AccountUtils {

    private static final String USER_NAME = "user_name";
    private static final String USER_PWD = "user_pwd";
    private static final String USER_ID = "user_id";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_NICK_NAME = "user_nickname";
    private static final String USER_NOTE_PWD = "user_note_pwd";
    private static final String USER_SEX = "user_sex";
    private static final String USER_HEAD_URL = "user_head_url";

    /**
     * 保存用户的所有信息
     * @param context
     * @param user
     * @param user_pwd 用户的登录密码
     */
    public static void saveUserInfos(Context context, User user, String user_pwd) {
        saveUserName(context, user.getUsername());
        saveUserPwd(context, user_pwd);
        saveUserId(context, user.getObjectId());
        saveUserNickName(context, user.getUserNickname());
        saveUserEmail(context, user.getEmail());
        saveUserDiaryPwd(context, user.getUserNotePwd());
        saveUserSex(context, user.getUserSex());
        saveUserHeadUrl(context, user.getUserHeadUrl());
    }


    public static String getUserName(Context context) {
        return (String) SPUtils.get(context, USER_NAME, "");
    }

    public static void saveUserName(Context context, String str) {
        SPUtils.put(context, USER_NAME, str);
    }

    public static String getUserPwd(Context context) {
        return (String) SPUtils.get(context, USER_PWD, "");
    }

    public static void saveUserPwd(Context context, String str) {
        SPUtils.put(context, USER_PWD, str);
    }

    public static String getUserId(Context context) {
        return (String) SPUtils.get(context, USER_ID, "");
    }

    public static void saveUserId(Context context, String str) {
        SPUtils.put(context, USER_ID, str);
    }

    public static String getUserEmail(Context context) {
        return (String) SPUtils.get(context, USER_EMAIL, "");
    }

    public static void saveUserEmail(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            SPUtils.put(context, USER_EMAIL, str);
        }
    }

    public static String getUserNickName(Context context) {
        return (String) SPUtils.get(context, USER_NICK_NAME, "");
    }

    public static void saveUserNickName(Context context, String str) {
        SPUtils.put(context, USER_NICK_NAME, str);
    }

    public static String getUserDiaryPwd(Context context) {
        return (String) SPUtils.get(context, USER_NOTE_PWD, "");
    }

    public static void saveUserDiaryPwd(Context context, String str) {
        SPUtils.put(context, USER_NOTE_PWD, str);
    }

    public static String getUserSex(Context context) {
        return (String) SPUtils.get(context, USER_SEX, "");
    }

    public static void saveUserSex(Context context, String str) {
        SPUtils.put(context, USER_SEX, str);
    }

    public static String getUserHeadUrl(Context context){
        return (String) SPUtils.get(context, USER_HEAD_URL, "");
    }


    public static void saveUserHeadUrl(Context context, String str) {
        SPUtils.put(context, USER_HEAD_URL, str);
    }
    public static void clearAllInfos(Context context){
        SPUtils.clear(context);
    }
}
