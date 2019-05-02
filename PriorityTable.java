
package com.minghaoqin.q.eaoow;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.RoomDatabase;

import android.support.annotation.NonNull;

import com.minghaoqin.q.eaoow.util.Constants;

import java.io.Serializable;


@Entity (tableName = Constants.TABLE_NAME_NOTE)
public class PriorityTable implements Serializable {


    @PrimaryKey (autoGenerate = true)
    private long index;


    @ColumnInfo(name = "rest_name")
    private String restName;

    @ColumnInfo(name = "priority")
    private int priority;




    public PriorityTable(String restName, int priority) {
        this.restName = restName;
        this.priority = priority;


    }



    public long getIndex (){
        return index;
    }

    public void setIndex(long index){
        this.index = index;
    }

    public  String getRestName() {
        return restName;
    }

    public void setRestName(String RestName) {
        this.restName = RestName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    @Override
    public String toString() {
        return "PriorityTable{" +
                "index=" + index +
                ", Restaurant Name ='" + restName + '\'' +
                ", Priority='" + priority + '\'' +
                '}';
    }


}



