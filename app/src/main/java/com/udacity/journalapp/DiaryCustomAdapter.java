package com.udacity.journalapp;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.journalapp.Database.DiaryEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Elsi on 30/06/2018.
 */

/**
 * This DiaryCustomAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class DiaryCustomAdapter extends RecyclerView.Adapter<DiaryCustomAdapter.DiaryViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<DiaryEntry> mDiaries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the DiaryCustomAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public DiaryCustomAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new DiaryViewHolder that holds the view for each task
     */
    @Override
    public DiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.display_single_diary, parent, false);

        return new DiaryViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(DiaryViewHolder holder, int position) {
        // Determine the values of the wanted data
        DiaryEntry diaryEntry = mDiaries.get(position);
        String description = diaryEntry.getDescription();
        String title = diaryEntry.getTitle();
        String updatedAt = dateFormat.format(diaryEntry.getUpdatedAt());

        //Set values
        holder.diaryDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);
        holder.titleView.setText(title);
    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mDiaries == null) {
            return 0;
        }
        return mDiaries.size();
    }

    public List<DiaryEntry> getDiary() {
        return mDiaries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setDiary(List<DiaryEntry> diaryEntries) {
        mDiaries = diaryEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class DiaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView diaryDescriptionView;
        TextView updatedAtView;
        TextView titleView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public DiaryViewHolder(View itemView) {
            super(itemView);

            diaryDescriptionView = itemView.findViewById(R.id.textDescription);
            updatedAtView = itemView.findViewById(R.id.textTime);
            titleView = itemView.findViewById(R.id.textTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mDiaries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

}
