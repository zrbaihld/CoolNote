package com.htq.baidu.coolnote.entity;


import android.content.Context;

import com.htq.baidu.coolnote.db.NoteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class NotebookData extends BmobObject implements /*Serializable,*/
        Comparable<NotebookData> {


    private int id;
    private int iid;

    private String userId;//用于服务器端存储需要

    private String unixTime;//

    private String date;//日期

    private String Classified;//分类

    private String content;//内容

    private String colorText;//字体颜色
    private String father;//父分类
    private String imgpath;//图片地址
    private String title;//标题

    private int color;
    private int level;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public static final String NOTE_USER_ID = "userId";

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        } else {
            if (o instanceof NotebookData) {
                NotebookData data = (NotebookData) o;
                try {
                    return (this.id == data.getId())
                            && (this.iid == data.getIid())
                            && (this.unixTime == data.getUnixTime())
                            && (this.date.equals(data.getDate()))
                            && (this.content == data.getContent())
                            && (this.color == data.getColor());
                } catch (NullPointerException e) {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

public void postNoteToServer(Context context,final OnResponseListener listener)
{
 //   for(int i=0;i<data.size();i++) {
        save(context, new SaveListener() {
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
 //   }
}

    /**
     *
     * @param context
     * @param listener
     */
    public void updateNoteInServe(Context context,String objectId,final OnResponseListener listener)
    {
        //objectId,
          update(context, new UpdateListener() {
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
    public void deleteNoteInServe(Context context,/*int noteId,*/final OnResponseListener listener)
    {
       // String objectId=new NoteDatabase().query();

        delete(context, new DeleteListener() {
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
    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getIid() {
        return iid;
    }

    public void setIid(int iid) {
        this.iid = iid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(String time) {
        this.unixTime = time;
      //  setServerUpdateTime(time);
    }

    public String getClassified() {
        return Classified;
    }

    public void setClassified(String classified) {
        Classified = classified;
    }

    public String getColorText() {
        return colorText;
    }

    public void setColorText(String color) {
        this.colorText = color;
    }

    public int getColor() {
        // 客户端始终以当前手机上的颜色为准
        if ("blue".equals(colorText)) {
            this.color = 3;
        } else if ("red".equals(colorText)) {
            this.color = 2;
        } else if ("yellow".equals(colorText)) {
            this.color = 1;
        } else if ("purple".equals(colorText)) {
            this.color = 4;
        } else if ("green".equals(colorText)) {
            this.color = 0;
        }
        return color;
    }

//    public String getServerUpdateTime() {
//        return serverUpdateTime;
//    }
//
//    public void setServerUpdateTime(String serverUpdateTime) {
//        this.serverUpdateTime = serverUpdateTime;
//    }

    public void setColor(int color) {
        switch (color) {
        case 0:
            colorText = "green";
            break;
        case 1:
            colorText = "yellow";
            break;
        case 2:
            colorText = "red";
            break;
        case 3:
            colorText = "blue";
            break;
        case 4:
            colorText = "purple";
            break;
        default:
            this.color = color;
            break;
        }
    }

    @Override
    public int compareTo(NotebookData another) {
        return this.iid - another.getIid();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}