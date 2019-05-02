
package com.minghaoqin.q.eaoow;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {PriorityTable.class}, version = 1)
public abstract class RestNameDatabase extends RoomDatabase {


    public abstract DaoAccess getDaoAccess() ;


    private static RestNameDatabase restNameDB;

    public static synchronized RestNameDatabase getInstance(Context context) {
        if (restNameDB == null) {
            restNameDB = buildDatabaseInstance(context);
        }
        return restNameDB;
    }

    private static RestNameDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                RestNameDatabase.class,
                "PriorityTable").build();
    }

    public  void cleanUp(){
        restNameDB = null;
    }
}





