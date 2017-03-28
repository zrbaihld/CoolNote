package com.htq.baidu.coolnote.entity;

/**
 * 配合EvetnBus使用
 */
public class UpdataEvent {
    public static final int UPDATE_NOTES = 0;
    public static final int UPDATE_DIARIES = 1;
    public static final int UPDATE_USER_INFOS = 2;
    private int type;
    private String content;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
    public void setString(String str)
    {
        this.content=str;
    }
    public String getString()
    {
        return  content;
    }

}
