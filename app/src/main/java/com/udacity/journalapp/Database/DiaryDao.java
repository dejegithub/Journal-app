package com.udacity.journalapp.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Elsi on 30/06/2018.
 */

@Dao
public interface DiaryDao {

    @Query("SELECT * FROM diary ORDER BY title")
    LiveData<List<DiaryEntry>> loadAllDiaries();

    @Insert
    void insertDiary(DiaryEntry diaryEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDiary(DiaryEntry diaryEntry);

    @Delete
    void deleteDiary(DiaryEntry diaryEntry);

    @Query("SELECT * FROM diary WHERE id = :id")
    LiveData<DiaryEntry> loadDiaryById(int id);
}