package com.htq.baidu.coolnote.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.htq.baidu.coolnote.entity.NotebookData;
import com.htq.baidu.coolnote.utils.AccountUtils;
import com.htq.baidu.coolnote.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NoteDatabase {
    private final DatabaseHelper dbHelper;

    public NoteDatabase(Context context) {
        super();
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 增
     *
     * @param data
     */
    public void insert(NotebookData data) {
        String sql = "insert into " + DatabaseHelper.NOTE_TABLE_NAME;

        sql += "(_id, objectid, iid, time, date, content, color, classify, level) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[]{data.getId() + "",
                data.getIid() + "", data.getObjectId(), data.getUnixTime() + "", data.getDate(),
                data.getContent(), data.getColor() + "", data.getClassified()});
        sqlite.close();
    }

    /**
     * 增
     *
     * @param data
     */
    public void insertTable(NotebookData data) {
        String sql = "insert into " + DatabaseHelper.NOTE_TABLE_NAME;

        sql += "(_id, objectid, iid, time, date, content, color, classify, level) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[]{data.getId() + "",
                data.getIid() + "", data.getObjectId(), data.getUnixTime() + "", data.getDate(),
                data.getContent(), data.getColor() + "", data.getClassified()});
        sqlite.close();
    }

    /**
     * 删
     *
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + DatabaseHelper.NOTE_TABLE_NAME + " where _id=?");
        sqlite.execSQL(sql, new Integer[]{id});
        sqlite.close();
    }

    /**
     * 改
     *
     * @param data
     */
    public void update(NotebookData data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + DatabaseHelper.NOTE_TABLE_NAME + " set iid=?, objectid=?, time=?, date=?, content=?, color=?, classify=?, level=? where _id=?");
        sqlite.execSQL(sql,
                new String[]{data.getIid() + "", data.getObjectId() + "", data.getUnixTime() + "",
                        data.getDate(), data.getContent(),
                        data.getColor() + "", data.getClassified(), data.getLevel() + "", data.getId() + ""});
        sqlite.close();
    }

    public List<NotebookData> query(int level) {
        if (level < 0) {
            return query(" ");
        } else {
            return query(" where level=" + level);
        }

    }

    /**
     * 查
     *
     * @param where
     * @return
     */
    public List<NotebookData> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<NotebookData> data = null;
        data = new ArrayList<NotebookData>();
        Cursor cursor = sqlite.rawQuery("select * from "
                + DatabaseHelper.NOTE_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            NotebookData notebookData = new NotebookData();
            notebookData.setId(cursor.getInt(0));
            notebookData.setObjectId(cursor.getString(1));
            notebookData.setIid(cursor.getInt(2));
            notebookData.setUnixTime(cursor.getString(3));
            notebookData.setDate(cursor.getString(4));
            notebookData.setContent(cursor.getString(5));
            notebookData.setColor(cursor.getInt(6));
            notebookData.setClassified(cursor.getString(7));
            notebookData.setLevel(cursor.getInt(8));
            data.add(notebookData);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return data;
    }

    /**
     * 重置
     *
     * @param datas
     */
    public void reset(List<NotebookData> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + DatabaseHelper.NOTE_TABLE_NAME);
            // 重新添加
            for (NotebookData data : datas) {
                insert(data);
            }
            sqlite.close();
        }
    }

    /**
     * 保存一条数据到本地(若已存在则直接覆盖)
     *
     * @param data
     */
    public void save(NotebookData data) {
        List<NotebookData> datas = query(" where _id=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            insert(data);
        }
    }

    //
    // /**
    // * 合并一条数据到本地(通过更新时间判断仅保留最新)
    // *
    // * @param data
    // * @return 数据是否被合并了
    // */
    // public boolean merge(NotebookData data) {
    // Cursor cursor = sqlite.rawQuery(
    // "select * from " + DatabaseHelper.NOTE_TABLE_NAME
    // + " where _id=" + data.getId(), null);
    // NotebookData localData = new NotebookData();
    // // 本循环其实只执行一次
    // for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
    // localData.setId(cursor.getInt(0));
    // localData.setIid(cursor.getInt(1));
    // localData.setUnixTime(cursor.getString(2));
    // localData.setDate(cursor.getString(3));
    // localData.setContent(cursor.getString(4));
    // localData.setColor(cursor.getInt(5));
    // }
    // // 是否需要合这条数据
    // boolean isMerge = localData.getUnixTime() < data.getUnixTime();
    // if (isMerge) {
    // save(data);
    // }
    // return isMerge;
    // }

    public void destroy() {
        dbHelper.close();
    }

    public void insertIntroduce(Context context) {
        NotebookData editData = new NotebookData();
        if (editData.getId() == 0) {
            editData.setId(-1
                    * StringUtils.toInt(
                    StringUtils.getDataTime("dddHHmmss"), 0));
        }
        editData.setUnixTime(StringUtils.getDataTime("yyyy-MM-dd HH:mm:ss"));
        editData.setContent("欢迎使用颖火虫记事本，赶快记下你此刻的灵感吧！");
        editData.setUserId(AccountUtils.getUserId(context));
        save(editData);
    }
}