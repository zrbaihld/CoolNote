package com.htq.baidu.coolnote.entity;

import android.graphics.Bitmap;

import java.util.List;

/**
 * 服务器响应类
 *
 */
public class Response {

    private String msg;
    private boolean isSucces=true;

    private List<NotebookData> noteItemList;
 //   private List<DiaryItem> diaryItemList;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSucces() {
        return isSucces;
    }

    public void setIsSucces(boolean isSucces) {
        this.isSucces = isSucces;
    }

    public List<NotebookData> getNoteItemList() {
        return noteItemList;
    }

    public void setNoteItemList(List<NotebookData> noteItemList) {
        this.noteItemList = noteItemList;
    }

//    public List<DiaryItem> getDiaryItemList() {
//        return diaryItemList;
//    }
//
//    public void setDiaryItemList(List<DiaryItem> diaryItemList) {
//        this.diaryItemList = diaryItemList;
//    }
}
