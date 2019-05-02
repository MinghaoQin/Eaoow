
package com.minghaoqin.q.eaoow;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.minghaoqin.q.eaoow.util.Constants;

import java.util.List;


@Dao
public interface DaoAccess {

    @Query("SELECT * FROM "+ Constants.TABLE_NAME_NOTE)
    List<PriorityTable> getPt();

    @Query(("SELECT distinct rest_name FROM PriorityTable"))
    List<String> getDistinctRestName();

    @Query("DELETE FROM PriorityTable")
    void deleteAll();

    @Query("UPDATE  PriorityTable SET priority = priority + 2 Where rest_name =  :newName")
    void updatePriorityAfterYes(String newName);

    @Query("UPDATE PriorityTable SET priority = priority - 3 Where rest_name =  :newName2")
    void updatePriorityAfterNo(String newName2);

    @Query(("SELECT distinct rest_name FROM PriorityTable WHERE priority > 0 ORDER BY priority DESC "))
    List<String> getRestNamePriorityGreaterZero();

    @Insert
    long insert (PriorityTable pt);



}


