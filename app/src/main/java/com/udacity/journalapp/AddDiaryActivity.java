package com.udacity.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.udacity.journalapp.Database.AppDatabase;
import com.udacity.journalapp.Database.AppExecutors;
import com.udacity.journalapp.Database.DiaryEntry;

import java.util.Date;

/**
 * Created by Elsi on 30/06/2018.
 */

public class AddDiaryActivity extends AppCompatActivity {

    // Extra for the diary ID to be received in the intent
    public static final String EXTRA_DIARY_ID = "extraDiaryId";
    // Extra for the diary ID to be received after rotation
    public static final String INSTANCE_DIARY_ID = "instanceDiaryId";

    // Constant for default diary id to be used when not in update mode
    private static final int DEFAULT_DIARY_ID = -1;
    // Constant for logging
    private static final String TAG = AddDiaryActivity.class.getSimpleName();
    // Fields for views
    EditText mEditText, mEditTitle;

    Button mButton;

    private int mDiaryId = DEFAULT_DIARY_ID;

    // Member variable for the Database
    private AppDatabase mDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_DIARY_ID)) {
            mDiaryId = savedInstanceState.getInt(INSTANCE_DIARY_ID, DEFAULT_DIARY_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_DIARY_ID)) {
            mButton.setText(R.string.update_button);
            if (mDiaryId == DEFAULT_DIARY_ID) {
                // populate the UI
                mDiaryId = intent.getIntExtra(EXTRA_DIARY_ID, DEFAULT_DIARY_ID);

                Log.d(TAG, "Actively retrieving a specific diary from the DataBase");
                final LiveData<DiaryEntry> diary = mDb.diaryDao().loadDiaryById(mDiaryId);
                AddDiaryViewModelFactory factory = new AddDiaryViewModelFactory(mDb, mDiaryId);
                final AddDiaryViewModel viewModel = ViewModelProviders.of(this, factory).get(AddDiaryViewModel.class);

                viewModel.getDiary().observe(this, new Observer<DiaryEntry>() {
                    @Override
                    public void onChanged(@Nullable DiaryEntry diaryEntry) {
                        viewModel.getDiary().removeObserver(this);
                        populateUI(diaryEntry);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_DIARY_ID, mDiaryId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditText = findViewById(R.id.editdescription);
        mEditTitle = findViewById(R.id.editTitle);

        mButton = findViewById(R.id.buttonsave);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param diary the diaryEntry to populate the UI
     */
    private void populateUI(DiaryEntry diary) {
        if (diary == null) {
            return;
        }

        mEditText.setText(diary.getDescription());
        mEditTitle.setText(diary.getTitle());
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new diary data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String description = mEditText.getText().toString();
        String title = mEditTitle.getText().toString();
        Date date = new Date();
        if (description.length() != 0 && title.length() != 0) {

            final DiaryEntry diary = new DiaryEntry(description, title, date);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mDiaryId == DEFAULT_DIARY_ID) {
                        // insert new diary
                        mDb.diaryDao().insertDiary(diary);
                    } else {
                        //update diary
                        diary.setId(mDiaryId);
                        mDb.diaryDao().updateDiary(diary);
                    }
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "Please fill the required form", Toast.LENGTH_SHORT).show();
        }
    }

    //apply action on back button

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();

    }
}
