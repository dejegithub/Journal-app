package com.udacity.journalapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.journalapp.Database.AppDatabase;
import com.udacity.journalapp.Database.DiaryEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<DiaryEntry>> mDiary;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase mDb = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        mDiary = mDb.diaryDao().loadAllDiaries();

    }

    public LiveData<List<DiaryEntry>> getmDiary() {
        return mDiary;
    }


}