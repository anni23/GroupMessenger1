package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;
import android.util.Log;

public class DBProvider extends SQLiteOpenHelper
{
    public static final String DatabaseName="GroupMessengerNew1";
    public static final String TableName="MessegesNew1";
    public static final String ColName1="key";
    public static final String ColName2="value";
    public static final String CreateTableQuery="create table "+TableName+"("+ColName1+" varchar(50) primary key, "+ColName2+" varchar(50) not null);";
    SQLiteDatabase d;
    public DBProvider(Context ctx)throws SQLException
    {
        super(ctx,DatabaseName,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db)throws SQLException
    {
        db.execSQL(CreateTableQuery);
        Log.i("db provider","reached1");
    }

    public void insertIntoDb(ContentValues cv)throws SQLException
    {
        try
        {


            d = getWritableDatabase();
            long l = d.insertWithOnConflict(TableName, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
            if (l == -1)
                Log.i("DbProvider", "error occured");
            else
                Log.i("DbProvider", "done");
            //String x=cv.get("1").toString();
            //Log.i("DbProvider",x);
            Log.i("db provider","reached2");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public Cursor queryDb(String sel)throws SQLException
    {
        Cursor c;
        String s[]={ColName2};
        d=getReadableDatabase();
        c=d.query(TableName,null,ColName1+"='"+sel+"'",null,null,null,null,null);
        c.moveToFirst();
        //c=d.query(TableName,null,null,null,null,null,null,null);
        return c;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
