package com.udacity.journalapp;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.udacity.journalapp.Database.AppDatabase;

/**
 * Created by Elsi on 30/06/2018.
 */

public class AddDiaryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mDiaryId;

    public AddDiaryViewModelFactory(AppDatabase db, int diaryId) {

        this.mDb = db;
        this.mDiaryId = diaryId;
    }

    // Note: This can be reused with minor modifications
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddDiaryViewModel(mDb, mDiaryId);
    }
}
