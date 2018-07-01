package com.udacity.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.udacity.journalapp.Database.AppDatabase;
import com.udacity.journalapp.Database.AppExecutors;
import com.udacity.journalapp.Database.DiaryEntry;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * Created by Elsi on 30/06/2018.
 */

public class DiaryMainActivity extends AppCompatActivity implements DiaryCustomAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = DiaryMainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private DiaryCustomAdapter mAdapter;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_diary);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.rv_diary);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new DiaryCustomAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);


        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<DiaryEntry> diary = mAdapter.getDiary();
                        mDb.diaryDao().deleteDiary(diary.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddDiaryActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddDiaryActivity
                Intent addTaskIntent = new Intent(DiaryMainActivity.this, AddDiaryActivity.class);
                startActivity(addTaskIntent);
            }
        });

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    // COMPLETED (8) This method is not retrieving the diaries any more. Refactor to a more suitable name such as setupViewModel
    private void setupViewModel() {
        // COMPLETED (5) Remove the logging and the call to loadAllDiary, this is done in the ViewModel now
        // COMPLETED (6) Declare a ViewModel variable and initialize it by calling ViewModelProviders.of
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // COMPLETED (7) Observe the LiveData object in the ViewModel
        viewModel.getmDiary().observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(@Nullable List<DiaryEntry> diaryEntries) {
                Log.d(TAG, "Updating list of diaries from LiveData in ViewModel");
                mAdapter.setDiary(diaryEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddDiaryActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(DiaryMainActivity.this, AddDiaryActivity.class);
        intent.putExtra(AddDiaryActivity.EXTRA_DIARY_ID, itemId);
        startActivity(intent);
    }

    /**
     * Methods for setting up the menu
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the visualizer_menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            // [START signin]
            signOut();
        } else if (id == R.id.action_disconnect) {
            // [START signin]
            revokeAccess();
        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(DiaryMainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(DiaryMainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

}