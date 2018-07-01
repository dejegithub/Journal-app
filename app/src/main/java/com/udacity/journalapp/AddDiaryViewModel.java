package com.udacity.journalapp;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.journalapp.Database.AppDatabase;
import com.udacity.journalapp.Database.DiaryEntry;

/**
 * Created by Elsi on 30/06/2018.
 */

public class AddDiaryViewModel extends ViewModel {
    //Constant for Logging
    private static final String TAG = AndroidViewModel.class.getSimpleName();

    private LiveData<DiaryEntry> mDiary;

    // Note: The constructor should receive the database and the diaryId
    public AddDiaryViewModel(AppDatabase database, int diaryId) {
        mDiary = database.diaryDao().loadDiaryById(diaryId);
    }

    public LiveData<DiaryEntry> getDiary() {
        return mDiary;
    }
}
