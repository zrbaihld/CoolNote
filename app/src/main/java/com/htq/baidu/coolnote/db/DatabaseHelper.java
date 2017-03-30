package com.htq.baidu.coolnote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @author htq 爱丽颖的颖火虫
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COOLNOTE_DATABASE_NAME = "coolnote";

    public static final String NOTE_TABLE_NAME = "htq_Notebook";

    public static final String CREATE_NOTE_TABLE = "create table "
            + NOTE_TABLE_NAME
            + " (_id integer primary key autoincrement, objectid text, iid integer,"
            + " time varchar(10), date varchar(10), content text, color integer, classify text, level integer, father text, path text, title text)";

    public static final String NEWS_LIST = "osc_news_list";

    public static final String CREATE_NEWS_LIST_TABLE = "create table "
            + NOTE_TABLE_NAME + "(" + "_id integer primary key autoincrement, "
            + "news_id interger, title varchar(10), " + ")";

    public DatabaseHelper(Context context) {
        super(context, COOLNOTE_DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE_TABLE);
        // db.execSQL(CREATE_NEWS_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}