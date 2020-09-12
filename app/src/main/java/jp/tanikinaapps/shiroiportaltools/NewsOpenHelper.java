package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewsOpenHelper extends SQLiteOpenHelper {
    //データベースのバージョン
    private static final int DATABASE_VERSION = 1;

    //データベース名
    private static final String DATABASE_NAME = "NewsDB.db";
    private static final String TABLE_NAME = "newsdb";
    private static final String _ID = "_id";
    private static final String COLUMN_PAGE_TITLE = "pagetitle";
    private static final String COLUMN_PAGE_UPDATE = "pageupdate";
    private static final String COLUMN_PAGE_IS_CHECKED = "pageischecked";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_PAGE_TITLE + " TEXT," +
                    COLUMN_PAGE_UPDATE + " TEXT," +
                    COLUMN_PAGE_IS_CHECKED + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    NewsOpenHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db,int oldVersion,int newVersion){
        onUpgrade(db,oldVersion,newVersion);
    }
}
