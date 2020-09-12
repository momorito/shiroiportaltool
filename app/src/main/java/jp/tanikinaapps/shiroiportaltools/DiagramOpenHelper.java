package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiagramOpenHelper extends SQLiteOpenHelper {
    //データベースのバージョン
    private static final int DATABASE_VERSION = 1;

    //データベース名
    private static final String DATABASE_NAME = "DiagramDB.db";
    private static final String TABLE_NAME = "diadb";
    private static final String _ID = "_id";
    private static final String COLUMN_START_POINT = "startpoint";
    private static final String COLUMN_START_HOUR = "starthour";
    private static final String COLUMN_START_MINUTE = "startminute";
    private static final String COLUMN_ARRIVE_POINT = "arrivepoint";
    private static final String COLUMN_ARRIVE_HOUR = "arrivehour";
    private static final String COLUMN_ARRIVE_MINUTE = "arriveminute";
    private static final String COLUMN_ROOT= "root";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_START_POINT+ " TEXT," +
                    COLUMN_START_HOUR+ " INTEGER," +
                    COLUMN_START_MINUTE+ " INTEGER," +
                    COLUMN_ARRIVE_POINT + " TEXT," +
                    COLUMN_ARRIVE_HOUR+ " INTEGER," +
                    COLUMN_ARRIVE_MINUTE+ " INTEGER,"+
                    COLUMN_ROOT+" TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    DiagramOpenHelper(Context context){
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
